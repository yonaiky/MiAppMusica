package it.fast4x.rimusic.ui.screens.mood

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.kreate.android.R
import app.kreate.android.themed.rimusic.component.album.AlbumItem
import app.kreate.android.themed.rimusic.component.artist.ArtistItem
import app.kreate.android.themed.rimusic.component.playlist.PlaylistItem
import it.fast4x.compose.persist.persist
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.bodies.BrowseBodyWithLocale
import it.fast4x.innertube.requests.BrowseResult
import it.fast4x.innertube.requests.browse
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.models.Mood
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.components.themed.HeaderPlaceholder
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.utils.center
import it.fast4x.rimusic.utils.secondary
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.utils.shimmerEffect

internal const val defaultBrowseId = "FEmusic_moods_and_genres_category"

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun MoodList(
    navController: NavController,
    mood: Mood
) {
    val windowInsets = LocalPlayerAwareWindowInsets.current

    val browseId = mood.browseId ?: defaultBrowseId
    var moodPage by persist<Result<BrowseResult>>("playlist/$browseId${mood.params?.let { "/$it" } ?: ""}")

    LaunchedEffect(Unit) {
        moodPage = Innertube.browse(BrowseBodyWithLocale(browseId = browseId, params = mood.params))
    }

    val thumbnailSizeDp = Dimensions.thumbnails.album
    val thumbnailSizePx = thumbnailSizeDp.px

    val lazyListState = rememberLazyListState()

    val endPaddingValues = windowInsets.only(WindowInsetsSides.End).asPaddingValues()

    val sectionTextModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 24.dp, bottom = 8.dp)
        .padding(endPaddingValues)

    Column (
        modifier = Modifier
            .background(colorPalette().background0)
            //.fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth(
                if( NavigationBarPosition.Right.isCurrent() )
                    Dimensions.contentWidthRightBar
                else
                    1f
            )
    ) {
        if( moodPage == null ) {
            HeaderPlaceholder( Modifier.shimmerEffect() )
            repeat(4) {
                Row {
                    repeat(6) {
                        AlbumItem.VerticalPlaceholder( thumbnailSizeDp )
                    }
                }
            }
        }

        moodPage?.fold(
            onSuccess = { moodResult ->
                LazyColumn(
                    state = lazyListState,
                    //contentPadding = LocalPlayerAwareWindowInsets.current
                    //    .only(WindowInsetsSides.Vertical + WindowInsetsSides.End).asPaddingValues(),
                    modifier = Modifier
                        .background(colorPalette().background0)
                        .fillMaxSize()
                ) {
                    item(
                        key = "header",
                        contentType = 0
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            HeaderWithIcon(
                                title = mood.name,
                                iconId = R.drawable.globe,
                                enabled = true,
                                showIcon = true,
                                modifier = Modifier,
                                onClick = {}
                            )
                        }
                    }

                    moodResult.items.forEach { item ->
                        item {
                            BasicText(
                                text = item.title,
                                style = typography().m.semiBold,
                                modifier = sectionTextModifier
                            )
                        }
                        item {
                            val appearance = LocalAppearance.current
                            val albumItemValues = remember( appearance ) {
                                AlbumItem.Values.from( appearance )
                            }
                            val artistItemValues = remember( appearance ) {
                                ArtistItem.Values.from( appearance )
                            }
                            val playlistItemValues = remember( appearance ) {
                                PlaylistItem.Values.from( appearance )
                            }

                            LazyRow {
                                items(items = item.items, key = { it.key }) { childItem ->
                                    if (childItem.key == defaultBrowseId) return@items
                                    when (childItem) {
                                        is Innertube.AlbumItem -> AlbumItem.Vertical(
                                            innertubeAlbum = childItem,
                                            widthDp = thumbnailSizeDp,
                                            values = albumItemValues,
                                            modifier = Modifier.clickable {
                                                childItem.info?.endpoint?.browseId?.let {
                                                    NavRoutes.YT_ALBUM.navigateHere( navController, it )
                                                }
                                            }
                                        )

                                        is Innertube.ArtistItem -> ArtistItem.Render(
                                            innertubeArtist = childItem,
                                            widthDp = thumbnailSizeDp,
                                            values = artistItemValues,
                                            modifier = Modifier.clickable {
                                                childItem.info?.endpoint?.browseId?.let {
                                                    NavRoutes.YT_ARTIST.navigateHere( navController, it )
                                                }
                                            }
                                        )

                                        is Innertube.PlaylistItem -> PlaylistItem.Vertical(
                                            innertubePlaylist = childItem,
                                            widthDp = thumbnailSizeDp,
                                            values = playlistItemValues,
                                            modifier = Modifier.clickable {
                                                childItem.info?.endpoint?.browseId?.let { browseId ->
                                                    NavRoutes.YT_PLAYLIST.navigateHere( navController, browseId )
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
                        Spacer(modifier = Modifier.height(Dimensions.bottomSpacer))
                    }

                }
            },
            onFailure = {
                BasicText(
                    text = stringResource(R.string.page_not_been_loaded),
                    style = typography().s.secondary.center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(all = 16.dp)
                )
            }
        )
    }
}
