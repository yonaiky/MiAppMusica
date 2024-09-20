package it.fast4x.rimusic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import database.MusicDatabaseDao
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.bodies.NextBody
import it.fast4x.innertube.requests.relatedPage
import it.fast4x.rimusic.ui.DesktopApp
import it.fast4x.rimusic.ui.theme.DesktopTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.compose_multiplatform
import rimusic.composeapp.generated.resources.restart_app_please
import rimusic.composeapp.generated.resources.tips

@Composable
fun App(db: MusicDatabaseDao) {

    DesktopTheme {
        DesktopApp()
    }


    /*
    MaterialTheme {
        var relatedPage = remember { mutableStateOf<Innertube.RelatedPage?>(null) }
        LaunchedEffect(Unit) {
            Innertube.relatedPage(
                NextBody(
                    videoId = ("HZnNt9nnEhw")
                )
            )?.onSuccess {
                relatedPage.value = it
            }
        }

        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                    Text(stringResource(Res.string.tips))
                    BasicText(text = stringResource(Res.string.restart_app_please))
                    BasicText(text = "Songs: ${relatedPage.value?.songs?.size.toString()}")
                    BasicText(text = "commonDao db song ${db.getAll()}")
                }
            }

        }
    }
     */
}