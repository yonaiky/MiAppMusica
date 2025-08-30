package it.fast4x.rimusic.extensions.games.pacman

import android.util.Log
import android.util.Range
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.fast4x.rimusic.extensions.games.pacman.utils.GameConstants.incrementValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val logTag = "GameViewModel"
    private var leftPress: Boolean = false
    private var rightPress: Boolean = false
    private var downPress: Boolean = false
    private var upPress: Boolean = false


    /* handles the direction the character is facing
         start angle
         when going left = 25f
         when going right = 200f
         when going down = 100f
         when going up = 280f
          */
    var characterStartAngle by mutableFloatStateOf( 25f )


    // handle presses
    fun rightPress(characterXOffset: MutableState<Float>, characterYOffset: MutableState<Float>) {
        rightPress = true
        characterStartAngle = 25f       // change direction character is facing
        viewModelScope.launch {
            while (rightPress) {
                delay(500)
                // move character to opposite wall
                if (characterXOffset.value > 315f) characterXOffset.value = -400f

                // implement barrier constraints

                if (
                //Top Right
                    Range.create(-310f, -225f).contains(characterXOffset.value) &&
                    Range.create(-975f, -900f).contains(characterYOffset.value) ||
                    // Top Left
                    Range.create(75f, 150f).contains(characterXOffset.value) &&
                    Range.create(-975f, -900f).contains(characterYOffset.value) ||
                    // EnemyBox
                    Range.create(-150f, -75f).contains(characterXOffset.value) &&
                    Range.create(-450f, -375f).contains(characterYOffset.value) ||
                    // Bottom Left
                    Range.create(-310f, -225f).contains(characterXOffset.value) &&
                    Range.create(-150f, -75f).contains(characterYOffset.value) ||
                    // Bottom Right
                    Range.create(75f, 150f).contains(characterXOffset.value) &&
                    Range.create(-150f, -75f).contains(characterYOffset.value)

                ) characterXOffset.value += 0f else characterXOffset.value += incrementValue

                Log.d(
                    logTag,
                    "rightpress: x:  ${characterXOffset.value} y: ${characterYOffset.value}"
                )

            }
        }
    }

    fun leftPress(characterXOffset: MutableState<Float>, characterYOffset: MutableState<Float>) {
        leftPress = true
        characterStartAngle = 200f          // change direction character is facing
        viewModelScope.launch {
            while (leftPress) {
                delay(500)
                // move character to opposite wall
                if (characterXOffset.value <= -290f) characterXOffset.value = +450f

                // implement barrier constraints

                if (
                //Top Right
                    Range.create(350f, 425f).contains(characterXOffset.value) &&
                    Range.create(-975f, -900f).contains(characterYOffset.value) ||
                    // Top Left
                    Range.create(-150f, -75f).contains(characterXOffset.value) &&
                    Range.create(-975f, -900f).contains(characterYOffset.value) ||
                    // EnemyBox
                    Range.create(75f, 150f).contains(characterXOffset.value) &&
                    Range.create(-450f, -375f).contains(characterYOffset.value) ||
                    // Bottom Left
                    Range.create(-150f, -75f).contains(characterXOffset.value) &&
                    Range.create(-150f, -75f).contains(characterYOffset.value) ||
                    // Bottom Right
                    Range.create(225f, 300f).contains(characterXOffset.value) &&
                    Range.create(-150f, -75f).contains(characterYOffset.value)

                ) characterXOffset.value -= 0f else characterXOffset.value -= incrementValue

                Log.d(
                    logTag,
                    "leftPress: X: ${characterXOffset.value} Y: ${characterYOffset.value}"
                )

            }
        }
    }

    fun upPress(characterYOffset: MutableState<Float>, characterXOffset: MutableState<Float>) {
        upPress = true
        characterStartAngle = 280f       // change direction character is facing
        viewModelScope.launch {
            while (upPress) {
                delay(500)

                // implement barrier constraints

                if (
                // keep inside border
                    characterYOffset.value <= -1000f ||
                    //Top Right
                    Range.create(150f, 300f).contains(characterXOffset.value) &&
                    Range.create(-975f, -900f).contains(characterYOffset.value) ||
                    // Top Left
                    Range.create(-225f, -75f).contains(characterXOffset.value) &&
                    Range.create(-975f, -900f).contains(characterYOffset.value) ||
                    // EnemyBox
                    Range.create(-75f, 150f).contains(characterXOffset.value) &&
                    Range.create(-375f, -300f).contains(characterYOffset.value) ||
                    // Bottom Left
                    Range.create(-225f, -0f).contains(characterXOffset.value) &&
                    Range.create(-150f, -75f).contains(characterYOffset.value) ||
                    // Bottom Right
                    Range.create(150f, 300f).contains(characterXOffset.value) &&
                    Range.create(-150f, -75f).contains(characterYOffset.value)

                ) characterYOffset.value -= 0f else characterYOffset.value -= incrementValue

                Log.d(logTag, "UpPress: Y: ${characterYOffset.value} x: ${characterXOffset.value}")
            }
        }
    }

    fun downPress(characterYOffset: MutableState<Float>, characterXOffset: MutableState<Float>) {
        downPress = true
        characterStartAngle = 100f       // change direction character is facing
        viewModelScope.launch {
            while (downPress) {
                delay(500)

                if (
                // keep inside border
                    characterYOffset.value >= 0f ||
                    //Top Right
                    Range.create(125f, 300f).contains(characterXOffset.value) &&
                    Range.create(-1050f, -825f).contains(characterYOffset.value) ||
                    // Top Left
                    Range.create(-250f, -75f).contains(characterXOffset.value) &&
                    Range.create(-1050f, -825f).contains(characterYOffset.value) ||
                    // EnemyBox
                    Range.create(-75f, 150f).contains(characterXOffset.value) &&
                    Range.create(-450f, -375f).contains(characterYOffset.value) ||
                    // Bottom Left
                    Range.create(-225f, -0f).contains(characterXOffset.value) &&
                    Range.create(-225f, -200f).contains(characterYOffset.value) ||
                    // Bottom Right
                    Range.create(150f, 300f).contains(characterXOffset.value) &&
                    Range.create(-225f, -200f).contains(characterYOffset.value)

                ) characterYOffset.value += 0f else characterYOffset.value += incrementValue

                Log.d(logTag, "downPress: y: ${characterYOffset.value}, x: ${characterXOffset.value}")

            }
        }
    }

//Cancel presses

    fun releaseLeft() {
        leftPress = false
    }

    fun releaseRight() {
        rightPress = false
    }

    fun releaseUp() {
        upPress = false
    }

    fun releaseDown() {
        downPress = false
    }


}