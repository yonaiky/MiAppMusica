package it.vfsfitvnm.vimusic.extensions.visualizer

import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import kotlin.ByteArray
import kotlin.Int

@OptIn(UnstableApi::class)
@Composable
fun GetVisualizer() {
    val binder = LocalPlayerServiceBinder.current ?: return
    val rate = Visualizer.getMaxCaptureRate()
    val audioOutput = binder.player.audioSessionId.let { Visualizer(it) } // get output audio stream
    audioOutput.setDataCaptureListener(object : OnDataCaptureListener {
        override fun onWaveFormDataCapture(
            visualizer: Visualizer,
            waveform: ByteArray,
            samplingRate: Int
        ) {
            val intensity = (waveform[0].toFloat() + 128f) / 256
            println("mediaItem Visualizer intensity $intensity")
            println("mediaItem Visualizer waveform ${waveform}")
        }

        override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
            println("mediaItem Visualizer fft $fft")
        }

    }, rate, true, false) // waveform not freq data

    println("mediaItem Visualizer rate ${Visualizer.getMaxCaptureRate()}")
    audioOutput.setEnabled(true)
}
