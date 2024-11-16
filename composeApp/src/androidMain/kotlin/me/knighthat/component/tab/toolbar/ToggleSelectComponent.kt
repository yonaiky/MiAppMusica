package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R

class ToggleSelectComponent <T> private constructor(
    private val selectState: MutableState<Boolean>,
    private val items: MutableList<T>
): MenuIcon {

    companion object {
        @JvmStatic
        @Composable
        fun <T> init( items: MutableList<T> ) =
            ToggleSelectComponent(
                remember { mutableStateOf(false) },
                items
            )
    }

    var isActivated: Boolean = selectState.value
        set(value) {
            selectState.value = value
            field = value
        }
    override val menuIconTitle: String
        @Composable
        get() = "${stringResource( R.string.item_select )}/${stringResource( R.string.item_deselect )}"
    override val iconId: Int = R.drawable.checked

    override fun onShortClick() {
        isActivated = !isActivated
        if( !isActivated )
            items.clear()
    }
}