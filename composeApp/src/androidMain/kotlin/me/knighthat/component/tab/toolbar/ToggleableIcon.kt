package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import me.knighthat.component.header.TabToolBar

interface ToggleableIcon: Icon {

    var isVisible: Boolean
    val iconIdOff: Int

    @Composable
    override fun ToolBarButton() {
        if( this is Clickable )
            TabToolBar.Toggleable(
                iconId,
                iconIdOff,
                isVisible,
                color,
                sizeDp,
                modifier,
                this::onShortClick,
                this::onLongClick
            )
        else
            TabToolBar.Toggleable(
                iconId,
                iconIdOff,
                isVisible,
                color,
                sizeDp,
                modifier,
                this::onShortClick
            )
    }
}