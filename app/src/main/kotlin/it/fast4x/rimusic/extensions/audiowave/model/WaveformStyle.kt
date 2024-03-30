package it.fast4x.rimusic.extensions.audiowave.model

import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

data class WaveformStyle(
    val label: String,
    val style: DrawStyle
)

fun getMockStyles() = listOf(
    WaveformStyle("Fill", Fill),
    WaveformStyle("Stroke", Stroke(width = 1f)),
    WaveformStyle("Dash", Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5F, 5F))))
)