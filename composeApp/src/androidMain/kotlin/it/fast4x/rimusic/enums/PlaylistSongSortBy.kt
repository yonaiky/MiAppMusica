package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import me.knighthat.enums.MenuTitle

enum class PlaylistSongSortBy( val textId: Int ): MenuTitle {
    Album( R.string.sort_album ),
    AlbumYear( R.string.sort_album_year ),
    Artist( R.string.sort_artist ),
    ArtistAndAlbum( -1 ),
    DatePlayed( R.string.sort_date_played ),
    PlayTime( R.string.sort_listening_time ),
    Position( R.string.sort_position ),
    Title( R.string.sort_title ),
    Duration( R.string.sort_duration ),
    DateLiked( R.string.sort_date_liked ),
    DateAdded( R.string.sort_date_added );

    override val titleId: Int
        get() {
            // Due to the requirement of 2 separated strings,
            // this needs to be handled separately
            if( this == ArtistAndAlbum )
                throw UnsupportedOperationException()

            return this.textId
        }
}
