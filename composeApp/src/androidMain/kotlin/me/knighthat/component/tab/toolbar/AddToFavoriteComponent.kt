package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.MediaItem
import androidx.room.Transaction
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.R
import it.fast4x.rimusic.transaction


class AddToFavoriteComponent private constructor(
    private val items: () -> List<MediaItem>
): MenuIcon {

    companion object {
        @JvmStatic
        @Composable
        fun init( items: () -> List<MediaItem> ) = AddToFavoriteComponent( items )
    }

    override val menuIconTitle: String
        @Composable
        get() = stringResource( R.string.add_to_favorites )
    override val iconId: Int = R.drawable.heart

    @Transaction
    override fun onShortClick() = transaction {
        items().forEach {
            Database.like(it.mediaId, System.currentTimeMillis())
        }
    }
}