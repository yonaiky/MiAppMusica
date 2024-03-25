package it.vfsfitvnm.vimusic.extensions.audiowave

import AudioCalculator
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.extensions.visualizer.audio.VisualizerComputer
import it.vfsfitvnm.vimusic.extensions.visualizer.audio.VisualizerData
import it.vfsfitvnm.vimusic.service.PlayerService
import it.vfsfitvnm.vimusic.utils.toast
import kotlin.Int

@OptIn(UnstableApi::class)
fun getAmplitudes (
    context: Context,
    binder: PlayerService.Binder,
    visualizerData: MutableState<VisualizerData>
): List<Int> {
    val activity = context as Activity
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        context.toast(context.resources.getString(R.string.require_mic_permission))
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO), 42
        )
    } else {
        val audioComputer = VisualizerComputer()
        //Log.d("mediaItemEqualizer","EXTERNAL audioSession ${binder?.player?.audioSessionId}")
        binder.player.audioSessionId.let {
            //Log.d("mediaItemEqualizer","internal audioSession ${it}")
            audioComputer.start(audioSessionId = it, onData = { data ->
                //Log.d("mediaItemEqualizer","onData amplitude ${data.amplitude} captureSize ${data.captureSize} rawWaveform ${data.rawWaveform} samplingRate ${data.samplingRate}")
                visualizerData.value = data
            })
        }

        val audioCalculator = AudioCalculator()
        val amplitudes = audioCalculator.getAmplitudes(visualizerData.value.rawWaveform)
        return amplitudes.asList()

    }

    return emptyList()

}
