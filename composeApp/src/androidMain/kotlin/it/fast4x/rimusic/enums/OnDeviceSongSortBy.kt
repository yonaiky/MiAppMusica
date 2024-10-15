package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import me.knighthat.enums.MenuTitle

enum class OnDeviceSongSortBy( private val textId: Int ): MenuTitle {
    Title( R.string.sort_title ),
    DateAdded( R.string.sort_date_played ),
    Artist( R.string.sort_artist ),
    Duration( R.string.sort_duration ),
    Album( R.string.sort_album );

    override val titleId: Int
        get() = this.textId
}
