package it.fast4x.rimusic.extensions.audiowave

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
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.extensions.visualizer.audio.VisualizerComputer
import it.fast4x.rimusic.extensions.visualizer.audio.VisualizerData
import it.fast4x.rimusic.service.PlayerService
import it.fast4x.rimusic.ui.components.themed.SmartToast
import it.fast4x.rimusic.utils.toast
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
        SmartToast(context.resources.getString(R.string.require_mic_permission), type = PopupType.Info)
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
