package it.fast4x.innertubes.pages

import it.fast4x.innertubes.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)
