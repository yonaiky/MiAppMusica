package it.fast4x.rimusic.ui.screens.player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView

import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi

import it.fast4x.rimusic.R
import it.fast4x.rimusic.extensions.exovisualizer.ExoVisualizer
import it.fast4x.rimusic.extensions.exovisualizer.FFTAudioProcessor

@UnstableApi
@Composable
fun ExoVisualizer(
    isDisplayed: Boolean
) {
           println("mediaItem exo visualizer")
            val exoVisualizer =  ExoVisualizer(LocalContext.current)
            exoVisualizer.processor = FFTAudioProcessor()
            AndroidView(
                factory = { context ->
                    println("mediaItem exo visualizer view")
                    val myView = LayoutInflater.from(context).inflate(R.layout.exovisualizer, null, false)
                        .apply {
                            if (parent != null) {
                                (parent as ViewGroup).removeView(this)
                            }
                        }
                    myView.findViewById<ExoVisualizer>(R.id.visualizer)

                },
                update = { view ->
                    // update the view/trigger recomposition
                }
            )


}

