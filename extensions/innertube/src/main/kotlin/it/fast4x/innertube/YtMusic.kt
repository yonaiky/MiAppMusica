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
import it.fast4x.innertube.requests.HistoryPage
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
        HomePage( sections = sections )
    }

    suspend fun getHistory(setLogin: Boolean = false): Result<HistoryPage> = runCatching {

        val response = Innertube.browse(browseId = "FEmusic_history", setLogin = setLogin)
            .body<BrowseResponse>()

        println("getHistory() response sections: ${response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents}" )

        HistoryPage(
            sections = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
                ?.tabRenderer?.content?.sectionListRenderer?.contents
                ?.mapNotNull {
                    it.musicShelfRenderer?.let { musicShelfRenderer ->
                        HistoryPage.fromMusicShelfRenderer(musicShelfRenderer)
                    }
                }
        )

    }

//    suspend fun getArtistPage(browseId: String): Result<Innertube.ArtistPage> = runCatching {
//        val response = innerTube.browse(WEB_REMIX, browseId, setLogin = useLoginForBrowse).body<BrowseResponse>()
//
//        ArtistPage(
//            artist = ArtistItem(
//                id = browseId,
//                title = response.header?.musicImmersiveHeaderRenderer?.title?.runs?.firstOrNull()?.text
//                    ?: response.header?.musicVisualHeaderRenderer?.title?.runs?.firstOrNull()?.text
//                    ?: response.header?.musicHeaderRenderer?.title?.runs?.firstOrNull()?.text!!,
//                thumbnail = response.header?.musicImmersiveHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
//                    ?: response.header?.musicVisualHeaderRenderer?.foregroundThumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
//                    ?: response.header?.musicDetailHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl(),
//                channelId = response.header?.musicImmersiveHeaderRenderer?.subscriptionButton?.subscribeButtonRenderer?.channelId,
//                playEndpoint = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
//                    ?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.musicShelfRenderer
//                    ?.contents?.firstOrNull()?.musicResponsiveListItemRenderer?.overlay?.musicItemThumbnailOverlayRenderer
//                    ?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint,
//                shuffleEndpoint = response.header?.musicImmersiveHeaderRenderer?.playButton?.buttonRenderer?.navigationEndpoint?.watchEndpoint
//                    ?: response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer
//                        ?.contents?.firstOrNull()?.musicShelfRenderer?.contents?.firstOrNull()?.musicResponsiveListItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint,
//                radioEndpoint = response.header?.musicImmersiveHeaderRenderer?.startRadioButton?.buttonRenderer?.navigationEndpoint?.watchEndpoint
//            ),
//            sections = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
//                ?.tabRenderer?.content?.sectionListRenderer?.contents
//                ?.mapNotNull(ArtistPage::fromSectionListRendererContent)!!,
//            description = response.header?.musicImmersiveHeaderRenderer?.description?.runs?.firstOrNull()?.text
//        )
//    }

}