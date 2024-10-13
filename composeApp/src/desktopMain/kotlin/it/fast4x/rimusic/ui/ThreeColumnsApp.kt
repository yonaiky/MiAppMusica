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
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.DrawerDefaults.windowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import database.MusicDatabaseDesktop
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.NavigationEndpoint
import it.fast4x.innertube.models.PlayerResponse
import it.fast4x.innertube.models.bodies.NextBody
import it.fast4x.innertube.models.bodies.PlayerBody
import it.fast4x.innertube.requests.discoverPage
import it.fast4x.innertube.requests.player
import it.fast4x.innertube.requests.relatedPage
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.items.AlbumItem
import it.fast4x.rimusic.items.ArtistItem
import it.fast4x.rimusic.items.MoodItemColored
import it.fast4x.rimusic.items.PlaylistItem
import it.fast4x.rimusic.items.SongItem
import it.fast4x.rimusic.styling.Dimensions.albumThumbnailSize
import it.fast4x.rimusic.styling.Dimensions.artistThumbnailSize
import it.fast4x.rimusic.styling.Dimensions.itemInHorizontalGridWidth
import it.fast4x.rimusic.styling.Dimensions.itemsVerticalPadding
import it.fast4x.rimusic.styling.Dimensions.layoutColumnBottomPadding
import it.fast4x.rimusic.styling.Dimensions.layoutColumnBottomSpacer
import it.fast4x.rimusic.styling.Dimensions.layoutColumnTopPadding
import it.fast4x.rimusic.styling.Dimensions.layoutColumnsHorizontalPadding
import it.fast4x.rimusic.styling.Dimensions.playlistThumbnailSize
import it.fast4x.rimusic.ui.components.CustomModalBottomSheet
import it.fast4x.rimusic.ui.components.Title
import it.fast4x.rimusic.ui.components.Title2Actions
import it.fast4x.rimusic.ui.screens.ArtistScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import player.frame.FrameContainer
import player.frame.FramePlayer
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.album
import rimusic.composeapp.generated.resources.app_icon
import rimusic.composeapp.generated.resources.app_logo_text
import rimusic.composeapp.generated.resources.artists
import rimusic.composeapp.generated.resources.library
import rimusic.composeapp.generated.resources.moods_and_genres
import rimusic.composeapp.generated.resources.musical_notes
import rimusic.composeapp.generated.resources.new_albums
import rimusic.composeapp.generated.resources.play
import rimusic.composeapp.generated.resources.playlists_you_might_like
import rimusic.composeapp.generated.resources.related_albums
import rimusic.composeapp.generated.resources.similar_artists
import vlcj.VlcjComponentController
import vlcj.VlcjFrameController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeColumnsApp(
    navController: NavHostController = rememberNavController()
) {


    val videoId = remember { mutableStateOf("HZnNt9nnEhw") }
    val artistId = remember { mutableStateOf("") }
    //val body = remember { mutableStateOf<PlayerResponse?>(null) }

    val formatAudio = remember { mutableStateOf<PlayerResponse.StreamingData.AdaptiveFormat?>(null) }

    LaunchedEffect(videoId.value) {
        //runBlocking(Dispatchers.IO) {
            Innertube.player(PlayerBody(videoId = videoId.value))
            ?.onSuccess {
            //body.value = it
            formatAudio.value = it.streamingData?.adaptiveFormats?.filter { type -> type.isAudio }?.maxByOrNull {
                it.bitrate?.times( (if (it.mimeType.startsWith("audio/webm")) 100 else 1)
                ) ?: -1
            }
        }
        println("videoId  ${videoId.value} formatAudio url inside ${formatAudio.value?.url}")
    }

    /*
    val formatAudio = body.value?.streamingData?.adaptiveFormats
        ?.filter { it.isAudio }
        ?.maxByOrNull {
            it.bitrate?.times( (if (it.mimeType.startsWith("audio/webm")) 100 else 1)
            ) ?: -1 }

     */



    /*
    val formatVideo = body.value?.streamingData?.adaptiveFormats
        ?.filter { it.isVideo }
        ?.maxByOrNull {
            it.bitrate?.times( (if (it.mimeType.startsWith("video/mp4")) 100 else 1)
            ) ?: -1 }
    */


    //val urlAudio by remember { mutableStateOf(formatAudio.value?.url ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    //var urlVideo by remember { mutableStateOf(formatVideo?.url ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    //var url by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    //val url by remember { mutableStateOf(formatAudio.value?.url) }

    val url = formatAudio.value?.url //"https://rr4---sn-hpa7znzr.googlevideo.com/videoplayback?expire=1727471735&ei=Fsz2ZoyiPO7Si9oPpreTiAI&ip=178.19.172.167&id=o-ABmCff7qCeQd05V_WN5fpAFEfxHP3kxR6G55H_QdlBsh&itag=251&source=youtube&requiressl=yes&xpc=EgVo2aDSNQ%3D%3D&mh=43&mm=31%2C26&mn=sn-hpa7znzr%2Csn-4g5lznez&ms=au%2Conr&mv=m&mvi=4&pl=22&gcr=it&initcwndbps=2505000&vprv=1&svpuc=1&mime=audio%2Fwebm&rqh=1&gir=yes&clen=3291443&dur=194.901&lmt=1714829870710563&mt=1727449746&fvip=4&keepalive=yes&fexp=51299152&c=ANDROID_MUSIC&txp=2318224&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cxpc%2Cgcr%2Cvprv%2Csvpuc%2Cmime%2Crqh%2Cgir%2Cclen%2Cdur%2Clmt&sig=AJfQdSswRQIhAP5IS0unA9IAhtAtkqY-63FGyG_eRi-FMMgNjWU1TWGzAiACd3c4niMxsPxXjp_55EylpIBysVBOpoD69oQ9xvF8bg%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=ABPmVW0wRAIgZBP07jXYZ5_4xSrp_hZ9jvIOMPsfOa-grREDshQvzSYCIC7ImmFVJCeLUMVASEkedlXa-R4je3RVC_fu2WH8XTvj"

    println("url $url")

    //var url by remember { mutableStateOf(urlVideo) }

    //val componentController = remember(url) { VlcjComponentController() }
    val frameController = remember(url) { VlcjFrameController() }
    var showPlayer by remember { mutableStateOf(false) }
    var showArtistPage by remember { mutableStateOf(false) }

    MusicDatabaseDesktop.getAll()

    //val backStackEntry by navController.currentBackStackEntryAsState()
    //val currentScreen = backStackEntry?.destination?.route ?: "artists"

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
                        url ?: "",
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
            onSongClick = {
                videoId.value = it
                println("videoId clicked $it")
            },
            onArtistClick = {
                artistId.value = it
                showArtistPage = true
            },
            onAlbumClick = {},
            onPlaylistClick = {},
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

        CustomModalBottomSheet(
            showSheet = showArtistPage,
            onDismissRequest = { showArtistPage = false },
            containerColor = Color.Black,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth(),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 0.dp),
                    color =Color.Black,
                    shape = ThumbnailRoundness.Medium.shape()
                ) {}
            },
            shape = ThumbnailRoundness.Medium.shape()
        ) {
           ArtistScreen(
               browseId = artistId.value,
               onPlaylistClick = {},
               onViewAllAlbumsClick = {},
               onViewAllSinglesClick = {},
               onAlbumClick = {}
           )
        }


    }
}

