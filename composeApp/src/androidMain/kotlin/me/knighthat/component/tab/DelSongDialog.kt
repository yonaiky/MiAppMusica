package me.knighthat.component.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.models.SongEntity
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import me.knighthat.appContext
import me.knighthat.component.tab.toolbar.ConfirmDialog
import me.knighthat.component.tab.toolbar.Descriptive
import me.knighthat.component.tab.toolbar.MenuIcon
import java.util.Optional

@UnstableApi
open class DelSongDialog protected constructor(
    protected val activeState: MutableState<Boolean>,
    protected val menuState: MenuState,
    protected val binder: PlayerServiceModern.Binder?
): ConfirmDialog, Descriptive, MenuIcon {

    companion object {
        @JvmStatic
        @Composable
        fun init() = DelSongDialog(
            rememberSaveable { mutableStateOf( false ) },
            LocalMenuState.current,
            LocalPlayerServiceBinder.current
        )
    }

    var song: Optional<SongEntity> = Optional.empty()
    override val messageId: Int = R.string.delete
    override val iconId: Int = R.drawable.trash
    override var isActive: Boolean = activeState.value
        set(value) {
            activeState.value = value
            field = value
        }
    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.delete_song )
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() = super.onShortClick()

    override fun onDismiss() {
        // Always override current value with empty Optional
        // to prevent unwanted outcomes
        song = Optional.empty()
        super.onDismiss()
    }

    override fun onConfirm() {
        song.ifPresent {
            transaction {
                menuState.hide()
                binder?.cache?.removeResource(it.song.id)
                binder?.downloadCache?.removeResource(it.song.id)
                Database.delete(it.song)
                Database.deleteSongFromPlaylists(it.song.id)
                Database.deleteFormat(it.song.id)
            }
            SmartMessage(
                message = appContext().resources.getString(R.string.deleted),
                context = appContext()
            )
        }

        onDismiss()
    }
}