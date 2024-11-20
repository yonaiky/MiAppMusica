package me.knighthat.component.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.PINNED_PREFIX
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.reorderInQueueEnabledKey
import me.knighthat.appContext
import me.knighthat.component.tab.toolbar.Descriptive
import me.knighthat.component.tab.toolbar.MenuIcon
import me.knighthat.component.tab.toolbar.StateConditional
import me.knighthat.component.tab.toolbar.ToggleableIcon


@Composable
fun pin(
    playlistPreview: PlaylistPreview?,
    playlistId: Long
): MenuIcon = object: StateConditional, MenuIcon, Descriptive {

    override var isActive: Boolean by rememberSaveable( playlistPreview ) { mutableStateOf( isPinned() ) }
    override val iconId: Int = R.drawable.pin
    override val messageId: Int = R.string.info_pin_unpin_playlist
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    fun isPinned(): Boolean {
        return playlistPreview?.playlist
                              ?.name
                              ?.startsWith( PINNED_PREFIX, true ) == true
    }

    override fun onShortClick() {
        transaction {
            val playlistName = playlistPreview?.playlist?.name ?: return@transaction
            if( playlistName.startsWith( PINNED_PREFIX ) )
                Database.unPinPlaylist( playlistId )
            else
                Database.pinPlaylist( playlistId )

            isActive = isPinned()
        }
    }
}

@Composable
fun positionLock(
    sortOrder: SortOrder
): MenuIcon = object: ToggleableIcon, MenuIcon, Descriptive {

    override val iconIdOff: Int = R.drawable.unlocked
    override val iconId: Int = R.drawable.locked
    override val messageId: Int = R.string.info_lock_unlock_reorder_songs
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    /**
     * If [isVisible] equals `true` then user CANNOT change songs' positions
     */
    override var isVisible: Boolean by rememberPreference( reorderInQueueEnabledKey, true )

    override fun onShortClick() {
        if( sortOrder == SortOrder.Ascending )
            isVisible = !isVisible
        else
            SmartMessage(
                message = appContext().resources.getString( R.string.info_reorder_is_possible_only_in_ascending_sort ),
                context = appContext()
            )
    }
}
