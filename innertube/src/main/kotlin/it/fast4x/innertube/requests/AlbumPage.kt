package it.fast4x.innertube.requests

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Url
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.BrowseResponse
import it.fast4x.innertube.models.NavigationEndpoint
import it.fast4x.innertube.models.bodies.BrowseBody

suspend fun Innertube.albumPage(body: BrowseBody) = playlistPage(body)?.map { album ->
    album.url?.let { Url(it).parameters["list"] }?.let { playlistId ->
        playlistPage(BrowseBody(browseId = "VL$playlistId"))?.getOrNull()?.let { playlist ->
            album.copy(songsPage = playlist.songsPage)
            /*
            albumPageDetails(BrowseBody(browseId = body.browseId)).getOrNull()?.let {
                //println("mediaItem albumPage pre songsPage ${album.songsPage?.items?.size} ")
                album.copy(songsPage = playlist.songsPage, description = it.description, otherInfo = it.otherInfo)
            } ?: album.copy(songsPage = playlist.songsPage)
            */
        }
    } ?: album
}
    ?.map { album ->
        albumPageDetails(BrowseBody(browseId = body.browseId)).getOrNull()?.let {
            println("mediaItem albumPage pre songsPage ${album.songsPage?.items?.size} ")
            album.copy(description = it.description, otherInfo = it.otherInfo)
        }
}
    ?.map { album ->
    println("mediaItem albumPage post songsPage ${album?.songsPage?.items?.size} des ${album?.description} browseId ${body.browseId}")

    val albumInfo = Innertube.Info(
        name = album?.title,
        endpoint = NavigationEndpoint.Endpoint.Browse(
            browseId = body.browseId,
            params = body.params
        )
    )

        album?.copy(
            songsPage = album.songsPage?.copy(
                items = album.songsPage.items?.map { song ->
                    song.copy(
                        authors = song.authors ?: album.authors,
                        album = albumInfo,
                        thumbnail = album.thumbnail
                    )
                }
            )
        )

}

/*
suspend fun Innertube.albumPage(body: BrowseBody) = playlistPage(body)?.map { album ->
    album.url?.let { Url(it).parameters["list"] }?.let { playlistId ->
        playlistPage(BrowseBody(browseId = "VL$playlistId"))?.getOrNull()?.let { playlist ->
            album.copy(songsPage = playlist.songsPage)
        } /*
        albumPageDetails(BrowseBody(browseId = body.browseId)).getOrNull()?.let {
            println("mediaItem albumPage pre songsPage ${it.songsPage?.items?.size} ")
            album.copy(description = it.description, otherInfo = it.otherInfo)
        }
        */
    } ?: album
}?.map { album ->

    //println("mediaItem albumPage post songsPage ${album.songsPage?.items?.size} des ${album.description} browseId ${body.browseId}")

    val albumInfo = Innertube.Info(
        name = album.title,
        endpoint = NavigationEndpoint.Endpoint.Browse(
            browseId = body.browseId,
            params = body.params
        )
    )



    album.copy(
        songsPage = album.songsPage?.copy(
            items = album.songsPage.items?.map { song ->
                song.copy(
                    authors = song.authors ?: album.authors,
                    album = albumInfo,
                    thumbnail = album.thumbnail
                )
            }
        )
    )

}
*/

suspend fun Innertube.albumPageDetails(body: BrowseBody) = runCatching {
    val response = client.post(browse) {
        setBody(body)
        //mask("contents.singleColumnBrowseResultsRenderer.tabs.tabRenderer.content.sectionListRenderer.contents(musicShelfRenderer(continuations,contents.$musicResponsiveListItemRendererMask),musicCarouselShelfRenderer.contents.$musicTwoRowItemRendererMask),header.musicDetailHeaderRenderer(title,subtitle,thumbnail),microformat")
        body.context.apply()
    }.body<BrowseResponse>()

    val musicDetailHeaderRenderer = response
        .header
        ?.musicDetailHeaderRenderer

    println("mediaItem albumPageDetails des ${musicDetailHeaderRenderer
        ?.description
        ?.text} browseId ${body.browseId}")

    Innertube.PlaylistOrAlbumPage(
        title = null,
        description = musicDetailHeaderRenderer
            ?.description
            ?.text,
        thumbnail = null,
        authors = null,
        year = null,
        url = null,
        songsPage = null,
        otherVersions = null,
        otherInfo = musicDetailHeaderRenderer
            ?.secondSubtitle
            ?.text
    )

}
