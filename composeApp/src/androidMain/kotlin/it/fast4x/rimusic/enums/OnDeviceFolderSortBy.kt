package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import me.knighthat.enums.MenuTitle

enum class OnDeviceFolderSortBy( private val textId: Int ): MenuTitle {
    Title( R.string.sort_title ),
    Artist( R.string.sort_artist ),
    Duration( R.string.sort_duration );

    override val titleId: Int
        get() = this.textId
}
