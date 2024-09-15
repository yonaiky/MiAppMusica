import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.icon

import ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "RiMusic Desktop App",
        icon = painterResource(Res.drawable.icon)
    ) {
        App()
    }
}