package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import it.fast4x.rimusic.R
import it.fast4x.rimusic.utils.autoShuffleKey
import it.fast4x.rimusic.utils.rememberPreference

class RandomSortComponent private constructor(
    private val autoShuffleState: MutableState<Boolean>
): StateConditional, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init() = RandomSortComponent(
            rememberPreference(autoShuffleKey, false)
        )
    }

    override var isActive: Boolean = autoShuffleState.value
        set(value) {
            autoShuffleState.value = value
            field = value
        }
    override val iconId: Int = R.drawable.random
    override val textId: Int = R.string.random_sorting

    override fun onShortClick() { isActive = !isActive }
}