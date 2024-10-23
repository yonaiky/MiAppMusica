package it.fast4x.innertube.requests

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.Context
import it.fast4x.innertube.models.PlayerResponse
import it.fast4x.innertube.models.bodies.PlayerBody
import it.fast4x.innertube.utils.runCatchingNonCancellable
import it.fast4x.invidious.Invidious
import it.fast4x.piped.models.Session

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

                /**** INVIDIOUS ****/
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

