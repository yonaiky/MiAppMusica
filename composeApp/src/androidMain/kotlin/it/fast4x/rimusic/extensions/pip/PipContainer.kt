package it.fast4x.rimusic.extensions.pip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import it.fast4x.rimusic.utils.PinchDirection
import it.fast4x.rimusic.utils.enablePictureInPictureKey
import it.fast4x.rimusic.utils.pinchToToggle
import it.fast4x.rimusic.utils.rememberPreference

@Composable
fun PipContainer(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    onPipOutAction: () -> Unit = {},
    content: @Composable () -> Unit
){
    val pipHandler = rememberPipHandler()

    Pip(
        numerator = 1,
        denominator = 1,
        modifier = modifier
    ) {
        Box(
            modifier = modifier
                .pinchToToggle(
                    key = enable,
                    direction = PinchDirection.Out,
                    threshold = 1.05f,
                    onPinch = { onPipOutAction() }
                )
                .pinchToToggle(
                    key = enable,
                    direction = PinchDirection.In,
                    threshold = .95f,
                    onPinch = { pipHandler.enterPictureInPictureMode() }
                )

        ) {
            content()
        }
    }
}
