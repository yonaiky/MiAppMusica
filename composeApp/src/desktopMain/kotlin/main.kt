import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import database.MusicDatabaseDesktop
import it.fast4x.rimusic.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(
            placement = WindowPlacement.Maximized,
        ),
        title = "RiMusic MP",
    ) {
        App(MusicDatabaseDesktop)
    }
}