package app.kreate.android.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import app.kreate.android.themed.rimusic.component.album.AlbumItem
import app.kreate.android.themed.rimusic.component.artist.ArtistItem
import app.kreate.android.themed.rimusic.component.playlist.PlaylistItem
import app.kreate.android.themed.rimusic.component.song.SongItem
import app.kreate.android.utils.innertube.toMediaItem
import it.fast4x.innertube.Innertube
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.isVideoEnabled
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.forcePlay
import it.fast4x.rimusic.utils.playVideo
import me.knighthat.innertube.model.InnertubeAlbum
import me.knighthat.innertube.model.InnertubeArtist
import me.knighthat.innertube.model.InnertubeItem
import me.knighthat.innertube.model.InnertubePlaylist
import me.knighthat.innertube.model.InnertubeSong


object ItemUtils {

    const val COLUMN_SPACING = 10

    @JvmName("OldInnertubeLazyRowItem")
    @UnstableApi
    @Composable
    fun LazyRowItem(
        navController: NavController,
        innertubeItems: List<Innertube.Item>,
        thumbnailSizeDp: Dp,
        currentlyPlaying: String?,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        val binder = LocalPlayerServiceBinder.current ?: return
        val hapticFeedback = LocalHapticFeedback.current
        val appearance = LocalAppearance.current
        val songItemValues = remember( appearance ) {
            SongItem.Values.from( appearance )
        }
        val albumItemValues = remember( appearance ) {
            AlbumItem.Values.from( appearance )
        }
        val artistItemValues = remember( appearance ) {
            ArtistItem.Values.from( appearance )
        }
        val playlistItemValues = remember( appearance ) {
            PlaylistItem.Values.from( appearance )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(COLUMN_SPACING.dp ),
            modifier = modifier
        ) {
            items(
                items = innertubeItems,
                key = Innertube.Item::key
            ) { childItem ->
                when ( childItem ) {
                    is Innertube.SongItem -> SongItem.Render(
                        innertubeSong = childItem,
                        context = context,
                        binder = binder,
                        hapticFeedback = hapticFeedback,
                        values = songItemValues,
                        isPlaying = childItem.key == currentlyPlaying,
                        navController = navController,
                        onClick = {
                            binder.player.forcePlay( childItem.asMediaItem )
                        }
                    )

                    is Innertube.VideoItem -> {
                        println("Innertube homePage VideoItem: ${childItem.info?.name}")
                        SongItem.Render(
                            innertubeVideo = childItem,
                            hapticFeedback = hapticFeedback,
                            isPlaying = currentlyPlaying == childItem.key,
                            values = songItemValues,
                            // Made-up number
                            thumbnailSizeDp = DpSize(thumbnailSizeDp, thumbnailSizeDp),
                            onClick = {
                                binder.stopRadio()
                                if ( isVideoEnabled() )
                                    binder.player.playVideo( childItem.asMediaItem )
                                else
                                    binder.player.forcePlay( childItem.asMediaItem )
                            }
                        )
                    }

                    is Innertube.AlbumItem -> AlbumItem.Vertical(
                        innertubeAlbum = childItem,
                        widthDp = thumbnailSizeDp,
                        values = albumItemValues,
                        navController = navController
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
                }
            }
        }
    }

    @UnstableApi
    @Composable
    fun LazyRowItem(
        navController: NavController,
        innertubeItems: List<InnertubeItem>,
        thumbnailSizeDp: Dp,
        currentlyPlaying: String?,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        val binder = LocalPlayerServiceBinder.current ?: return
        val hapticFeedback = LocalHapticFeedback.current
        val appearance = LocalAppearance.current
        val (songIV, albumIV, artistIV, playlistIV) = remember( appearance ) {
            Quadruple(
                SongItem.Values.from( appearance ),
                AlbumItem.Values.from( appearance ),
                ArtistItem.Values.from( appearance ),
                PlaylistItem.Values.from( appearance )
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(COLUMN_SPACING.dp ),
            modifier = modifier
        ) {
            items(
                items = innertubeItems,
                key = System::identityHashCode
            ) { item ->
                when( item ) {
                    is InnertubeSong -> SongItem.Render(
                        innertubeSong = item,
                        context = context,
                        binder = binder,
                        hapticFeedback = hapticFeedback,
                        values = songIV,
                        isPlaying = item.id == currentlyPlaying,
                        navController = navController,
                        onClick = {
                            binder.player.forcePlay( item.toMediaItem )
                        }
                    )

                    is InnertubeAlbum -> AlbumItem.Vertical(
                        innertubeAlbum = item,
                        widthDp = thumbnailSizeDp,
                        values = albumIV,
                        navController = navController
                    )

                    is InnertubeArtist -> ArtistItem.Render(
                        innertubeArtist = item,
                        widthDp = thumbnailSizeDp,
                        values = artistIV,
                        modifier = Modifier.clickable {
                            NavRoutes.YT_ARTIST.navigateHere( navController, item.id )
                        }
                    )

                    is InnertubePlaylist -> PlaylistItem.Vertical(
                        innertubePlaylist = item,
                        widthDp = thumbnailSizeDp,
                        values = playlistIV,
                        modifier = Modifier.clickable {
                            NavRoutes.YT_PLAYLIST.navigateHere( navController, item.id )
                        }
                    )
                }
            }
        }
    }

    private data class Quadruple<T1, T2, T3, T4>(val t1: T1, val t2: T2, val t3: T3, val t4: T4)
}