@Composable
fun ThreeColumnsLayout(
   onSongClick: (key: String) -> Unit = {},
   onArtistClick: (key: String) -> Unit = {},
   onAlbumClick: (key: String) -> Unit = {},
   onPlaylistClick: (key: String) -> Unit = {},
   onMoodClick: (mood: Innertube.Mood.Item) -> Unit = {},
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
        CenterPanelContent(
            onSongClick = onSongClick,
            onArtistClick = onArtistClick,
            onAlbumClick = onAlbumClick,
            onPlaylistClick = onPlaylistClick,
            onMoodClick = onMoodClick
        )
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
                    Modifier.requiredHeight(200.dp),
                        //.border(BorderStroke(1.dp, Color.Red)),
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
            .fillMaxWidth(0.23f)
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
fun CenterPanelContent(
    onSongClick: (key: String) -> Unit = {},
    onAlbumClick: (key: String) -> Unit = {},
    onArtistClick: (key: String) -> Unit = {},
    onPlaylistClick: (key: String) -> Unit = {},
    onMoodClick: (mood: Innertube.Mood.Item) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
            .padding(horizontal = layoutColumnsHorizontalPadding)
            .padding(top = layoutColumnTopPadding)
            .padding(bottom = layoutColumnBottomPadding)
            .verticalScroll(scrollState)
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

            Title2Actions(
                title = "For You",
                onClick1 = {},
                icon2 = Res.drawable.play,
                onClick2 = {}
            )

            val quickPicksLazyGridState = rememberLazyGridState()
            val moodAngGenresLazyGridState = rememberLazyGridState()
            val endPaddingValues = windowInsets.only(WindowInsetsSides.End).asPaddingValues()
            val related = remember { mutableStateOf<Innertube.RelatedPage?>(null) }
            var relatedPageResult by remember { mutableStateOf<Result<Innertube.RelatedPage?>?>(null) }
            var discoverPageResult by remember { mutableStateOf<Result<Innertube.DiscoverPage?>?>(null) }
            var discover = remember { mutableStateOf<Innertube.DiscoverPage?>(null) }

            LaunchedEffect(Unit) {
                relatedPageResult = Innertube.relatedPage(
                    NextBody(
                        videoId = "HZnNt9nnEhw"
                    )
                )

                discoverPageResult = Innertube.discoverPage()
            }
           relatedPageResult?.getOrNull().also { related.value = it }
           discoverPageResult?.getOrNull().also { discover.value = it }

            LazyHorizontalGrid(
                state = quickPicksLazyGridState,
                rows = GridCells.Fixed(if (related.value != null) 3 else 1),
                flingBehavior = ScrollableDefaults.flingBehavior(),
                contentPadding = endPaddingValues,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (related.value != null) itemsVerticalPadding * 3 * 9 else itemsVerticalPadding * 9)
            ) {
                if (related.value != null) {
                    items(
                        items = related.value!!.songs?.distinctBy { it.key }
                            //?.dropLast(if (trending == null) 0 else 1)
                            ?: emptyList(),
                        key = Innertube.SongItem::key
                    ) { song ->

                        SongItem(
                            song = song,
                            isDownloaded = false,
                            onDownloadClick = {},
                            //thumbnailSizeDp = 50.dp,
                            modifier = Modifier
                                .combinedClickable(
                                    onLongClick = {},
                                    onClick = {
                                        onSongClick(song.key)
                                    }
                                )
                                .animateItemPlacement()
                                .width(itemInHorizontalGridWidth)
                        )
                    }
                }
            }

            discover.let { page ->
                val showNewAlbums = true
                if (showNewAlbums) {
                    Title(
                        title = stringResource(Res.string.new_albums),
                        onClick = {},
                        //modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    LazyRow(contentPadding = endPaddingValues) {
                        page.value?.newReleaseAlbums?.let {
                            items(items = it.distinctBy { it.key }, key = { it.key }) {
                                AlbumItem(
                                    album = it,
                                    thumbnailSizeDp = albumThumbnailSize,
                                    alternative = true,
                                    modifier = Modifier.clickable(onClick = {
                                        onAlbumClick(it.key)
                                    })
                                )
                            }
                        }
                    }
                }
            }

            related.value?.albums?.let { albums ->
                val showRelatedAlbums = true
                if (showRelatedAlbums) {
                    Title(
                        title = stringResource(Res.string.related_albums),
                        onClick = {},
                        //modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    LazyRow(contentPadding = endPaddingValues) {
                        items(items = albums.distinctBy { it.key }, key = { it.key }) {
                            AlbumItem(
                                album = it,
                                thumbnailSizeDp = albumThumbnailSize,
                                alternative = true,
                                modifier = Modifier.clickable(onClick = {
                                    onAlbumClick(it.key)
                                })
                            )
                        }
                    }
                }
            }

            related.value?.artists?.let { artists ->
                val showSimilarArtists = true
                if (showSimilarArtists) {
                    Title(
                        title = stringResource(Res.string.similar_artists),
                        onClick = {},
                        //modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    LazyRow(contentPadding = endPaddingValues) {
                        items(items = artists.distinctBy { it.key }, key = { it.key }) {
                            ArtistItem(
                                artist = it,
                                thumbnailSizeDp = artistThumbnailSize,
                                alternative = true,
                                modifier = Modifier.clickable(onClick = {
                                    onArtistClick(it.key)
                                })
                            )

                        }
                    }
                }
            }

            related.value?.playlists?.let { playlists ->
                val showPlaylistMightLike = true
                if (showPlaylistMightLike) {
                    Title(
                        title = stringResource(Res.string.playlists_you_might_like),
                        onClick = {},
                        //modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    LazyRow(contentPadding = endPaddingValues) {
                        items(items = playlists.distinctBy { it.key }, key = { it.key }) {
                            PlaylistItem(
                                playlist = it,
                                thumbnailSizeDp = playlistThumbnailSize,
                                alternative = true,
                                showSongsCount = false,
                                modifier = Modifier.clickable(onClick = {
                                    onPlaylistClick(it.key)
                                })
                            )

                        }
                    }
                }
            }

            discover.let { page ->
                val showNewAlbums = true
                if (showNewAlbums) {
                    Title(
                        title = stringResource(Res.string.moods_and_genres),
                        onClick = {},
                        //modifier = Modifier.fillMaxWidth(0.7f)
                    )

                        LazyHorizontalGrid(
                            state = moodAngGenresLazyGridState,
                            rows = GridCells.Fixed(4),
                            flingBehavior = ScrollableDefaults.flingBehavior(),
                            contentPadding = endPaddingValues,
                            modifier = Modifier
                                //.fillMaxWidth()
                                .height(itemsVerticalPadding * 4 * 8)
                        ) {
                            page.value?.moods?.let {
                                items(
                                    items = it.sortedBy { it.title },
                                    key = { it.endpoint.params ?: it.title }
                                ) {
                                    MoodItemColored(
                                        mood = it,
                                        onClick = { it.endpoint.browseId?.let { _ -> onMoodClick(it) } },
                                        modifier = Modifier
                                            //.width(itemWidth)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }

                }
            }


            Spacer(Modifier.height(layoutColumnBottomSpacer))


        }

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
