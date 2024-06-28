package it.fast4x.innertube.requests

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.bodies.BrowseBodyWithLocale
import it.fast4x.innertube.models.v0624.charts.BrowseChartsResponse0624
import it.fast4x.innertube.models.v0624.charts.MusicCarouselShelfRenderer

suspend fun Innertube.chartsPage() = runCatching {
    val response = client.post(browse) {
        setBody(BrowseBodyWithLocale(browseId = "FEmusic_charts"))
    }.body<BrowseChartsResponse0624>()

    val musicDetailRenderer =
        response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents

    println("mediaItem chartsPage ${musicDetailRenderer
        ?.mapNotNull { it.musicCarouselShelfRenderer }
        ?.mapNotNull(Innertube.PlaylistItem::from)?.size}")

    /*
    println("mediaItem chartsPage Language ${
        musicDetailRenderer
            ?.musicShelfRenderer?.subheaders?.firstOrNull()
            ?.musicSideAlignedItemRenderer?.startItems?.firstOrNull()
            ?.musicSortFilterButtonRenderer
            ?.title?.runs?.firstOrNull()?.text}")
     */

    Innertube.ChartsPage(
        playlists = musicDetailRenderer
            ?.mapNotNull { it.musicCarouselShelfRenderer }
            ?.mapNotNull(Innertube.PlaylistItem::from)
    )

    /*
        Innertube.PlaylistOrAlbumPage(
            title = musicDetailRenderer
                ?.musicShelfRenderer?.subheaders?.firstOrNull()
                ?.musicSideAlignedItemRenderer?.startItems?.firstOrNull()
                ?.musicSortFilterButtonRenderer
                ?.title?.runs?.firstOrNull()?.text,
            description = null,
            thumbnail = null,
            authors = null,
            year = null,
            url = null,
            songsPage = null,
            otherVersions = null,
            otherInfo = null

        )

     */

}.onFailure {
    println("mediaItem ERROR IN Innertube chartsPage " + it.message)
}

fun Innertube.PlaylistItem.Companion.from(renderer: MusicCarouselShelfRenderer): Innertube.PlaylistItem? {

    val thumbnail0 = renderer
        .contents?.firstOrNull()?.musicTwoRowItemRenderer
        ?.thumbnailRenderer
        ?.musicThumbnailRenderer
        ?.thumbnail
        ?.thumbnails
        ?.firstOrNull()?.toThumbnail()

    val thumbnail1 = renderer
        .contents?.firstOrNull()?.musicResponsiveListItemRenderer
        ?.thumbnail
        ?.musicThumbnailRenderer
        ?.thumbnail
        ?.thumbnails
        ?.firstOrNull()?.toThumbnail()

    return Innertube.PlaylistItem(
        info = Innertube.Info(
            name = renderer.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.firstOrNull()?.text,
            endpoint = it.fast4x.innertube.models.NavigationEndpoint.Endpoint.Browse(
                browseId = renderer
                    .header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.firstOrNull()?.navigationEndpoint?.browseEndpoint?.browseID,
                params = renderer
                    .contents?.firstOrNull()?.musicTwoRowItemRenderer
                    ?.navigationEndpoint?.watchEndpoint?.params.toString(),
                browseEndpointContextSupportedConfigs = null
            )
        ),
        channel = null,
        songCount = renderer
            .contents?.size,
        thumbnail = thumbnail0 ?: thumbnail1
    ).takeIf { it.info?.endpoint?.browseId != null }
}