package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import me.knighthat.enums.MenuTitle

enum class AlbumSortBy( private val textId: Int ): MenuTitle {
    Title( R.string.sort_album ),
    Year( R.string.sort_album_year ),
    DateAdded( R.string.sort_date_added ),
    Artist( R.string.sort_artist ),
    Songs( R.string.sort_songs_number ),
    Duration( R.string.sort_duration );

    override val titleId: Int
        get() = this.textId
}
