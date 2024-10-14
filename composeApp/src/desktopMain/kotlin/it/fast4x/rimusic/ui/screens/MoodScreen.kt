package it.fast4x.rimusic.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerDefaults.windowInsets
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.bodies.BrowseBodyWithLocale
import it.fast4x.innertube.requests.BrowseResult
import it.fast4x.innertube.requests.browse
import it.fast4x.rimusic.items.AlbumItem
import it.fast4x.rimusic.items.ArtistItem
import it.fast4x.rimusic.items.PlaylistItem
import it.fast4x.rimusic.styling.Dimensions.albumThumbnailSize
import it.fast4x.rimusic.styling.Dimensions.artistThumbnailSize
import it.fast4x.rimusic.styling.Dimensions.layoutColumnBottomSpacer
import it.fast4x.rimusic.styling.Dimensions.playlistThumbnailSize
import it.fast4x.rimusic.ui.components.Title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.loader
import rimusic.composeapp.generated.resources.page_not_been_loaded

internal const val defaultBrowseId = "FEmusic_moods_and_genres_category"

@Composable
fun MoodScreen(
    mood: Innertube.Mood.Item?,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit
) {

    val browseId = mood?.endpoint?.browseId ?: defaultBrowseId

    println("mediaItem browseId: $browseId")

    var moodPage by remember { mutableStateOf<BrowseResult?>(null) }
    var moodPageResult by remember { mutableStateOf<Result<BrowseResult>?>(null) }
    LaunchedEffect(browseId) {
        moodPageResult = Innertube.browse(BrowseBodyWithLocale(browseId = browseId, params = mood?.endpoint?.params))
    }

    moodPageResult?.getOrThrow().also { moodPage = it }

    val endPaddingValues = windowInsets.only(WindowInsetsSides.End).asPaddingValues()

    val sectionTextModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 24.dp, bottom = 8.dp)

    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        moodPage?.let { moodResult ->
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    //.background(colorPalette().background0)
                    .fillMaxSize()
            ) {
                item(
                    key = "header",
                    contentType = 0
                ) {
                    //Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Title(
                            title = mood?.title ?: "Moods",
                        )

                    //}
                }

                moodResult.items.forEach { item ->
                    item {
                        Text(
                            text = item.title,
                            style = TextStyle(
                                fontSize = typography.titleMedium.fontSize,
                                fontWeight = typography.titleMedium.fontWeight,
                                color = Color.White,
                                textAlign = TextAlign.Start
                            ),
                            //modifier = Modifier
                            //    .align(Alignment.BottomCenter)

                        )
                    }
                    item {
                        LazyRow {
                            items(items = item.items, key = { it.key }) { childItem ->
                                if (childItem.key == defaultBrowseId) return@items
                                when (childItem) {
                                    is Innertube.AlbumItem -> AlbumItem(
                                        album = childItem,
                                        thumbnailSizeDp = albumThumbnailSize,
                                        alternative = true,
                                        modifier = Modifier.clickable {
                                            childItem.info?.endpoint?.browseId?.let {
                                                onAlbumClick(it)
                                            }
                                        }
                                    )

                                    is Innertube.ArtistItem -> ArtistItem(
                                        artist = childItem,
                                        thumbnailSizeDp = artistThumbnailSize,
                                        alternative = true,
                                        modifier = Modifier.clickable {
                                            childItem.info?.endpoint?.browseId?.let {
                                               onArtistClick(it)
                                            }
                                        }
                                    )

                                    is Innertube.PlaylistItem -> PlaylistItem(
                                        playlist = childItem,
                                        thumbnailSizeDp = playlistThumbnailSize,
                                        alternative = true,
                                        modifier = Modifier.clickable {
                                            childItem.info?.endpoint?.browseId?.let {
                                                onPlaylistClick(it)
                                            }
                                        }
                                    )

                                    else -> {}
                                }
                            }
                        }
                    }
                }

                item(key = "bottom") {
                    Spacer(modifier = Modifier.height(layoutColumnBottomSpacer))
                }

            }
        } ?: Image(
            painter = painterResource(Res.drawable.loader),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null
        )
    }

}