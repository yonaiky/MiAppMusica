package it.fast4x.rimusic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import database.MusicDatabaseDesktop
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.PlayerResponse
import it.fast4x.innertube.models.bodies.PlayerBody
import it.fast4x.innertube.requests.player
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.items.SongItem
import it.fast4x.rimusic.styling.Dimensions.layoutColumnBottomPadding
import it.fast4x.rimusic.styling.Dimensions.layoutColumnTopPadding
import it.fast4x.rimusic.styling.Dimensions.layoutColumnsHorizontalPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import player.frame.FrameContainer
import player.frame.FramePlayer
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.album
import rimusic.composeapp.generated.resources.app_icon
import rimusic.composeapp.generated.resources.app_logo_text
import rimusic.composeapp.generated.resources.artists
import rimusic.composeapp.generated.resources.library
import rimusic.composeapp.generated.resources.musical_notes
import vlcj.VlcjComponentController
import vlcj.VlcjFrameController


@Composable
fun ThreeColumnsApp(
    navController: NavHostController = rememberNavController()
) {


    val body = remember { mutableStateOf<PlayerResponse?>(null) }
    runBlocking(Dispatchers.IO) {
        Innertube.player(PlayerBody(videoId = "Cn9OyUDg22M"))
    }?.onSuccess {
        body.value = it
    }

    val formatAudio = body.value?.streamingData?.adaptiveFormats
        ?.filter { it.isAudio }
        ?.maxByOrNull {
            it.bitrate?.times( (if (it.mimeType.startsWith("audio/webm")) 100 else 1)
            ) ?: -1 }



    /*
    val formatVideo = body.value?.streamingData?.adaptiveFormats
        ?.filter { it.isVideo }
        ?.maxByOrNull {
            it.bitrate?.times( (if (it.mimeType.startsWith("video/mp4")) 100 else 1)
            ) ?: -1 }
    */


    var urlAudio by remember { mutableStateOf(formatAudio?.url ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    //var urlVideo by remember { mutableStateOf(formatVideo?.url ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    //var url by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }


    var url by remember { mutableStateOf(urlAudio) }
    //var url by remember { mutableStateOf(urlVideo) }

    val componentController = remember(url) { VlcjComponentController() }
    val frameController = remember(url) { VlcjFrameController() }
    var showPlayer by remember { mutableStateOf(false) }

    MusicDatabaseDesktop.getAll()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: "artists"

    Scaffold(
        containerColor = Color.Black,
        contentColor = Color.Gray,
        topBar = {
            /*
            DesktopTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
             */
        },
        bottomBar = {
            Column (verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                HorizontalDivider(
                    color = Color.DarkGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().alpha(0.6f)
                )
                Row(
                    Modifier.background(Color.Black).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        //Modifier.border(BorderStroke(1.dp, Color.Red)),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SongItem(
                            thumbnailContent = {},
                            authors = "Author",
                            duration = "00:00",
                            title = "Title",
                            isDownloaded = false,
                            onDownloadClick = {},
                            thumbnailSizeDp = 80.dp,
                            modifier = Modifier.fillMaxWidth(0.2f)
                        )
                    }
                    FramePlayer(
                        Modifier.fillMaxWidth(0.8f), //.border(BorderStroke(1.dp, Color.Yellow)),
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
            }
        }
    ) { innerPadding ->

        ThreeColumnsLayout(
            onShowPlayer = {},
            url = url,
            frameController = frameController
        )

        AnimatedVisibility(
            visible = showPlayer,
            enter = expandIn(animationSpec = tween(350, easing = LinearOutSlowInEasing), expandFrom = Alignment.TopStart),
            exit =  shrinkOut(animationSpec = tween(350, easing = FastOutSlowInEasing),shrinkTowards = Alignment.TopStart)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

            }
        }


    }
}

@Composable
fun ThreeColumnsLayout(
    onShowPlayer: (Boolean) -> Unit = {},
    url: String = "",
    frameController: VlcjFrameController = remember { VlcjFrameController() }
) {
    Row(Modifier.fillMaxSize()) {
        LeftPanelContent()
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = Color.Gray.copy(alpha = 0.6f)
        )
        CenterPanelContent()
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = Color.Gray.copy(alpha = 0.6f)
        )
        RightPanelContent(
            onShowPlayer = {}
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                FrameContainer(
                    Modifier.requiredHeight(300.dp),
                    frameController.size.collectAsState(null).value?.run {
                        IntSize(first, second)
                    } ?: IntSize.Zero,
                    frameController.bytes.collectAsState(null).value
                )
            }


        }

    }
}

