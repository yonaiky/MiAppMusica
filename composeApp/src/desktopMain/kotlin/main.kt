import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import it.fast4x.rimusic.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "RiMusic MP",
    ) {
        App()
    }
}