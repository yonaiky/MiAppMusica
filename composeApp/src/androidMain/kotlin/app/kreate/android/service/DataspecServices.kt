package app.kreate.android.service

import android.content.ContentResolver
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
import app.kreate.android.R
import app.kreate.android.network.innertube.Store
import app.kreate.android.utils.CharUtils
import app.kreate.android.utils.innertube.CURRENT_LOCALE
import com.grack.nanojson.JsonWriter
import io.ktor.client.statement.bodyAsText
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.Innertube.createPoTokenChallenge
import it.fast4x.innertube.models.PlayerResponse
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.enums.AudioQualityFormat
import it.fast4x.rimusic.isConnectionMeteredEnabled
import it.fast4x.rimusic.models.Format
import it.fast4x.rimusic.service.LoginRequiredException
import it.fast4x.rimusic.service.MyDownloadHelper
import it.fast4x.rimusic.service.UnknownException
import it.fast4x.rimusic.service.UnplayableException
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.utils.isConnectionMetered
import it.fast4x.rimusic.utils.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.knighthat.innertube.Endpoints
import me.knighthat.utils.Toaster
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.services.youtube.PoTokenResult
import org.schabi.newpipe.extractor.services.youtube.YoutubeJavaScriptPlayerManager
import org.schabi.newpipe.extractor.services.youtube.YoutubeStreamHelper
import me.knighthat.innertube.Innertube as NewInnertube

private const val CHUNK_LENGTH = 512 * 1024L     // 512Kb

/**
 * Acts as a lock to keep [upsertSongFormat] from starting before
 * [upsertSongInfo] finishes.
 */
private var databaseWorker: Job = Job()

/**
 * Store id of song just added to the database.
 * This is created to reduce load to Room
 */
@set:Synchronized
private var justInserted: String = ""

/**
 * Reach out to [Endpoints.NEXT] endpoint for song's information.
 *
 * Info includes:
 * - Titles
 * - Artist(s)
 * - Album
 * - Thumbnails
 * - Duration
 *
 * ### If song IS already inside database
 *
 * It'll replace unmodified columns with fetched data
 *
 * ### If song IS NOT already inside database
 *
 * New record will be created and insert into database
 *
 */
private fun upsertSongInfo( videoId: String ) {       // Use this to prevent suspension of thread while waiting for response from YT
    // Skip adding if it's just added in previous call
    if( videoId == justInserted || !isNetworkAvailable( appContext() ) ) return

    databaseWorker = CoroutineScope(Dispatchers.IO ).launch {
        NewInnertube.songBasicInfo( videoId, CURRENT_LOCALE )
                    .onSuccess{ Database.upsert( it ) }
                    .onFailure {
                        it.printStackTrace()

                        val message= it.message ?: appContext().getString( R.string.failed_to_fetch_original_property )
                        Toaster.e( message )
                    }
    }

    // Must not modify [JustInserted] to [upsertSongFormat] let execute later
}

/**
 * Upsert provided format to the database
 */
private fun upsertSongFormat( videoId: String, format: PlayerResponse.StreamingData.Format ) {
    // Skip adding if it's just added in previous call
    if( videoId == justInserted ) return

    CoroutineScope(Dispatchers.IO ).launch {
        // Wait until this job is finish to make sure song's info
        // is in the database before continuing
        databaseWorker.join()

        Database.asyncTransaction {
            formatTable.insertIgnore(Format(
                videoId,
                format.itag,
                format.mimeType,
                format.bitrate.toLong(),
                format.contentLength,
                format.lastModified,
                format.loudnessDb?.toFloat()
            ))

            // Format must be added successfully before setting variable
            justInserted = videoId
        }
    }
}

@UnstableApi
private fun upstreamDatasourceFactory( context: Context ): DataSource.Factory =
    DefaultDataSource.Factory( context, KtorHttpDatasource.Factory(NetworkService.client ) )

//<editor-fold defaultstate="collapsed" desc="Extractors">
private val jsonParser =
    Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        useArrayPolymorphism = true
        explicitNulls = false
    }

@UnstableApi
private fun checkPlayability( playabilityStatus: PlayerResponse.PlayabilityStatus? ) {
    if( playabilityStatus?.status != "OK" )
        when( playabilityStatus?.status ) {
            "LOGIN_REQUIRED"    -> throw LoginRequiredException()
            "UNPLAYABLE"        -> throw UnplayableException()
            else                -> throw UnknownException()
        }
}

private fun extractFormat(
    streamingData: PlayerResponse.StreamingData?,
    audioQualityFormat: AudioQualityFormat,
    connectionMetered: Boolean
): PlayerResponse.StreamingData.Format? =
    when (audioQualityFormat) {
        AudioQualityFormat.High -> streamingData?.highestQualityFormat
        AudioQualityFormat.Medium -> streamingData?.mediumQualityFormat
        AudioQualityFormat.Low -> streamingData?.lowestQualityFormat
        AudioQualityFormat.Auto ->
            if (connectionMetered && isConnectionMeteredEnabled())
                streamingData?.mediumQualityFormat
            else
                streamingData?.autoMaxQualityFormat
    }

