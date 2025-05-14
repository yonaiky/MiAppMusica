package it.fast4x.rimusic.service.modern

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import com.google.gson.Gson
import com.grack.nanojson.JsonObject
import io.ktor.client.statement.bodyAsText
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.Innertube.createPoTokenChallenge
import it.fast4x.innertube.models.PlayerResponse
import it.fast4x.innertube.models.bodies.PlayerBody
import it.fast4x.innertube.requests.playerAdvanced
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.enums.AudioQualityFormat
import it.fast4x.rimusic.extensions.webpotoken.advancedPoTokenPlayer
import it.fast4x.rimusic.isConnectionMeteredEnabled
import it.fast4x.rimusic.models.Format
import it.fast4x.rimusic.service.LoginRequiredException
import it.fast4x.rimusic.service.NoInternetException
import it.fast4x.rimusic.service.TimeoutException
import it.fast4x.rimusic.service.UnknownException
import it.fast4x.rimusic.service.UnplayableException
import it.fast4x.rimusic.ui.screens.settings.isYouTubeLoggedIn
import it.fast4x.rimusic.ui.screens.settings.isYouTubeLoginEnabled
import it.fast4x.rimusic.useYtLoginOnlyForBrowse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.services.youtube.PoTokenResult
import org.schabi.newpipe.extractor.services.youtube.YoutubeJavaScriptPlayerManager
import org.schabi.newpipe.extractor.services.youtube.YoutubeStreamHelper
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

const val CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
fun randomString(length: Int): String = (1..length).map { CHARS.random() }.joinToString("")

private val jsonParser =
    Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        useArrayPolymorphism = true
        explicitNulls = false
    }

