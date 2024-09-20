import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import database.MusicDatabaseDesktop
import it.fast4x.rimusic.App
import org.jetbrains.compose.resources.painterResource
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.app_icon

fun main() = application {
    Window(
       icon = painterResource(Res.drawable.app_icon),
        onCloseRequest = ::exitApplication,
        state = WindowState(
            placement = WindowPlacement.Maximized,
        ),
        title = "RiMusic MP",
    ) {
        App(MusicDatabaseDesktop)
    }
}