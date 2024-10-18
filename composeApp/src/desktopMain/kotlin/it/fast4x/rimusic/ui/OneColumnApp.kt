package it.fast4x.rimusic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.PlayerResponse
import it.fast4x.innertube.models.bodies.PlayerBody
import it.fast4x.innertube.requests.player
import it.fast4x.rimusic.styling.Dimensions.layoutColumnTopPadding
import it.fast4x.rimusic.styling.Dimensions.layoutColumnsHorizontalPadding
import it.fast4x.rimusic.utils.getPipedSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import player.frame.FrameContainer
import player.frame.FramePlayer
import vlcj.VlcjComponentController
import vlcj.VlcjFrameController


@Composable
fun OneColumnApp(
    navController: NavHostController = rememberNavController()
) {

    val body = remember { mutableStateOf<PlayerResponse?>(null) }
    runBlocking(Dispatchers.IO) {
        Innertube.player(
            body = PlayerBody(videoId = "TVlyvIP_y1Y"),
            pipedSession = getPipedSession().toApiSession()
        )
    }?.onSuccess {
        body.value = it
    }

    val format = body.value?.streamingData?.adaptiveFormats
        ?.filter { it.isAudio }
        ?.maxByOrNull {
            it.bitrate?.times( (if (it.mimeType.startsWith("audio/webm")) 100 else 1)
            ) ?: -1 }

    //var url by remember { mutableStateOf(format?.url ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    var url by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }

    val componentController = remember(url) { VlcjComponentController() }
    val frameController = remember(url) { VlcjFrameController() }
    var showPlayer by remember { mutableStateOf(false) }

    //MusicDatabaseDesktop.getAllSongs()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: "artists"

    Scaffold(
        topBar = {
            DesktopTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            FramePlayer(
                Modifier.fillMaxWidth().border(BorderStroke(1.dp, Color.Yellow)),
                url,
                frameController.size.collectAsState(null).value?.run {
                    IntSize(first, second)
                } ?: IntSize.Zero,
                frameController.bytes.collectAsState(null).value,
                frameController,
                true,
                false
            )
        }
    ) { innerPadding ->

        Row(Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth(0.2f)
                    .padding(horizontal = layoutColumnsHorizontalPadding)
                    .padding(top = layoutColumnTopPadding)
            ) {
                Column(Modifier.fillMaxSize().border(1.dp, color = Color.Black)) {
                    Text(text = "Panel  ", modifier = Modifier.padding(start = 8.dp, top = layoutColumnTopPadding)
                        .clickable { showPlayer = !showPlayer })


                    AnimatedVisibility(
                        visible = showPlayer,
                        enter = expandIn(animationSpec = tween(350, easing = LinearOutSlowInEasing), expandFrom = Alignment.TopStart),
                        exit =  shrinkOut(animationSpec = tween(350, easing = FastOutSlowInEasing),shrinkTowards = Alignment.TopStart)
                    ){
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            FrameContainer(
                                Modifier.requiredHeight(400.dp),
                                frameController.size.collectAsState(null).value?.run {
                                    IntSize(first, second)
                                } ?: IntSize.Zero,
                                frameController.bytes.collectAsState(null).value
                            )
                        }
                    }


                }
            }
        }

    }


}

@Composable
fun OneColumnLayout() {
    Row(Modifier.fillMaxSize()) {
        OnePanelContent()
    }
}

@Composable
fun OnePanelContent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth(0.2f)
            .padding(horizontal = layoutColumnsHorizontalPadding)
            .padding(top = layoutColumnTopPadding)
    ) {
        Column(Modifier.fillMaxSize().border(1.dp, color = Color.Black)) {
            Text(text = "Panel  ", modifier = Modifier.padding(start = 8.dp, top = layoutColumnTopPadding))
        }
    }
}

