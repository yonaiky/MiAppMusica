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
        Innertube.createPlaylist(Context.DefaultWeb.client, title).body<CreatePlaylistResponse>().playlistId
    }.onFailure {
        println("YtMusic createPlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun deletePlaylist(playlistId: String) = runCatching {
        Innertube.deletePlaylist(Context.DefaultWeb.client, playlistId)
    }.onFailure {
        println("YtMusic deletePlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun renamePlaylist(playlistId: String, name: String) = runCatching {
        Innertube.renamePlaylist(Context.DefaultWeb.client, playlistId, name)
    }.onFailure {
        println("YtMusic renamePlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun addToPlaylist(playlistId: String, videoId: String) = runCatching {
        Innertube.addToPlaylist(Context.DefaultWeb.client, playlistId, videoId)
    }.onFailure {
        println("YtMusic addToPlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun removeFromPlaylist(playlistId: String, videoId: String, setVideoId: String? = null) = runCatching {
        Innertube.removeFromPlaylist(Context.DefaultWeb.client, playlistId, videoId, setVideoId)
    }.onFailure {
        println("YtMusic removeFromPlaylist error: ${it.stackTraceToString()}")
    }

    suspend fun getHomePage(setLogin: Boolean = false): Result<HomePage> = runCatching {

        var response = Innertube.browse(browseId = "FEmusic_home", setLogin = setLogin).body<BrowseResponse>()

        println("homePage() response sections: ${response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents}" )



//        val accountName =
//                response.contents
//                    ?.singleColumnBrowseResultsRenderer
//                    ?.tabs
//                    ?.get(
//                        0,
//                    )?.tabRenderer
//                    ?.content
//                    ?.sectionListRenderer
//                    ?.contents
//                    ?.get(
//                        0,
//                    )?.musicCarouselShelfRenderer
//                    ?.header
//                    ?.musicCarouselShelfBasicHeaderRenderer
//                    ?.strapline
//                    ?.runs
//                    ?.get(
//                        0,
//                    )?.text ?: ""
//        val accountThumbnailUrl =
//                response.contents
//                    ?.singleColumnBrowseResultsRenderer
//                    ?.tabs
//                    ?.get(
//                        0,
//                    )?.tabRenderer
//                    ?.content
//                    ?.sectionListRenderer
//                    ?.contents
//                    ?.get(
//                        0,
//                    )?.musicCarouselShelfRenderer
//                    ?.header
//                    ?.musicCarouselShelfBasicHeaderRenderer
//                    ?.thumbnail
//                    ?.musicThumbnailRenderer
//                    ?.thumbnail
//                    ?.thumbnails
//                    ?.get(
//                        0,
//                    )?.url
//                    ?.replace("s88", "s352") ?: ""



        var continuation = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.continuations?.getContinuation()

        val sections = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents!!
            .mapNotNull { it.musicCarouselShelfRenderer }
            .mapNotNull {
                HomePage.Section.fromMusicCarouselShelfRenderer(it)
            }.toMutableList()
        while (continuation != null) {
            println("gethomePage() continuation before:  ${continuation}" )
            response = Innertube.browse(continuation = continuation).body<BrowseResponse>()
            continuation = response.continuationContents?.sectionListContinuation?.continuations?.getContinuation()
            println("gethomePage() continuation after:  ${continuation}" )

            sections += response.continuationContents?.sectionListContinuation?.contents
                ?.mapNotNull { it.musicCarouselShelfRenderer }
                ?.mapNotNull {
                    HomePage.Section.fromMusicCarouselShelfRenderer(it)
                }.orEmpty()

        }
        HomePage(
            sections = sections,
            //accountName = accountName,
            //accountThumbnailUrl = accountThumbnailUrl,
        )
    }

}