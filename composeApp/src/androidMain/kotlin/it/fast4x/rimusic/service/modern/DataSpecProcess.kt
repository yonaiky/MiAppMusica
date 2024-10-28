package it.fast4x.rimusic.service.modern

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.PlayerResponse
import it.fast4x.innertube.models.bodies.PlayerBody
import it.fast4x.innertube.requests.player
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.enums.AudioQualityFormat
import it.fast4x.rimusic.models.Format
import it.fast4x.rimusic.query
import it.fast4x.rimusic.service.LoginRequiredException
import it.fast4x.rimusic.service.NoInternetException
import it.fast4x.rimusic.service.TimeoutException
import it.fast4x.rimusic.service.UnknownException
import it.fast4x.rimusic.service.UnplayableException
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.utils.enableYouTubeLoginKey
import it.fast4x.rimusic.utils.getPipedSession
import it.fast4x.rimusic.utils.preferences
import kotlinx.coroutines.flow.distinctUntilChanged
import me.knighthat.appContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(UnstableApi::class)
internal suspend fun PlayerServiceModern.dataSpecProcess(
    dataSpec: DataSpec,
    context: Context,
    metered: Boolean
): DataSpec {
    val songUri = dataSpec.uri.toString()
    val videoId = songUri.substringAfter("watch?v=")
    val chunkLength = 512 * 1024L

    if( dataSpec.isLocal ||
        cache.isCached(videoId, dataSpec.position, chunkLength) ||
        downloadCache.isCached(videoId, dataSpec.position, if (dataSpec.length >= 0) dataSpec.length else 1)
    ) {
        println("PlayerService DataSpecProcess Playing song ${videoId} from cached or local file")
        return dataSpec.withUri(Uri.parse(dataSpec.uri.toString()))
    }

    val format = getMediaFormat(videoId, audioQualityFormat)

    println("PlayerService DataSpecProcess Playing song ${videoId} from format $format from url=${format?.url}")
    return dataSpec.withUri(Uri.parse(format?.url))

}

@OptIn(UnstableApi::class)
suspend fun getMediaFormat(
    videoId: String,
    audioQualityFormat: AudioQualityFormat
): PlayerResponse.StreamingData.AdaptiveFormat? {
    //println("PlayerService MyDownloadHelper DataSpecProcess getMediaFormat Playing song $videoId from format $audioQualityFormat")
    return Innertube.player(
        body = PlayerBody(videoId = videoId),
        withLogin = appContext().preferences.getBoolean(enableYouTubeLoginKey, false),
        //pipedSession = getPipedSession().toApiSession()
    ).fold(
        { playerResponse ->

            when(playerResponse.playabilityStatus?.status) {
                "OK" -> {
                    when (audioQualityFormat) {
                        AudioQualityFormat.Auto -> playerResponse.streamingData?.autoMaxQualityFormat
                        AudioQualityFormat.High -> playerResponse.streamingData?.highestQualityFormat
                        AudioQualityFormat.Medium -> playerResponse.streamingData?.mediumQualityFormat
                        AudioQualityFormat.Low -> playerResponse.streamingData?.lowestQualityFormat
                    }.let {
                        // Specify range to avoid YouTube's throttling
                        it?.copy(url = "${it.url}&range=0-${it.contentLength ?: 10000000}")
                    }.also {
                        //println("PlayerService MyDownloadHelper DataSpecProcess getMediaFormat before upsert format $it")
                        query {
                            if (Database.songExist(videoId) > 0)
                                Database.upsert(
                                    Format(
                                        songId = videoId,
                                        itag = it?.itag,
                                        mimeType = it?.mimeType,
                                        contentLength = it?.contentLength,
                                        bitrate = it?.bitrate,
                                        lastModified = it?.lastModified,
                                        loudnessDb = it?.loudnessDb?.toFloat(),
                                    )
                                )
                        }
                        //println("PlayerService MyDownloadHelper DataSpecProcess getMediaFormat after upsert format $it")
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
                    println("PlayerService MyDownloadHelper DataSpecProcess Error: ${throwable.stackTraceToString()}")
                    throw throwable
                }
            }

        }
    )
}