@OptIn(UnstableApi::class)
internal suspend fun PlayerServiceModern.dataSpecProcess(
    dataSpec: DataSpec,
    context: Context,
    connectionMetered: Boolean = false
): DataSpec {
    val songUri = dataSpec.uri.toString()
    val videoId = songUri.substringAfter("watch?v=")
    val chunkLength = 512 * 1024L
    val length = if (dataSpec.length >= 0) dataSpec.length else 1

    println("PlayerServiceModern DataSpecProcess Playing song ${videoId} dataSpec position ${dataSpec.position} length ${dataSpec.length}")
    if( dataSpec.isLocal ||
        cache.isCached(videoId, dataSpec.position, chunkLength) ||
        downloadCache.isCached(videoId, dataSpec.position, length)
    ) {
        println("PlayerServiceModern DataSpecProcess Playing song ${videoId} from cached or local file")
        return dataSpec //.withUri(Uri.parse(dataSpec.uri.toString()))
    }

    val formatUrl =
        try {
            getAndroidReelFormatUrl( videoId, audioQualityFormat, connectionMetered )
        } catch ( e: Exception ) {
            when( e ) {
                is LoginRequiredException,
                is UnplayableException -> getIosFormatUrl( videoId, audioQualityFormat, connectionMetered )
                else -> throw e
            }
        }

    return dataSpec.withUri( formatUrl ).subrange( dataSpec.uriPositionOffset )
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

@UnstableApi
private fun getFormatUrl(
    videoId: String,
    cpn: String,
    responseJson: JsonObject,
    audioQualityFormat: AudioQualityFormat,
    connectionMetered: Boolean,
): Uri {
    val jsonString = Gson().toJson( responseJson )
    val playerResponse = jsonParser.decodeFromString<PlayerResponse>( jsonString )

    checkPlayability( playerResponse.playabilityStatus )

    val format = extractFormat( playerResponse.streamingData, audioQualityFormat, connectionMetered )
    return YoutubeJavaScriptPlayerManager.getUrlWithThrottlingParameterDeobfuscated( videoId, format?.url.orEmpty() )
                                         .toUri()
                                         .buildUpon()
                                         .appendQueryParameter( "range", "0-${format?.contentLength ?: 1_000_000}" )
                                         .appendQueryParameter( "cpn", cpn )
                                         .build()
}

@UnstableApi
fun getAndroidReelFormatUrl(
    videoId: String,
    audioQualityFormat: AudioQualityFormat,
    connectionMetered: Boolean
): Uri {
    val cpn = randomString( 16 )
    val response = YoutubeStreamHelper.getAndroidReelPlayerResponse( ContentCountry.DEFAULT, Localization.DEFAULT, videoId, cpn )
    return getFormatUrl( videoId, cpn, response, audioQualityFormat, connectionMetered )
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

@UnstableApi
suspend fun getIosFormatUrl(
    videoId: String,
    audioQualityFormat: AudioQualityFormat,
    connectionMetered: Boolean
): Uri {
    val cpn = randomString( 16 )
    val (_, visitorData, _) = Innertube.getVisitorData( videoId, null )
    val playerRequestToken = generateIosPoToken().orEmpty()
    val poTokenResult = PoTokenResult(visitorData, playerRequestToken, null )
    val response = YoutubeStreamHelper.getIosPlayerResponse( ContentCountry.DEFAULT, Localization.DEFAULT, videoId, cpn, poTokenResult )
    return getFormatUrl( videoId, cpn, response, audioQualityFormat, connectionMetered )
}

@OptIn(UnstableApi::class)
suspend fun getAvancedInnerTubeStream(
    videoId: String,
    audioQualityFormat: AudioQualityFormat,
    connectionMetered: Boolean
): PlayerResponse.StreamingData.Format? {
    return advancedPoTokenPlayer(
        body = PlayerBody(videoId = videoId),
    ).fold(
        { playerResponse ->
            println("PlayerServiceModern MyDownloadHelper DataSpecProcess getInnerTubeStream playabilityStatus ${playerResponse.second?.playabilityStatus?.status} for song $videoId from adaptiveFormats itag ${playerResponse.second?.streamingData?.adaptiveFormats?.map { it.itag }}")
            when(playerResponse.second?.playabilityStatus?.status) {
                "OK" -> {
                    // SELECT FORMAT BY ITAG
                    when (audioQualityFormat) {
                        AudioQualityFormat.Auto -> if (connectionMetered && isConnectionMeteredEnabled()) playerResponse.second?.streamingData?.mediumQualityFormat
                        else playerResponse.second?.streamingData?.autoMaxQualityFormat
                        AudioQualityFormat.High -> playerResponse.second?.streamingData?.highestQualityFormat
                        AudioQualityFormat.Medium -> playerResponse.second?.streamingData?.mediumQualityFormat
                        AudioQualityFormat.Low -> playerResponse.second?.streamingData?.lowestQualityFormat
                    }
                    .let {
                        if (playerResponse.first != null) {
                            it?.copy(url = it.url?.plus("&cpn=${playerResponse.first}&range=0-${it.contentLength ?: 10000000}"))
                        } else {
                            it?.copy(url = it.url?.plus("&range=0-${it.contentLength ?: 10000000}"))
                        }
                    }
                    .let {
                        if (playerResponse.third != null)
                            it?.copy(url = it.url?.plus("&pot=${playerResponse.third}"))
                        else it
                    }
                    .also {
                        println("PlayerServiceModern MyDownloadHelper DataSpecProcess getAdvancedInnerTubeStream url ${it?.url}")

                        println("PlayerServiceModern MyDownloadHelper DataSpecProcess getInnerTubeStream song $videoId itag selected ${it}")
                        Database.asyncTransaction {
                            val isVideoExist = runBlocking {
                                songTable.exists( videoId ).first()
                            }
                            if( !isVideoExist ) return@asyncTransaction

                            formatTable.upsert(
                                Format(
                                    songId = videoId,
                                    itag = it?.itag?.toInt(),
                                    mimeType = it?.mimeType,
                                    contentLength = it?.contentLength,
                                    bitrate = it?.bitrate?.toLong(),
                                    lastModified = it?.lastModified,
                                    loudnessDb = it?.loudnessDb?.toFloat()
                                )
                            )
                        }
                    }
                }
                "LOGIN_REQUIRED" -> throw LoginRequiredException()
                "UNPLAYABLE" -> throw UnplayableException()
                else -> throw UnknownException()
            }
        },
        { throwable ->
            when (throwable) {
                is ConnectException, is UnknownHostException -> {
                    throw NoInternetException()
                }

                is SocketTimeoutException -> {
                    throw TimeoutException()
                }

                else -> {
                    println("PlayerServiceModern MyDownloadHelper DataSpecProcess Error: ${throwable.stackTraceToString()}")
                    throw throwable
                }
            }

        }
    )
}

@OptIn(UnstableApi::class)
suspend fun getInnerTubeStream(
    videoId: String,
    audioQualityFormat: AudioQualityFormat,
    connectionMetered: Boolean
): PlayerResponse.StreamingData.Format? {
    return Innertube.playerAdvanced(
        body = PlayerBody(videoId = videoId),
        withLogin =  (!useYtLoginOnlyForBrowse() && isYouTubeLoginEnabled() && isYouTubeLoggedIn()),
    ).fold(
        { playerResponse ->
            println("PlayerServiceModern MyDownloadHelper DataSpecProcess getInnerTubeStream playabilityStatus ${playerResponse.second?.playabilityStatus?.status} for song $videoId from adaptiveFormats itag ${playerResponse.second?.streamingData?.adaptiveFormats?.map { it.itag }}")
            when(playerResponse.second?.playabilityStatus?.status) {
                "OK" -> {
                    // SELECT FORMAT BY ITAG
                    when (audioQualityFormat) {
                        AudioQualityFormat.Auto -> if (connectionMetered && isConnectionMeteredEnabled()) playerResponse.second?.streamingData?.mediumQualityFormat
                        else playerResponse.second?.streamingData?.autoMaxQualityFormat
                        AudioQualityFormat.High -> playerResponse.second?.streamingData?.highestQualityFormat
                        AudioQualityFormat.Medium -> playerResponse.second?.streamingData?.mediumQualityFormat
                        AudioQualityFormat.Low -> playerResponse.second?.streamingData?.lowestQualityFormat
                    }
                    .let {
                        if (playerResponse.first != null) {
                            it?.copy(url = it.url?.plus("&cpn=${playerResponse.first}&range=0-${it.contentLength ?: 10000000}"))
                        } else {
                            it?.copy(url = it.url?.plus("&range=0-${it.contentLength ?: 10000000}"))
                        }
                    }
                    .also {
                        println("PlayerServiceModern MyDownloadHelper DataSpecProcess getInnerTubeStream song $videoId itag selected ${it}")
                        Database.asyncTransaction {
                            val isVideoExist = runBlocking {
                                songTable.exists( videoId ).first()
                            }
                            if( !isVideoExist ) return@asyncTransaction

                            formatTable.upsert(
                                Format(
                                    songId = videoId,
                                    itag = it?.itag,
                                    mimeType = it?.mimeType,
                                    contentLength = it?.contentLength,
                                    bitrate = it?.bitrate?.toLong(),
                                    lastModified = it?.lastModified,
                                    loudnessDb = it?.loudnessDb?.toFloat()
                                )
                            )
                        }
                    }
                }
                "LOGIN_REQUIRED" -> throw LoginRequiredException()
                "UNPLAYABLE" -> throw UnplayableException()
                else -> throw UnknownException()
            }
        },
        { throwable ->
            when (throwable) {
                is ConnectException, is UnknownHostException -> {
                    throw NoInternetException()
                }

                is SocketTimeoutException -> {
                    throw TimeoutException()
                }

                else -> {
                    println("PlayerServiceModern MyDownloadHelper DataSpecProcess Error: ${throwable.stackTraceToString()}")
                    throw throwable
                }
            }

        }
    )
}