@Composable
fun LeftPanelContent() {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.2f)
            .padding(horizontal = layoutColumnsHorizontalPadding)
            .padding(top = layoutColumnTopPadding)
    ) {
        val (currentTabIndex, setCurrentTabIndex) = remember { mutableStateOf(0) }
        TabRow(
            currentTabIndex,
            modifier = Modifier
                .height(36.dp)
                .fillMaxWidth(),
            backgroundColor = Color.Gray.copy(alpha = 0.2f),
            contentColor = Color.White.copy(alpha = 0.6f)
        ) {
            Tab(
                currentTabIndex == 0,
                onClick = { setCurrentTabIndex(0) },
                text = {
                    //Text("")
                },
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.musical_notes),
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.6f)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }

            )
            Tab(
                currentTabIndex == 1,
                onClick = { setCurrentTabIndex(1) },
                text = {
                    //Text("")
                },
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.artists),
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.6f)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            )
            Tab(
                currentTabIndex == 2,
                onClick = { setCurrentTabIndex(2) },
                text = {
                    //Text("")
                },
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.album),
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.6f)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            )
            Tab(
                currentTabIndex == 3,
                onClick = { setCurrentTabIndex(3) },
                text = {
                    //Text("")
                },
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.library),
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.6f)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            )

        }
        /*
        Column(Modifier.fillMaxSize().border(1.dp, color = Color.Black)) {
            Text(text = "Left Panel  ", modifier = Modifier.padding(start = 8.dp, top = layoutColumnTopPadding))
        }
         */
        //Spacer(Modifier.size(100.dp))
        //Text(text = "Left Pane bottom Text Box")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CenterPanelContent() {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.6f)
            .padding(horizontal = layoutColumnsHorizontalPadding)
            .padding(top = layoutColumnTopPadding)
            .padding(bottom = layoutColumnBottomPadding)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(Res.drawable.app_icon),
                colorFilter = ColorFilter.tint(Color.Green.copy(alpha = 0.6f)),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {}
                    )
            )
            Image(
                painter = painterResource(Res.drawable.app_logo_text),
                colorFilter = ColorFilter.tint( Color.White
                    /*
                    when (colorPaletteMode) {
                        ColorPaletteMode.Light, ColorPaletteMode.System -> colorPalette.text
                        else -> Color.White
                    }

                     */
                ),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .clickable {}
            )
        }

        Column(Modifier.fillMaxSize().border(1.dp, color = Color.Black)) {
            Text(text = "Center Panel", modifier = Modifier.padding(start = 8.dp, top = 5.dp))
        }
        //Spacer(Modifier.size(100.dp))
        //Text(text = "Left Pane bottom Text Box")
    }
}

@Composable
fun RightPanelContent(
    onShowPlayer: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    var showPlayer by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = layoutColumnsHorizontalPadding)
            .padding(top = layoutColumnTopPadding)
    ) {
        /*
        Text(text = "Right Panel", modifier = Modifier.clickable {
            showPlayer = !showPlayer
            onShowPlayer(showPlayer)
        })
        Spacer(Modifier.size(20.dp))

         */
        Spacer(Modifier.size(layoutColumnTopPadding))
        Row(
            //Modifier.border(1.dp, color = Color.Yellow)
        ) {
            content()
        }
    }
}
