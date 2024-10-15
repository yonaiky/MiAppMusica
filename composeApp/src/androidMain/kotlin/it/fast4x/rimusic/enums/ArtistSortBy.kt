package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import me.knighthat.enums.MenuTitle

enum class ArtistSortBy( private val textId: Int ): MenuTitle {
    Name( R.string.sort_artist ),
    DateAdded( R.string.sort_date_added );

    override val titleId: Int
        get() = this.textId
}
