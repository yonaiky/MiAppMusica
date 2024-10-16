package me.knighthat.component.tab.toolbar

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import me.knighthat.component.header.TabToolBar

interface Dialog: Button {

    val context: Context
    val toggleState: MutableState<Boolean>

    @get:DrawableRes
    val iconId: Int
    @get:StringRes
    val titleId: Int
    @get:StringRes
    val messageId: Int

    @Composable
    fun Render()

    /**
     * What happens when user taps on icon.
     * <p>
     * By default, this action only flip value
     * of toggleState.
     */
    fun onShortClick() { toggleState.value = !toggleState.value }

    /**
     * What happens when user holds this icon for a while.
     * <p>
     * By default, this will send out message
     */
    fun onLongClick() =
        SmartMessage(
            context.resources.getString( messageId ),
            context = context
        )


    @Composable
    override fun ToolBarButton() {
        TabToolBar.Icon(
            iconId =  this.iconId,
            onShortClick = ::onShortClick,
            onLongClick = ::onLongClick
        )
    }
}