package it.fast4x.rimusic.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerDefaults.windowInsets
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.bodies.BrowseBody
import it.fast4x.innertube.requests.artistPage
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.items.AlbumItem
import it.fast4x.rimusic.items.PlaylistItem
import it.fast4x.rimusic.items.SongItem
import it.fast4x.rimusic.styling.Dimensions.albumThumbnailSize
import it.fast4x.rimusic.styling.Dimensions.fadeSpacingBottom
import it.fast4x.rimusic.styling.Dimensions.fadeSpacingTop
import it.fast4x.rimusic.styling.Dimensions.playlistThumbnailSize
import it.fast4x.rimusic.ui.components.AutoResizeText
import it.fast4x.rimusic.ui.components.ExpandIcon
import it.fast4x.rimusic.ui.components.FontSizeRange
import it.fast4x.rimusic.ui.components.Title
import it.fast4x.rimusic.ui.components.Title2Actions
import it.fast4x.rimusic.ui.components.adaptiveThumbnailContent
import it.fast4x.rimusic.utils.fadingEdge
import it.fast4x.rimusic.utils.getHttpClient
import it.fast4x.rimusic.utils.languageDestination
import it.fast4x.rimusic.utils.resize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.bush.translator.Language
import me.bush.translator.Translator
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.albums
import rimusic.composeapp.generated.resources.arrow_left
import rimusic.composeapp.generated.resources.artist_subscribers
import rimusic.composeapp.generated.resources.chevron_down
import rimusic.composeapp.generated.resources.dice
import rimusic.composeapp.generated.resources.from_wikipedia_cca
import rimusic.composeapp.generated.resources.information
import rimusic.composeapp.generated.resources.musical_notes
import rimusic.composeapp.generated.resources.playlists
import rimusic.composeapp.generated.resources.singles
import rimusic.composeapp.generated.resources.songs
import rimusic.composeapp.generated.resources.translate
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistScreen(
    browseId: String,
    onPlaylistClick: (String) -> Unit,
    onViewAllAlbumsClick: () -> Unit,
    onViewAllSinglesClick: () -> Unit,
    onAlbumClick: (String) -> Unit,
    onClosePage: () -> Unit
) {
    val scrollState = rememberScrollState()
    val artistPage = remember { mutableStateOf<Innertube.ArtistPage?>(null) }
    LaunchedEffect(Unit) {
        Innertube.artistPage(BrowseBody(browseId = browseId))
            ?.onSuccess {
                artistPage.value = it
            }
    }
    val artist = artistPage.value
    val endPaddingValues = windowInsets.only(WindowInsetsSides.End).asPaddingValues()

    val sectionTextModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 24.dp, bottom = 8.dp)

    var translateEnabled by remember {
        mutableStateOf(false)
    }

    val translator = Translator(getHttpClient())
    val languageDestination = languageDestination()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    //.fillMaxSize()
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
            ) {
                ExpandIcon(
                    onAction = onClosePage,
                    actionClose = true,
                    modifier = Modifier.fillMaxWidth()
                )

                val modifierArt = Modifier
                    .aspectRatio(16f / 9)
                    .height(300.dp)

                Box(
                    modifier = modifierArt
                ) {
                    if (artistPage.value != null) {
                        AsyncImage(
                            model = artistPage.value!!.thumbnail?.url?.resize(1200, 900),
                            contentDescription = "loading...",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .fadingEdge(
                                    top = fadeSpacingTop,
                                    bottom = fadeSpacingBottom
                                )
                        )
                    }
                    AutoResizeText(
                        text = artistPage.value?.name.toString(),
                        style = typography.displayLarge,
                        fontSizeRange = FontSizeRange(50.sp, 100.sp),
                        fontWeight = typography.displayLarge.fontWeight,
                        fontFamily = typography.displayLarge.fontFamily,
                        color = typography.displayLarge.color,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 30.dp)
                    )
                }

                artistPage.value?.subscriberCountText?.let {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.artist_subscribers,it),
                            style = TextStyle(
                                fontSize = typography.titleMedium.fontSize,
                                fontWeight = typography.titleMedium.fontWeight,
                                color = Color.White,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier.weight(1f)

                        )
                    }
                }

                artistPage.value?.description?.let { description ->
                    val attributionsIndex = description.lastIndexOf("\n\nFrom Wikipedia")

                        Title(
                            title = stringResource(Res.string.information),
                            icon = Res.drawable.translate,
                            onClick = {
                                translateEnabled = !translateEnabled
                            }
                        )
                        var translatedText by remember { mutableStateOf("") }
                        val nonTranslatedText by remember {
                            mutableStateOf(
                                if (attributionsIndex == -1) {
                                    description
                                } else {
                                    description.substring(0, attributionsIndex)
                                }
                            )
                        }


                        if (translateEnabled == true) {
                            LaunchedEffect(Unit) {
                                val result = withContext(Dispatchers.IO) {
                                    try {
                                        translator.translate(
                                            nonTranslatedText,
                                            languageDestination,
                                            Language.AUTO
                                        ).translatedText
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                translatedText =
                                    if (result.toString() == "kotlin.Unit") "" else result.toString()
                            }
                        } else translatedText = nonTranslatedText

                        Text(
                            text = translatedText,
                            style = TextStyle(
                                fontSize = typography.titleSmall.fontSize,
                                fontWeight = typography.titleSmall.fontWeight,
                                color = Color.White,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                //.weight(1f)

                        )

                    if (attributionsIndex != -1) {
                        Text(
                            text = stringResource(Res.string.from_wikipedia_cca),
                            style = TextStyle(
                                fontSize = typography.titleSmall.fontSize,
                                fontWeight = typography.titleSmall.fontWeight,
                                color = Color.White,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp)
                                //.padding(endPaddingValues)

                        )
                    }

                }

            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    //.fillMaxWidth(0.5f)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {

                if (artistPage.value != null) {

                    artistPage.value!!.songs?.let { allSongs ->

                        val parentalControlEnabled = false

                        val songs = if (parentalControlEnabled)
                            allSongs.filter { !it.explicit } else allSongs
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(endPaddingValues)
                        ) {
                            Title(
                                title = stringResource(Res.string.songs),
                                onClick = {
                                    //if (youtubeArtistPage.songsEndpoint?.browseId != null) {

                                    //} else SmartToast(context.resources.getString(R.string.info_no_songs_yet))
                                },
                                //modifier = Modifier.fillMaxWidth(0.7f)
                            )

                        }

                        songs.forEachIndexed { index, song ->

                            SongItem(
                                song = song,
                                isDownloaded = false,
                                onDownloadClick = {},
                                //thumbnailSizeDp = songThumbnailSizeDp,
                                modifier = Modifier
                                    .combinedClickable(
                                        onLongClick = {
                                            /*
                                            menuState.display {
                                                NonQueuedMediaItemMenu(
                                                    navController = navController,
                                                    onDismiss = menuState::hide,
                                                    mediaItem = song.asMediaItem,
                                                )
                                            }
                                             */
                                        },
                                        onClick = {}
                                    )
                                    .padding(endPaddingValues)
                            )

                        }
                    }

                    artistPage.value!!.playlists?.let { playlists ->
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(endPaddingValues)
                        ) {
                            Title(
                                title = stringResource(Res.string.playlists),
                                onClick = {
                                    //if (youtubeArtistPage.albumsEndpoint?.browseId != null) {
                                    //onViewAllAlbumsClick()
                                    //} else SmartToast(context.resources.getString(R.string.info_no_albums_yet))
                                }
                            )
                        }

                        LazyRow(
                            contentPadding = endPaddingValues,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(
                                items = playlists,
                                key = Innertube.PlaylistItem::key
                            ) { playlist ->
                                PlaylistItem(
                                    playlist = playlist,
                                    thumbnailSizeDp = playlistThumbnailSize,
                                    alternative = true,
                                    modifier = Modifier
                                        .clickable(onClick = {
                                            onPlaylistClick(playlist.key)
                                        }
                                        )
                                )
                            }
                        }
                    }

                    artistPage.value!!.albums?.let { albums ->
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(endPaddingValues)
                        ) {
                            Title2Actions(
                                title = stringResource(Res.string.albums),
                                onClick1 = {
                                    //if (youtubeArtistPage.albumsEndpoint?.browseId != null) {
                                    onViewAllAlbumsClick()
                                    //} else SmartToast(context.resources.getString(R.string.info_no_albums_yet))
                                },
                                icon2 = Res.drawable.dice,
                                onClick2 = {
                                    val albumId = albums.get(
                                        Random(System.currentTimeMillis()).nextInt(
                                            0,
                                            albums.size - 1
                                        )
                                    ).key
                                    onAlbumClick(albumId)
                                }
                            )
                        }

                        LazyRow(
                            contentPadding = endPaddingValues,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(
                                items = albums,
                                key = Innertube.AlbumItem::key
                            ) { album ->
                                AlbumItem(
                                    album = album,
                                    thumbnailSizeDp = albumThumbnailSize,
                                    alternative = true,
                                    modifier = Modifier
                                        .clickable(onClick = { onAlbumClick(album.key) })
                                )
                            }
                        }
                    }

                    artistPage.value!!.singles?.let { singles ->
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(endPaddingValues)
                        ) {
                            Title(
                                title = stringResource(Res.string.singles),
                                onClick = {
                                    //if (youtubeArtistPage.singlesEndpoint?.browseId != null) {
                                    onViewAllSinglesClick()
                                    //} else SmartToast(context.resources.getString(R.string.info_no_singles_yet))
                                }
                            )
                        }

                        LazyRow(
                            contentPadding = endPaddingValues,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(
                                items = singles,
                                key = Innertube.AlbumItem::key
                            ) { album ->
                                AlbumItem(
                                    album = album,
                                    thumbnailSizeDp = albumThumbnailSize,
                                    alternative = true,
                                    modifier = Modifier
                                        .clickable(onClick = { onAlbumClick(album.key) })
                                )
                            }

                        }
                    }


                }


            }
        }

    }

}