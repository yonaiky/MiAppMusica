package it.vfsfitvnm.vimusic.enums

enum class PlaylistSongSortBy {
    AlbumYear,
    Artist,
    DatePlayed,
    PlayTime,
    Position,
    Title,
    Duration;

    val index: Int
        get() = when (this) {
            AlbumYear -> 0
            Artist -> 1
            DatePlayed -> 2
            PlayTime -> 3
            Position -> 4
            Title -> 5
            Duration -> 6
    }


}
