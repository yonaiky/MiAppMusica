package it.fast4x.innertube.requests

import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.MusicResponsiveListItemRenderer
import it.fast4x.innertube.models.MusicShelfRenderer
import it.fast4x.innertube.models.oddElements

data class HistoryPage(
    val sections: List<HistorySection>?,
) {
    data class HistorySection(
        val title: String,
        val songs: List<Innertube.SongItem>
    )

    companion object {
        fun fromMusicShelfRenderer(renderer: MusicShelfRenderer): HistorySection {
            return HistorySection(
                title = renderer.title?.runs?.firstOrNull()?.text!!,
                songs = renderer.contents?.mapNotNull {
                    it.musicResponsiveListItemRenderer?.let { it1 ->
                        fromMusicResponsiveListItemRenderer(
                            it1
                        )
                    }
                }!!
            )
        }

        private fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): Innertube.SongItem? {
            return Innertube.SongItem(
                info = Innertube.Info(
                    name = renderer.flexColumns.firstOrNull()
                        ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
                        ?.text ?: return null,
                    endpoint = renderer.navigationEndpoint?.watchEndpoint
                ),
                authors = renderer.flexColumns.getOrNull(1)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.oddElements()
                    ?.map {
                        Innertube.Info(
                            name = it.text,
                            endpoint = it.navigationEndpoint?.browseEndpoint
                        )
                    } ?: emptyList(),
                album = renderer.flexColumns.getOrNull(3)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
                    ?.let {
                        Innertube.Info(
                            name = it.text,
                            endpoint = it.navigationEndpoint?.browseEndpoint ?: return@let null
                        )
                    },
                durationText = renderer.fixedColumns?.firstOrNull()?.musicResponsiveListItemFlexColumnRenderer
                    ?.text?.runs?.firstOrNull()?.text,
                thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.thumbnail?.thumbnails?.lastOrNull()
                    ?: return null,
                explicit = renderer.badges?.find {
                    it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                } != null,
//                endpoint = renderer.overlay?.musicItemThumbnailOverlayRenderer?.content
//                    ?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint
            )
        }
    }
}
