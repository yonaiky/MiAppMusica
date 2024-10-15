package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import me.knighthat.enums.MenuTitle

enum class SongSortBy( private val textId: Int ): MenuTitle {
    PlayTime( R.string.sort_listening_time ),
    Title( R.string.sort_title ),
    DateAdded( R.string.sort_date_played ),
    DatePlayed( R.string.sort_date_played ),
    DateLiked( R.string.sort_date_liked ),
    Artist( R.string.sort_artist ),
    Duration( R.string.sort_duration ),
    AlbumName( R.string.sort_album );

    override val titleId: Int
        get() = this.textId
}
