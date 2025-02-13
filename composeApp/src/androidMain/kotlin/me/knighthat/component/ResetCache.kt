package me.knighthat.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.ui.components.tab.toolbar.ConfirmDialog
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.ui.components.themed.SmartMessage

@UnstableApi
class ResetCache private constructor(
    activeState: MutableState<Boolean>,
    private val binder: PlayerServiceModern.Binder?,
    private val getSongs: () -> List<Song>
): MenuIcon, Descriptive, ConfirmDialog {

    companion object {
        @Composable
        operator fun invoke( getSongs: () -> List<Song> ) =
            ResetCache(
                remember { mutableStateOf(false) },
                LocalPlayerServiceBinder.current,
                getSongs
            )
    }

    override val iconId: Int
        get() = R.drawable.refresh_circle
    // TODO Insert custom message to strings.xml
    override val messageId: Int = R.string.info_clean_cached_congs
    override val menuIconTitle: String
        @Composable
        // TODO Insert custom message to strings.xml
        get() = "Reset cache"
    override val dialogTitle: String
        @Composable
        // TODO Insert custom message to strings.xml
        get() = "Are you sure"

    override var isActive: Boolean by activeState

    override fun onShortClick() = super.onShortClick()

    override fun onConfirm() {
        getSongs().forEach { song ->
            // Transaction is placed inside the loop
            // so when ONE song fails, the other won't be affected
            Database.asyncTransaction {
                binder?.cache?.removeResource( song.id )
                binder?.downloadCache?.removeResource( song.id )
                deleteFormat( song.id )
                resetContentLength( song.id )
            }
        }

        SmartMessage(
            message = appContext().resources.getString( R.string.done ),
            context = appContext()
        )
    }
}