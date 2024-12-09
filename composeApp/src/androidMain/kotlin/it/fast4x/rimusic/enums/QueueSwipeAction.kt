package it.fast4x.rimusic.enums

import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NotificationButtons.Favorites
import it.fast4x.rimusic.enums.NotificationButtons.Radio
import it.fast4x.rimusic.enums.NotificationButtons.Repeat
import it.fast4x.rimusic.enums.NotificationButtons.Search
import it.fast4x.rimusic.enums.NotificationButtons.Shuffle
import me.knighthat.appContext

enum class QueueSwipeAction {
    PlayNext,
    Download,
    AddToPlaylist,
    ListenOn,
    RemoveFromQueue;

    val displayName: String
        get() = when (this) {
            PlayNext -> appContext().resources.getString(R.string.play_next)
            Download  -> appContext().resources.getString(R.string.download)
            AddToPlaylist  -> appContext().resources.getString(R.string.add_to_playlist)
            ListenOn  -> appContext().resources.getString(R.string.listen_on)
            RemoveFromQueue  -> appContext().resources.getString(R.string.remove_from_queue)
        }

    val icon: Int
        get() = when (this) {
            PlayNext -> R.drawable.play_skip_forward
            Download -> R.drawable.download
            AddToPlaylist -> R.drawable.add_in_playlist
            ListenOn -> R.drawable.play
            RemoveFromQueue -> R.drawable.trash
        }
}
