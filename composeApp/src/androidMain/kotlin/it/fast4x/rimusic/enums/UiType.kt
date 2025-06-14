package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import app.kreate.android.Settings
import me.knighthat.enums.TextView

enum class UiType: TextView {
    RiMusic,
    ViMusic;

    companion object {

        fun current(): UiType = Settings.MAIN_THEME.value
    }

    override val text: String
        @Composable
        get() = this.name

    fun isCurrent(): Boolean = current() == this

    fun isNotCurrent(): Boolean = !isCurrent()
}