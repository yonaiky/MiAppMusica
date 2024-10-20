package it.fast4x.rimusic.enums

import androidx.media3.common.Player

enum class QueueLoopType {
    Default,
    RepeatOne,
    RepeatAll;

    val type: Int
        get() = when (this) {
        Default -> Player.REPEAT_MODE_OFF
        RepeatOne -> Player.REPEAT_MODE_ONE
        RepeatAll -> Player.REPEAT_MODE_ALL
    }
}