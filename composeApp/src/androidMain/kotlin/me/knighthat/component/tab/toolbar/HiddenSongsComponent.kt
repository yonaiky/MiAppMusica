package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import it.fast4x.rimusic.R

class HiddenSongsComponent private constructor(
    private val showHiddenState: MutableState<Int>
): ToggleableIcon, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init() = HiddenSongsComponent(
            remember { mutableIntStateOf(0) }   // False equivalent
        )
    }

    var showHidden: Int = showHiddenState.value
        set(value) {
            showHiddenState.value = value
            field = value
        }
    override var isVisible: Boolean = showHidden == -1
        set(value) {
            // Clarification:
            // showHidden == -1 means show hidden songs
            // showHidden == 0 means hide them
            showHidden = if( value ) -1 else 0
            field = value
        }
    override val iconIdOff: Int = R.drawable.eye_off
    override val iconId: Int = R.drawable.eye
    override val messageId: Int = R.string.info_show_hide_hidden_songs

    override fun onShortClick() { isVisible = !isVisible }
}