package it.fast4x.rimusic.extensions.audiowave

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.model.WaveformAlignment
import it.fast4x.rimusic.R
import it.fast4x.rimusic.extensions.audiowave.model.AudioWaveformUiState
import it.fast4x.rimusic.extensions.audiowave.model.getMockPalettes
import it.fast4x.rimusic.extensions.audiowave.model.getMockStyles


@Composable
fun AudioWaveformScreen(
    uiState: AudioWaveformUiState,
    onPlayClicked: () -> Unit,
    onProgressChange: (Float) -> Unit,
) {
    val colorPalettes = getMockPalettes()
    val waveformStyles = getMockStyles()
    var colorPaletteIndex by remember { mutableStateOf(0) }
    var waveformStyle by remember { mutableStateOf(waveformStyles.first()) }
    var waveformAlignment by remember { mutableStateOf(WaveformAlignment.Center) }
    var amplitudeType by remember { mutableStateOf(AmplitudeType.Avg) }
    var spikeWidth by remember { mutableStateOf(4F) }
    var spikePadding by remember { mutableStateOf(2F) }
    var spikeCornerRadius by remember { mutableStateOf(2F) }
    val playButtonIcon by remember(uiState.isPlaying) {
        mutableStateOf(if(uiState.isPlaying) R.drawable.pause else R.drawable.play)
    }
    var scrollEnabled by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .verticalScroll(state = rememberScrollState(), enabled = scrollEnabled)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = uiState.audioDisplayName,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                AudioWaveform(
                    modifier = Modifier.fillMaxWidth(),
                    style = waveformStyle.style,
                    waveformAlignment = waveformAlignment,
                    amplitudeType = amplitudeType,
                    progressBrush = colorPalettes[colorPaletteIndex].progressColor,
                    waveformBrush = colorPalettes[colorPaletteIndex].waveformColor,
                    spikeWidth = Dp(spikeWidth),
                    spikePadding = Dp(spikePadding),
                    spikeRadius = Dp(spikeCornerRadius),
                    progress = uiState.progress,
                    amplitudes = uiState.amplitudes,
                    onProgressChange = {
                        scrollEnabled = false
                        onProgressChange(it)
                    },
                    onProgressChangeFinished = {
                        scrollEnabled = true
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                LabelSlider(
                    text = "Spike width",
                    value = spikeWidth,
                    onValueChange = { spikeWidth = it },
                    valueRange = 1.dp.value..24.dp.value
                )
                LabelSlider(
                    text = "Spike padding",
                    value = spikePadding,
                    onValueChange = { spikePadding = it },
                    valueRange = 0.dp.value..12.dp.value
                )
                LabelSlider(
                    text = "Spike radius",
                    value = spikeCornerRadius,
                    onValueChange = { spikeCornerRadius = it },
                    valueRange = 0.dp.value..12.dp.value
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WaveformAlignment.entries.forEach {
                        RadioGroupItem(
                            text = it.name,
                            selected = waveformAlignment == it,
                            onClick = { waveformAlignment = it }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AmplitudeType.entries.forEach {
                        RadioGroupItem(
                            text = it.name,
                            selected = amplitudeType == it,
                            onClick = { amplitudeType = it }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    waveformStyles.forEach {
                        RadioGroupItem(
                            text = it.label,
                            selected = it.label == waveformStyle.label,
                            onClick = {
                                waveformStyle = it
                            }
                        )
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                colorPalettes.forEach {
                    ColorPaletteItem(
                        selected = it.label == colorPalettes[colorPaletteIndex].label,
                        progressColor = it.progressColor,
                        waveformColor = it.waveformColor
                    ) {
                        colorPaletteIndex = colorPalettes.indexOf(it)
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = onPlayClicked
            ) {
                Icon(
                    painter = painterResource(id = playButtonIcon),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun LabelSlider(
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    text: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange
        )
    }
}

@Composable
fun RadioGroupItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

@Composable
fun ColorPaletteItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    progressColor: Brush,
    waveformColor: Brush,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Box(modifier = Modifier
            .height(24.dp)
            .weight(1F)
            .background(progressColor)
        )
        Box(modifier = Modifier
            .height(24.dp)
            .weight(1F)
            .background(waveformColor)
        )
    }
}