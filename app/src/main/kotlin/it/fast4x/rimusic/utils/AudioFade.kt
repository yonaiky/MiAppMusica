package it.fast4x.rimusic.utils

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.service.PlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


var volume = 0f

@OptIn(UnstableApi::class)
fun startFadeIn(binder: PlayerService.Binder, fadeDuration: Int = 5000) {
    //val fadeDuration = 10000 //The duration of the fade
    //The amount of time between volume changes. The smaller this is, the smoother the fade
    val fadeInterval = 100
    val maxVolume = 1 //The volume will increase from 0 to 1
    val numberOfSteps = fadeDuration / fadeInterval //Calculate the number of fade steps
    //Calculate by how much the volume changes each step
    val deltaVolume = maxVolume / numberOfSteps.toFloat()

    //Create a new Timer and Timer task to run the fading outside the main UI thread
    val timer = Timer(true)
    val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            fadeInStep(binder,deltaVolume) //Do a fade step
            //Cancel and Purge the Timer if the desired volume has been reached
            if (volume >= 1f) {
                timer.cancel()
                timer.purge()
            }
        }
    }

    val coroutineScope = CoroutineScope(Dispatchers.IO) + Job()
    coroutineScope.launch {
        withContext(Dispatchers.Main) {
            timer.schedule(timerTask, fadeInterval.toLong(), fadeInterval.toLong())
        }
    }
}

@OptIn(UnstableApi::class)
private fun fadeInStep(binder: PlayerService.Binder, deltaVolume: Float) {
    val coroutineScope = CoroutineScope(Dispatchers.IO) + Job()
    coroutineScope.launch {
        withContext(Dispatchers.Main) {
            binder.player.volume = volume
        }
    }
    volume += deltaVolume
}

@OptIn(UnstableApi::class)
fun startFadeOut(binder: PlayerService.Binder, fadeDuration: Int = 5000) {
    //val fadeDuration = 10000 //The duration of the fade
    //The amount of time between volume changes. The smaller this is, the smoother the fade
    val fadeInterval = 100
    val maxVolume = 1 //The volume will increase from 0 to 1
    val numberOfSteps = fadeDuration / fadeInterval //Calculate the number of fade steps
    //Calculate by how much the volume changes each step
    val deltaVolume = maxVolume / numberOfSteps.toFloat()

    //Create a new Timer and Timer task to run the fading outside the main UI thread
    val timer = Timer(true)
    val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            fadeOutStep(binder,deltaVolume) //Do a fade step
            //Cancel and Purge the Timer if the desired volume has been reached
            if (volume <= 0f) {
                timer.cancel()
                timer.purge()
            }
        }
    }

    val coroutineScope = CoroutineScope(Dispatchers.IO) + Job()
    coroutineScope.launch {
        withContext(Dispatchers.Main) {
            timer.schedule(timerTask, fadeInterval.toLong(), fadeInterval.toLong())
        }
    }

}

@OptIn(UnstableApi::class)
private fun fadeOutStep(binder: PlayerService.Binder, deltaVolume: Float) {
    val coroutineScope = CoroutineScope(Dispatchers.IO) + Job()
    coroutineScope.launch {
        withContext(Dispatchers.Main) {
            binder.player.volume = volume
        }
    }
    volume -= deltaVolume
}