package it.fast4x.rimusic

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "RiMusic MP",
    ) {
        App()
    }
}