fun getAndroidResponse( videoId: String, cpn: String ): PlayerResponse {
    val response = YoutubeStreamHelper.getAndroidReelPlayerResponse( ContentCountry.DEFAULT, Localization.DEFAULT, videoId, cpn )
    return JsonWriter.string( response )
                     .let( jsonParser::decodeFromString )
}

private fun String.getPoToken(): String? =
    this.replace("[", "")
        .replace("]", "")
        .split(",")
        .findLast { it.contains("\"") }
        ?.replace("\"", "")

private suspend fun generateIosPoToken() =
    createPoTokenChallenge().bodyAsText()
        .let { challenge ->
            val listChallenge = jsonParser.decodeFromString<List<String?>>(challenge)
            listChallenge.filterIsInstance<String>().firstOrNull()
        }?.let { poTokenChallenge ->
            Innertube.generatePoToken(poTokenChallenge)
                .bodyAsText()
                .getPoToken()
        }

suspend fun getIosResponse(videoId: String, cpn: String ): PlayerResponse {
    val visitorData = Store.getIosVisitorData()
    val playerRequestToken = generateIosPoToken().orEmpty()
    val poTokenResult = PoTokenResult(visitorData, playerRequestToken, null )
    val response = YoutubeStreamHelper.getIosPlayerResponse( ContentCountry.DEFAULT, Localization.DEFAULT, videoId, cpn, poTokenResult )

    return JsonWriter.string( response )
                     .let( jsonParser::decodeFromString )
}
//</editor-fold>

@UnstableApi
fun DataSpec.process(
    videoId: String,
    audioQualityFormat: AudioQualityFormat,
    connectionMetered: Boolean
): DataSpec = runBlocking( Dispatchers.IO ) {
    val cpn = CharUtils.randomString( 16 )
    val playerResponse =
        try {
            getAndroidResponse( videoId, cpn )
        } catch ( e: Exception ) {
            when( e ) {
                is LoginRequiredException,
                is UnplayableException -> getIosResponse( videoId, cpn )
                else -> throw e
            }
        }

    checkPlayability( playerResponse.playabilityStatus )

    val format = extractFormat(
        playerResponse.streamingData,
        audioQualityFormat,
        connectionMetered
    )

    upsertSongFormat( videoId, format!! )

    YoutubeJavaScriptPlayerManager.getUrlWithThrottlingParameterDeobfuscated( videoId, format.url.orEmpty() )
                                  .toUri()
                                  .buildUpon()
                                  .appendQueryParameter( "range", "$uriPositionOffset-${format.contentLength ?: CHUNK_LENGTH}" )
                                  .appendQueryParameter( "cpn", cpn )
                                  .build()
                                  .let( ::withUri )
                                  .subrange( uriPositionOffset )
}

//<editor-fold defaultstate="collapsed" desc="Data source factories">
@UnstableApi
fun PlayerServiceModern.createDataSourceFactory( context: Context ): DataSource.Factory =
    ResolvingDataSource.Factory(
        CacheDataSource.Factory()
                       .setCache( downloadCache )
                       .setUpstreamDataSourceFactory(
                           CacheDataSource.Factory()
                                          .setCache( cache )
                                          .setUpstreamDataSourceFactory(
                                              upstreamDatasourceFactory( context )
                                          )
                       )
                       .setCacheWriteDataSinkFactory( null )
                       .setFlags( FLAG_IGNORE_CACHE_ON_ERROR )
    ) { dataSpec ->
        Database.asyncTransaction {
            runBlocking( Dispatchers.Main ) {
                player.currentMediaItem
            }?.also( ::insertIgnore )
        }

        val videoId = dataSpec.uri.toString().substringAfter("watch?v=")

        val isLocal = dataSpec.uri.scheme == ContentResolver.SCHEME_CONTENT || dataSpec.uri.scheme == ContentResolver.SCHEME_FILE
        val isCached = cache.isCached( videoId, dataSpec.position, CHUNK_LENGTH )
        val isDownloaded = downloadCache.isCached( videoId, dataSpec.position, CHUNK_LENGTH )

        if( !isLocal )
            upsertSongInfo( videoId )

        return@Factory if( isLocal || isCached || isDownloaded )
            // No need to fetch online for already cached data
            dataSpec
        else
            dataSpec.process( videoId, audioQualityFormat, applicationContext.isConnectionMetered() )
    }

@UnstableApi
fun MyDownloadHelper.createDataSourceFactory( context: Context ): DataSource.Factory =
    ResolvingDataSource.Factory(
        CacheDataSource.Factory()
                       .setCache( getDownloadCache(context) )
                       .apply {
                           setUpstreamDataSourceFactory(
                               upstreamDatasourceFactory( context )
                           )
                           setCacheWriteDataSinkFactory( null )
                       }
    ) { dataSpec: DataSpec ->
        val videoId: String = dataSpec.uri
                                      .toString()
                                      .substringAfter( "watch?v=" )
                                      .also( ::upsertSongInfo )

        val isDownloaded = downloadCache.isCached( videoId, dataSpec.position, CHUNK_LENGTH )

        return@Factory if( isDownloaded )
            // No need to fetch online for already cached data
            dataSpec
        else
            dataSpec.process( videoId, audioQualityFormat, context.isConnectionMetered() )
    }
//</editor-fold>
