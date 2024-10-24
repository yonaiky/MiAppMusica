package it.fast4x.innertube.requests

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.Context
import it.fast4x.innertube.models.PlayerResponse
import it.fast4x.innertube.models.YouTubeClient
import it.fast4x.innertube.models.bodies.PlayerBody
import it.fast4x.innertube.utils.runCatchingNonCancellable
import it.fast4x.invidious.Invidious
import it.fast4x.piped.models.Session

suspend fun Innertube.player(body: PlayerBody): Result<PlayerResponse> = runCatching {

    val response = client.post(player) {
        ytClient(YouTubeClient.ANDROID_MUSIC, setLogin = true)
        setBody(body)
        mask("playabilityStatus.status,playerConfig.audioConfig,streamingData.adaptiveFormats,videoDetails.videoId")
    }.body<PlayerResponse>()

    println("PlayerService NEW Innertube.player response $response")

    response

    /*
    val playerResponse = newPlayer(ANDROID_MUSIC, videoId, playlistId).body<PlayerResponse>()
    if (playerResponse.playabilityStatus.status == "OK") {
        return@runCatching playerResponse
    }
    val safePlayerResponse = innerTube.player(TVHTML5, videoId, playlistId).body<PlayerResponse>()
    if (safePlayerResponse.playabilityStatus.status != "OK") {
        return@runCatching playerResponse
    }
    val audioStreams = innerTube.pipedStreams(videoId).body<PipedResponse>().audioStreams
    safePlayerResponse.copy(
        streamingData = safePlayerResponse.streamingData?.copy(
            adaptiveFormats = safePlayerResponse.streamingData.adaptiveFormats.mapNotNull { adaptiveFormat ->
                audioStreams.find { it.bitrate == adaptiveFormat.bitrate }?.let {
                    adaptiveFormat.copy(
                        url = it.url
                    )
                }
            }
        )
    )
    */
}.onFailure {
    println("YoutubeLogin PlayerService NEW Innertube.player error ${it.stackTraceToString()}")
}
/*
suspend fun Innertube.player(
    body: PlayerBody,
    //pipedApiInstance: String = "pipedapi.adminforge.de",
    pipedSession: Session
    ) = runCatchingNonCancellable {

        val response = client.post(player) {
            setBody(body)
            mask("playabilityStatus.status,playerConfig.audioConfig,streamingData.adaptiveFormats,videoDetails.videoId")
        }.body<PlayerResponse>()

        println("PlayerService Innertube.player response $response")


        if (response.playabilityStatus?.status == "OK") {
            response
        } else {
            val safePlayerResponse = client.post(player) {
                setBody(
                    body.copy(
                        context = Context.DefaultAndroid.copy(
                            thirdParty = Context.ThirdParty(
                                embedUrl = "https://www.youtube.com/watch?v=${body.videoId}"
                            )
                        ),
                    )
                )
                mask("playabilityStatus.status,playerConfig.audioConfig,streamingData.adaptiveFormats,videoDetails.videoId")
            }.body<PlayerResponse>()

            println("PlayerService Innertube.player response safePlayerResponse $safePlayerResponse")

            if (safePlayerResponse.playabilityStatus?.status == "OK") {
                safePlayerResponse
            } else {


                /**** INVIDIOUS TEST AS BACKUP LINE ****/
                println("PlayerService Innertube.player Invidious.api.videos ${body.videoId}")
                val safeResponse = Invidious.api.videos(body.videoId)?.getOrNull()

                if (safeResponse != null) {
                    safePlayerResponse.copy(
                        streamingData = safePlayerResponse.streamingData?.copy(
                            adaptiveFormats = safePlayerResponse.streamingData.adaptiveFormats?.map { adaptiveFormat ->
                                adaptiveFormat.copy(
                                    url = safeResponse.find { it.bitrate == adaptiveFormat.bitrate }?.url
                                )
                            }

                        )
                    )
                } else safePlayerResponse
                /**** INVIDIOUS ****/


                /*
                @Serializable
                data class AudioStream(
                    val url: String,
                    val bitrate: Long
                )

                @Serializable
                data class PipedResponse(
                    val audioStreams: List<AudioStream>
                )

                val audioStreams = client.get("https://$pipedApiInstance/streams/${body.videoId}") {
                    contentType(ContentType.Application.Json)
                }.body<PipedResponse>().audioStreams
                 */

                //safePlayerResponse // temporaly used

                /*
                    // TODO() Piped api streams not working, improve with other service
                    val audioStreams = Piped.media.audioStreams(
                        session = pipedSession,
                        videoId = body.videoId
                    )?.getOrNull()

                println("PlayerService Innertube.player PIPED audiostreams $audioStreams")

                    safePlayerResponse.copy(
                        streamingData = safePlayerResponse.streamingData?.copy(
                            adaptiveFormats = safePlayerResponse.streamingData.adaptiveFormats?.map { adaptiveFormat ->
                                adaptiveFormat.copy(
                                    url = audioStreams?.find { it.bitrate == adaptiveFormat.bitrate }?.url
                                )
                            }
                        )
                    )

                 */


            }

        }



    }

*/