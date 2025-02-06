package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import it.fast4x.rimusic.enums.MenuStyle
import it.fast4x.rimusic.ui.components.MenuState

interface Menu {

    val menuState: MenuState
    val styleState: MutableState<MenuStyle>

    @Composable
    fun ListMenu()

    @Composable
    fun GridMenu()

    @Composable
    fun MenuComponent()

    fun openMenu() = menuState.display { MenuComponent() }

    fun closeMenu() = menuState.hide()
}