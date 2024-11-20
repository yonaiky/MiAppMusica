package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.Menu
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import me.knighthat.enums.HomeItemSize
import me.knighthat.preference.Preference

class ItemSize private constructor(
    val menuState: MenuState,
    private val sizeState: MutableState<HomeItemSize>
): MenuIcon {

    companion object {
        @JvmStatic
        @Composable
        fun init(key: Preference.Key<HomeItemSize>): ItemSize =
            ItemSize(
                LocalMenuState.current,
                Preference.remember(key)
            )
    }

    override val iconId: Int = R.drawable.resize
    override val menuIconTitle: String
        @Composable
        get() = stringResource( R.string.size )

    var size: HomeItemSize = sizeState.value
        set(value) {
            sizeState.value = value
            field = value
        }

    @Composable
    private fun Entry( size: HomeItemSize ) {
        MenuEntry(
            size.iconId,
            stringResource( size.textId ),
            onClick = {
                sizeState.value = size
                menuState::hide
            }
        )
    }

    override fun onShortClick() {
        menuState.display {
            Menu {
                HomeItemSize.entries.forEach { Entry(it) }
            }
        }
    }
}