package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import it.fast4x.rimusic.R
import it.fast4x.rimusic.utils.autosyncKey
import it.fast4x.rimusic.utils.rememberPreference

class SyncComponent private constructor(
    private val activeState: MutableState<Boolean>
): StateConditional, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init() = SyncComponent(
            rememberPreference(autosyncKey, false)
        )
    }

    override var isActive: Boolean = activeState.value
        set(value) {
            activeState.value = value
            field = value
        }
    override val iconId: Int = R.drawable.sync
    override val messageId: Int = R.string.autosync

    override fun onShortClick() { isActive = !isActive }
}