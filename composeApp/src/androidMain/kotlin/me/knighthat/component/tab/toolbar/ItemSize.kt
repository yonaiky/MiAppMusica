package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.Menu
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import me.knighthat.component.header.TabToolBar
import me.knighthat.enums.HomeItemSize

interface ItemSize: Button {

    val menuState: MenuState
    val sizeState: MutableState<HomeItemSize>

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

    @Composable
    override fun ToolBarButton() {
        TabToolBar.Icon( R.drawable.resize ) {

            menuState.display {
                Menu {
                    HomeItemSize.entries.forEach { Entry(it) }
                }
            }
        }
    }
}