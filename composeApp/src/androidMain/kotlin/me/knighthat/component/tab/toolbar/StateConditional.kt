package me.knighthat.component.tab.toolbar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import me.knighthat.colorPalette
import me.knighthat.component.header.TabToolBar

interface StateConditional: Icon {

    val colorOff: Color
        @Composable
        get() = colorPalette().textDisabled
    var isActive: Boolean

    @ExperimentalFoundationApi
    @Composable
    override fun ToolBarButton() {
        val state = if( this is ToggleableIcon ) isVisible else isActive

        if( this is Clickable )
            TabToolBar.Toggleable(
                iconId,
                color,
                colorOff,
                isActive,
                state,
                sizeDp,
                modifier,
                this::onShortClick,
                this::onLongClick
            )
        else
            TabToolBar.Toggleable(
                iconId,
                color,
                colorOff,
                isActive,
                state,
                sizeDp,
                modifier,
                this::onShortClick
            )
    }
}