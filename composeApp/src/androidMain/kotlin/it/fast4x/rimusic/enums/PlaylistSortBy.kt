package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import me.knighthat.enums.MenuTitle

enum class PlaylistSortBy( private val textId: Int ): MenuTitle {
    MostPlayed( R.string.sort_listening_time ),
    Name( R.string.sort_name ),
    DateAdded( R.string.sort_date_added ),
    SongCount( R.string.sort_songs_number );

    override val titleId: Int
        get() = this.textId
}
