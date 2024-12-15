package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import me.knighthat.appContext

enum class PlaylistSwipeAction {
    NoAction,
    PlayNext,
    Download,
    Favourite,
    Enqueue;

    val displayName: String
        get() = when (this) {
            NoAction -> appContext().resources.getString(R.string.none)
            PlayNext -> appContext().resources.getString(R.string.play_next)
            Download  -> appContext().resources.getString(R.string.download)
            Favourite -> appContext().resources.getString(R.string.favorites)
            Enqueue  -> appContext().resources.getString(R.string.enqueue)
        }

    val icon: Int
        get() = when (this) {
            NoAction -> R.drawable.alert
            PlayNext -> R.drawable.play_skip_forward
            Download -> R.drawable.download
            Favourite -> R.drawable.heart_outline
            Enqueue -> R.drawable.enqueue
        }
}
