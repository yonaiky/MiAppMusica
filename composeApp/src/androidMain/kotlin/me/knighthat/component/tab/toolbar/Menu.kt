package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import it.fast4x.rimusic.ui.components.MenuState

interface Menu: Icon {

    val menuState: MenuState

    @Composable
    fun MenuComponent()

    override fun onShortClick() = menuState.display { MenuComponent() }
}