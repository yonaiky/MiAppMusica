package it.fast4x.rimusic.service

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import it.fast4x.rimusic.service.modern.getAvancedInnerTubeStream
import it.fast4x.rimusic.service.modern.getInnerTubeStream
import it.fast4x.rimusic.service.modern.isLocal
import it.fast4x.rimusic.ui.screens.settings.isYouTubeLoggedIn

@OptIn(UnstableApi::class)
internal suspend fun MyDownloadHelper.dataSpecProcess(
    dataSpec: DataSpec,
    context: Context,
    connectionMetered: Boolean = false
): DataSpec {
    val songUri = dataSpec.uri.toString()
    val videoId = songUri.substringAfter("watch?v=")
    val chunkLength = 512 * 1024L
    val length = if (dataSpec.length >= 0) dataSpec.length else 1

    println("MyDownloadHelper DataSpecProcess Playing song ${videoId} dataSpec position ${dataSpec.position} length ${dataSpec.length}")
    if( dataSpec.isLocal ||
        downloadCache.isCached(videoId, dataSpec.position, length)
    ) {
        println("MyDownloadHelper DataSpecProcess download song ${videoId} from cached or local file")
        return dataSpec.withUri(Uri.parse(dataSpec.uri.toString()))
    }


    try {
        //runBlocking(Dispatchers.IO) {
            //if loggedin use advanced player with webPotoken and new newpipe extractor
            val format = if (!isYouTubeLoggedIn()) getInnerTubeStream(videoId, audioQualityFormat, connectionMetered)
            else getAvancedInnerTubeStream(videoId, audioQualityFormat, connectionMetered)
            return dataSpec.withUri(Uri.parse(format?.url))
        //}


    } catch ( e: Exception ) {
        println("MyDownloadHelper DataSpecProcess Error: ${e.stackTraceToString()}")
        val format = getInnerTubeStream(videoId, audioQualityFormat, connectionMetered)
        return dataSpec.withUri(Uri.parse(format?.url))
//        println("MyDownloadHelper DataSpecProcess Playing song $videoId from ALTERNATIVE url")
//        val alternativeUrl = "https://jossred.josprox.com/yt/stream/$videoId"
//        return dataSpec.withUri(alternativeUrl.toUri())

    } catch ( e: Exception ) {
        // Rethrow exception if it's not handled
        throw e
    }
}

//@OptIn(UnstableApi::class)
//internal suspend fun MyDownloadHelper.dataSpecProcess(
//    dataSpec: DataSpec,
//    context: Context,
//    connectionMetered: Boolean = false
//): DataSpec {
//    val songUri = dataSpec.uri.toString()
//    val videoId = songUri.substringAfter("watch?v=")
//
//    if( dataSpec.isLocal ||
//        downloadCache.isCached(videoId, dataSpec.position, if (dataSpec.length >= 0) dataSpec.length else 1)
//    ) {
//        println("MyDownloadHelper DataSpecProcess Playing song ${videoId} from cached or local file")
//        return dataSpec.withUri(Uri.parse(dataSpec.uri.toString()))
//    }
//
//    // specify range to avoid YouTube's throttling in download
//    val format = getInnerTubeFormatUrl(videoId, audioQualityFormat, connectionMetered)
//        ?.let { it.copy( url = "${it.url}&range=0-${it.contentLength ?: 10000000}") }
//
//    println("MyDownloadHelper DataSpecProcess Playing song $videoId from format $format from url=${format?.url}")
//    return dataSpec.withUri(Uri.parse(format?.url))
//
//}
