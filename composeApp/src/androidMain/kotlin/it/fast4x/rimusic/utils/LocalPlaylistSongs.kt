package it.fast4x.rimusic.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.kreate.android.R
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.PIPED_PREFIX
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.cleanPrefix
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.PipedSession
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.tab.toolbar.ConfirmDialog
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.DualIcon
import it.fast4x.rimusic.ui.components.tab.toolbar.DynamicColor
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.ui.components.themed.DeleteDialog
import it.fast4x.rimusic.ui.components.themed.IDialog
import kotlinx.coroutines.CoroutineScope
import me.knighthat.utils.Toaster
import java.util.UUID

class PositionLock private constructor(
    private val iconState: MutableState<Boolean>,
    private val colorState: MutableState<Boolean>
): MenuIcon, DualIcon, DynamicColor, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init( sortOrder: SortOrder ) = PositionLock(
            rememberPreference( reorderInQueueEnabledKey, true ),
            rememberSaveable( sortOrder ) { mutableStateOf( sortOrder == SortOrder.Ascending ) }
        )
    }

    override val secondIconId: Int = R.drawable.unlocked
    override val iconId: Int = R.drawable.locked
    override val messageId: Int = R.string.info_lock_unlock_reorder_songs
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override var isFirstIcon: Boolean = iconState.value
        set(value) {
            iconState.value = value
            field = value
        }
    override var isFirstColor: Boolean = colorState.value
        set(value) {
            colorState.value = value
            field = value
        }

    fun isLocked(): Boolean = isFirstIcon

    override fun onShortClick() {
        if( !isFirstColor )
            Toaster.i( R.string.info_reorder_is_possible_only_in_ascending_sort )
        else
            isFirstIcon = !isFirstIcon
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun DeletePlaylist(
    activeState: MutableState<Boolean> = rememberSaveable { mutableStateOf( false ) },
    menuState: MenuState = LocalMenuState.current,
    onEvent: DeleteDialog.() -> Unit
): DeleteDialog = object : DeleteDialog( activeState, menuState ) {

    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.delete_playlist )

    override fun onConfirm() = onEvent()
}

@SuppressLint("ComposableNaming")
@Composable
fun Reposition(playlistId: Long): MenuIcon = object: ConfirmDialog, MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val messageId: Int = R.string.renumber_songs_positions
    override val iconId: Int = R.drawable.position
    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.do_you_really_want_to_renumbering_positions_in_this_playlist )
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override var isActive: Boolean by rememberSaveable { mutableStateOf( false ) }

    override fun onShortClick() = super.onShortClick()

    override fun onConfirm() {
        Database.asyncTransaction {
            // [List.shuffled()] is extremely slow. Furthermore, passing
            // songs here will create a capture of the list, that requires
            // memory and the list is incomplete, thus, results in the
            // shuffle only happens to a few items.
            songPlaylistMapTable.shufflePositions( playlistId )
        }

        onDismiss()
        menuState.hide()
    }
}

class RenameDialog private constructor(
    private val menuState: MenuState,
    private val playlistNameState: MutableState<String>,
    private val activeState: MutableState<Boolean>,
    private val pipedSession: PipedSession,
    private val coroutineScope: CoroutineScope,
    private val isPipedEnabled: () -> Boolean,
    private val playlistPreview: () -> PlaylistPreview?
): IDialog, Descriptive, MenuIcon {

    companion object {
        @JvmStatic
        @Composable
        fun init(
            pipedSession: PipedSession,
            coroutineScope: CoroutineScope,
            isPipedEnabled: () -> Boolean,
            playlistNameState: MutableState<String>,
            playlistPreview: () -> PlaylistPreview?
        ) = RenameDialog(
            LocalMenuState.current,
            playlistNameState,
            rememberSaveable { mutableStateOf( false ) },
            pipedSession,
            coroutineScope,
            isPipedEnabled,
            playlistPreview
        )
    }

    override val messageId: Int = R.string.rename
    override val iconId: Int = R.drawable.title_edit
    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.enter_the_playlist_name )
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    var playlistName: String = playlistNameState.value
        set(value) {
            playlistNameState.value = value
            field = value
        }
    override var isActive: Boolean = activeState.value
        set(value) {
            activeState.value = value
            field = value
        }
    override var value: String = playlistNameState.value
        set(value) {
            playlistName = value
            field = value
        }

    override fun onShortClick() = super.onShortClick()

    override fun onSet( newValue: String ) {
        val playlist = playlistPreview()?.playlist ?: return

        val isPipedPlaylist =
            playlist.name.startsWith(PIPED_PREFIX)
                    && isPipedEnabled()
                    && pipedSession.token.isNotEmpty()
        val prefix = if( isPipedPlaylist ) PIPED_PREFIX else ""

        Database.asyncTransaction {
            playlist.copy( name = "$prefix$newValue" )
                    .let( playlistTable::update )
        }

        if ( isPipedPlaylist )
            renamePipedPlaylist(
                context = appContext(),
                coroutineScope = coroutineScope,
                pipedSession = pipedSession.toApiSession(),
                id = UUID.fromString( cleanPrefix(playlist.browseId ?: "") ),
                name = "$PIPED_PREFIX$newValue"
            )

        onDismiss()
        menuState.hide()
    }
}