package me.knighthat.component.menu.song

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.R
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.ui.components.themed.IDialog

class RenameSongDialog private constructor(
    valueState: MutableState<String>,
    activeState: MutableState<Boolean>,
    private val song: Song
): IDialog, MenuIcon, Descriptive{

    companion object {
        @Composable
        operator fun invoke( song: Song ): RenameSongDialog =
            RenameSongDialog(
                valueState = remember( song.title ) { mutableStateOf(song.title) },
                activeState = remember { mutableStateOf(false) },
                song = song
            )
    }

    override var isActive: Boolean by activeState
    override val iconId: Int = R.drawable.title_edit
    override val messageId: Int = R.string.update_title
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )
    override val dialogTitle: String
        @Composable
        get() = menuIconTitle

    override var value: String by valueState

    override fun onShortClick() = super.onShortClick()

    override fun onSet( newValue: String ) {
        if( newValue.isEmpty() ) return

        Database.asyncTransaction {
            updateSongTitle( song.id, newValue )
        }
    }
}