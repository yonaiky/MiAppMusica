package me.knighthat.component.tab.toolbar

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.ui.components.themed.InputTextDialog

interface InputDialog: Dialog {

    val placeHolder: Int
        @StringRes
        get() = titleId
    val defValue: String
        get() = ""

    /**
     * What happens when user hits "Confirm" button
     */
    fun onSet( newValue: String )

    /**
     * Triggered when user interacts with back button
     * or with something outside of this menu's scope
     * <p>
     * By default, this will turn off the dialog
     */
    fun onDismiss() { toggleState.value = false }

    @Composable
    override fun Render() {
        if( !toggleState.value ) return

        InputTextDialog(
            onDismiss = ::onDismiss,
            title = stringResource( this.titleId ),
            value = this.defValue,
            placeholder = stringResource( this.placeHolder ),
            setValue = ::onSet
        )
    }
}