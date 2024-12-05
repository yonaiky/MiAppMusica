package it.fast4x.rimusic.enums

import androidx.annotation.DrawableRes
import it.fast4x.rimusic.R
import it.fast4x.rimusic.appContext

enum class AlbumSwipeAction(
    @field:DrawableRes override val iconId: Int
): Drawable {

    NoAction( R.drawable.close ),

    PlayNext( R.drawable.play_skip_forward ),

    Bookmark( R.drawable.bookmark_outline ),

    Enqueue( R.drawable.enqueue );

    val displayName: String
        get() = when (this) {
            NoAction -> appContext().resources.getString(R.string.none)
            PlayNext -> appContext().resources.getString(R.string.play_next)
            Bookmark  -> appContext().resources.getString(R.string.bookmark)
            Enqueue  -> appContext().resources.getString(R.string.enqueue)
        }

    fun getStateIcon(bookmarkedState: Long?): Int? {
        return when (this) {
            Bookmark -> when(bookmarkedState) {
                null -> R.drawable.bookmark_outline
                else -> R.drawable.bookmark
            }
            NoAction -> null
            else -> iconId
        }
    }
}
