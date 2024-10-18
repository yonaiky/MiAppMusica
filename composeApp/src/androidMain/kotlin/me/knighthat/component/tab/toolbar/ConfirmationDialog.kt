package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.ui.components.themed.ConfirmationDialog

interface ConfirmationDialog: Dialog {

    /**
     * What happens when user hits "Confirm" button.
     * <p>
     */
    fun onConfirm()

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

        ConfirmationDialog(
            text = stringResource( this.titleId ),
            onDismiss = ::onDismiss,
            onConfirm = ::onConfirm
        )
    }
}