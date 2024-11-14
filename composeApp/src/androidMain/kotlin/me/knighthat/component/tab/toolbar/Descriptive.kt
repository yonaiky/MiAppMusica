package me.knighthat.component.tab.toolbar

import androidx.annotation.StringRes
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import me.knighthat.appContext

interface Descriptive: Clickable {

    @get:StringRes
    val textId: Int

    override fun onLongClick() {
        SmartMessage(
            appContext().resources.getString( textId ),
            context = appContext()
        )
    }
}