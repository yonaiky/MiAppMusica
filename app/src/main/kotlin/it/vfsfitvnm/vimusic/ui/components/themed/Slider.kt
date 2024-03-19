package it.vfsfitvnm.vimusic.ui.components.themed

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.utils.semiBold

@Composable
fun SliderDialog(
    onDismiss: () -> Unit,
    title: String,
    provideState: @Composable () -> MutableState<Float>,
    onSlideCompleted: (newState: Float) -> Unit,
    min: Float,
    max: Float,
    modifier: Modifier = Modifier,
    toDisplay: @Composable (Float) -> String = { it.toString() },
    @IntRange(from = 0) steps: Int = 0,
    content: @Composable () -> Unit = { }
) = Dialog(onDismissRequest = onDismiss) {
    val (colorPalette, typography) = LocalAppearance.current
    var state by provideState()

    Column(
        modifier = modifier
            .padding(all = 48.dp)
            .background(color = colorPalette.background1, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 16.dp)
    ) {
        BasicText(
            text = title,
            style = typography.s.semiBold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
        )

        Slider(
            state = state,
            setState = { state = it },
            onSlideCompleted = { onSlideCompleted(state) },
            range = min..max,
            steps = steps,
            modifier = Modifier
                .height(36.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        BasicText(
            text = toDisplay(state),
            style = typography.s.semiBold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
        )

        //content()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = { state = 1f },
                icon = R.drawable.close,
                color = colorPalette.text
            )
            IconButton(
                onClick = onDismiss,
                icon = R.drawable.checkmark,
                color = colorPalette.accent
            )
        }
    }
}

@Composable
fun Slider(
    state: Float,
    setState: (Float) -> Unit,
    onSlideCompleted: () -> Unit,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    @IntRange(from = 0) steps: Int = 0
) {
    val (colorPalette) = LocalAppearance.current

    androidx.compose.material.Slider(
        value = state,
        onValueChange = setState,
        onValueChangeFinished = onSlideCompleted,
        valueRange = range,
        modifier = modifier,
        steps = steps,
        colors = SliderDefaults.colors(
            thumbColor = colorPalette.onAccent,
            activeTrackColor = colorPalette.accent,
            inactiveTrackColor = colorPalette.text.copy(alpha = 0.75f)
        )
    )

}