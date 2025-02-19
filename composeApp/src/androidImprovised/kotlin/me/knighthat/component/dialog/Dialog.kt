package me.knighthat.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.utils.bold

interface Dialog {

    companion object {
        /**
         * Space between top and bottom line of dialog.
         */
        const val VERTICAL_PADDING = 15
    }

    @get:Composable
    val dialogTitle: String

    /**
     * Whether the dialog should be shown or not.
     *
     * **Avoid** changing this value directly, instead,
     * use [showDialog] and/or [hideDialog] to
     * modify dialog's visibility.
     */
    var isActive: Boolean

    fun showDialog() { isActive = true }

    fun hideDialog() { isActive = false }

    @Composable
    fun DialogBody()

    @Composable
    fun Render() {
        if( !isActive ) return

        val screenWidthDp = LocalConfiguration.current.screenWidthDp

        androidx.compose.ui.window.Dialog( ::hideDialog ) {
            Column(
                modifier = Modifier.padding( horizontal = (screenWidthDp * .05f).dp )
                                   .background(
                                       color = colorPalette().background0,
                                       shape = RoundedCornerShape( 8.dp )
                                   ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer( Modifier.height( VERTICAL_PADDING.dp ) )

                BasicText(
                    text = dialogTitle,
                    style = typography().m.bold,
                    modifier = Modifier.padding(
                                           bottom = 8.dp,
                                           start = (screenWidthDp * .05f).dp
                                       )
                                       .fillMaxWidth( .8f )
                                       .align( Alignment.Start )
                )

                HorizontalDivider(
                    Modifier.height( 2.dp )
                            .fillMaxWidth( .95f )
                            .background( colorPalette().textDisabled )
                )

                DialogBody()

                Spacer( Modifier.height( VERTICAL_PADDING.dp ) )
            }
        }
    }
}