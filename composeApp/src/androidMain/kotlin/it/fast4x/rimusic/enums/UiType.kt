package it.fast4x.rimusic.enums

import app.kreate.android.Settings

enum class UiType {
    RiMusic,
    ViMusic;

    companion object {

        fun current(): UiType = Settings.MAIN_THEME.value
    }

    fun isCurrent(): Boolean = current() == this

    fun isNotCurrent(): Boolean = !isCurrent()
}