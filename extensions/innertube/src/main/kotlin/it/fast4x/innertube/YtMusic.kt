package it.fast4x.innertube

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import it.fast4x.innertube.Innertube.client
import it.fast4x.innertube.Innertube.locale
import it.fast4x.innertube.Innertube.setLogin
import it.fast4x.innertube.Innertube.visitorData
import it.fast4x.innertube.models.BrowseResponse
import it.fast4x.innertube.models.Context
import it.fast4x.innertube.models.Context.Client
import it.fast4x.innertube.models.Context.Companion.DefaultIOS
import it.fast4x.innertube.models.CreatePlaylistResponse
import it.fast4x.innertube.models.bodies.BrowseBodyWithLocale
import it.fast4x.innertube.models.bodies.PlayerBody
import it.fast4x.innertube.models.getContinuation
import it.fast4x.innertube.requests.HomePage
import it.fast4x.innertube.requests.browse

object YtMusic {

    suspend fun createPlaylist(title: String) = runCatching {
        Innertube.createPlaylist(Context.DefaultWebRemix.client, title).body<CreatePlaylistResponse>().playlistId
    }.onFailure {
        println("YtMusic createPlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun deletePlaylist(playlistId: String) = runCatching {
        Innertube.deletePlaylist(Context.DefaultWebRemix.client, playlistId)
    }.onFailure {
        println("YtMusic deletePlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun renamePlaylist(playlistId: String, name: String) = runCatching {
        Innertube.renamePlaylist(Context.DefaultWebRemix.client, playlistId, name)
    }.onFailure {
        println("YtMusic renamePlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun addToPlaylist(playlistId: String, videoId: String) = runCatching {
        Innertube.addToPlaylist(Context.DefaultWebRemix.client, playlistId, videoId)
    }.onFailure {
        println("YtMusic addToPlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun removeFromPlaylist(playlistId: String, videoId: String, setVideoId: String? = null) = runCatching {
        Innertube.removeFromPlaylist(Context.DefaultWebRemix.client, playlistId, videoId, setVideoId)
    }.onFailure {
        println("YtMusic removeFromPlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun homePage(): Result<HomePage> = runCatching {
        var response = Innertube.browse(Context.DefaultWeb.client, browseId = "FEmusic_home").body<BrowseResponse>()
        var continuation = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.continuations?.getContinuation()
        val sections = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents!!
            .mapNotNull { it.musicCarouselShelfRenderer }
            .mapNotNull {
                HomePage.Section.fromMusicCarouselShelfRenderer(it)
            }.toMutableList()
        while (continuation != null) {
            response = Innertube.browse(Context.DefaultWeb.client, browseId = "", continuation = continuation).body<BrowseResponse>()
            continuation = response.continuationContents?.sectionListContinuation?.continuations?.getContinuation()
            sections += response.continuationContents?.sectionListContinuation?.contents
                ?.mapNotNull { it.musicCarouselShelfRenderer }
                ?.mapNotNull {
                    HomePage.Section.fromMusicCarouselShelfRenderer(it)
                }.orEmpty()
        }
        HomePage(sections)
    }

    suspend fun player(
        //ytClient: Client,
        videoId: String,
        playlistId: String?,
    ) = client.post(Innertube.player) {
        //setLogin(ytClient, setLogin = true)
        setLogin(setLogin = true)
        setBody(
            PlayerBody(
//                context = ytClient.toContext(locale, visitorData).let {
//                    if (ytClient == Context.DefaultRestrictionBypass.client) {
//                        it.copy(
//                            thirdParty =
//                            Context.ThirdParty(
//                                embedUrl = "https://www.youtube.com/watch?v=$videoId",
//                            ),
//                        )
//                    } else {
//                        it
//                    }
//                },
                videoId = videoId,
                playlistId = playlistId,
            ),
        )
    }

    suspend fun noLogInPlayer(videoId: String, withLogin: Boolean = false) =
        client.post(Innertube.player) {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            header("Host", "music.youtube.com")
            setBody(
                PlayerBody(
                    context = DefaultIOS.client.toContext(locale, visitorData),
                    playlistId = null,
                    videoId = videoId,
                ),
            )
        }

}