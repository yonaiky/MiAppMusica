package me.knighthat.component.menu.song

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.MenuStyle
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.service.isLocal
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.navigation.header.TabToolBar
import it.fast4x.rimusic.ui.components.tab.toolbar.Button
import it.fast4x.rimusic.ui.components.tab.toolbar.Menu
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.ui.components.themed.Enqueue
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.components.themed.PlayNext
import it.fast4x.rimusic.ui.components.themed.PlaylistsMenu
import it.fast4x.rimusic.ui.screens.settings.isYouTubeSyncEnabled
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.addNext
import it.fast4x.rimusic.utils.addToYtLikedSong
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.enqueue
import it.fast4x.rimusic.utils.isNetworkConnected
import it.fast4x.rimusic.utils.mediaItemToggleLike
import it.fast4x.rimusic.utils.menuStyleKey
import it.fast4x.rimusic.utils.rememberPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.knighthat.component.SongItem
import me.knighthat.component.song.ChangeAuthorDialog
import me.knighthat.component.song.GoToAlbum
import me.knighthat.component.song.GoToArtist
import me.knighthat.component.song.RenameSongDialog
import me.knighthat.component.song.ResetSongDialog
import me.knighthat.component.tab.DeleteSongDialog
import me.knighthat.component.tab.LikeComponent
import me.knighthat.component.tab.Radio
import me.knighthat.utils.Toaster
import timber.log.Timber
import java.util.Optional

@UnstableApi
@ExperimentalFoundationApi
class SongItemMenu private constructor(
    private val navController: NavController,
    private val song: Song,
    override val menuState: MenuState,
    override val styleState: MutableState<MenuStyle>
): Menu {

    companion object {
        /**
         * Maximum height can the content box take of the screen.
         * Percentage format with value ranging from 0.0 to 1.0
         * with 0.0 means 0% and 1.0 means 100%.
         *
         * **This value only accountable for the content box,
         * song's preview takes about 10% of screen's height**
         */
        private const val CONTENT_HEIGHT_FRACTION = .4f

        private const val CONTENT_HORIZONTAL_PADDING = 8

        private const val CONTENT_TOP_PADDING = 20

        @Composable
        operator fun invoke( navController: NavController, song: Song ) : SongItemMenu =
            SongItemMenu(
                navController = navController,
                song = song,
                menuState = LocalMenuState.current,
                styleState = rememberPreference( menuStyleKey, MenuStyle.List )
            )
    }

    lateinit var buttons: List<Button>

    @Composable
    override fun ListMenu() {
        val screenHeight = LocalConfiguration.current.screenHeightDp

        Column(
            // With Song preview on top, it should take approx 50% of screen's height
            Modifier
                .heightIn(max = (screenHeight * CONTENT_HEIGHT_FRACTION).dp)
                .padding(
                    start = CONTENT_HORIZONTAL_PADDING.dp,
                    end = CONTENT_HORIZONTAL_PADDING.dp,
                    top = CONTENT_TOP_PADDING.dp
                    // bottom padding is handled by [Modifier#navigationBarsPadding]
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            buttons.forEach {
                if( it is MenuIcon )
                    it.ListMenuItem()
            }
        }
    }

    @Composable
    override fun GridMenu() {
        val screenHeight = LocalConfiguration.current.screenHeightDp

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(
                start = CONTENT_HORIZONTAL_PADDING.dp,
                end = CONTENT_HORIZONTAL_PADDING.dp,
                top = CONTENT_TOP_PADDING.dp
                // bottom padding is handled by [Modifier#navigationBarsPadding]
            ),
            // With Song preview on top, it should take approx 50% of screen's height
            modifier = Modifier
                .heightIn(max = (screenHeight * CONTENT_HEIGHT_FRACTION).dp)
                .navigationBarsPadding()
        ) {
            items( buttons, Button::hashCode ) {
                if( it is MenuIcon)
                    it.GridMenuItem()
            }
        }
    }

    @Composable
    override fun MenuComponent() {
        val context = LocalContext.current
        val binder = LocalPlayerServiceBinder.current

        /*
         * This big chunk of code is currently running as singleton.
         * While it may not have a big impact on performance but
         * it's there. One way to mitigate this is to setup a
         * pre-defined buttons with each button has a function
         * to update song(s). This way the buttons only init once
         * but the song(s) can be updated as we go
         */
        //<editor-fold defaultstate="collapsed" desc="Buttons">
        val renameSong = RenameSongDialog{ song }
        val changeAuthor = ChangeAuthorDialog{ song }
        val startRadio = Radio { listOf(song) }
        val playNext = PlayNext {
            binder?.player?.addNext( listOf(song.asMediaItem), appContext() )
        }
        val enqueue = Enqueue {
            binder?.player?.enqueue( listOf(song.asMediaItem), appContext() )
        }
        val addToFavorite = LikeComponent { listOf(song) }
        val addToPlaylist = PlaylistsMenu.init(
            navController = navController,
            mediaItems = { _ -> listOf(song.asMediaItem) },
            onFailure = { throwable, preview ->
                Timber.e( "Failed to add songs to playlist ${preview.playlist.name} on HomeSongs" )
                throwable.printStackTrace()
            },
            finalAction = {}
        )
        val deleteSongDialog = DeleteSongDialog().apply {
            song = Optional.of( this@SongItemMenu.song )
        }
        val goToArtist = GoToArtist( navController ) { song }
        val goToAlbum = GoToAlbum( navController ) { song }
        val resetDialog = ResetSongDialog( song )

        buttons = mutableListOf<Button>().apply {
            add( renameSong )
            add( changeAuthor )
            add( startRadio )
            add( playNext )
            add( enqueue )
            add( addToFavorite )
            add( addToPlaylist )
            if( !song.isLocal ) {
                add( goToAlbum )
                add( goToArtist )
                add( resetDialog )
            }
            add( deleteSongDialog )
        }
        //</editor-fold>

        //<editor-fold desc="Dialog renders">
        renameSong.Render()
        changeAuthor.Render()
        deleteSongDialog.Render()
        resetDialog.Render()
        //</editor-fold>

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorPalette().background0)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background( colorPalette().background1 )
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Arrow Down",
                    tint = colorPalette().textSecondary,
                    modifier = Modifier.size( 24.dp )
                )

                SongItem(
                    song = song,
                    modifier = Modifier.padding(
                        top = 5.dp,
                        bottom = 10.dp
                    ),
                    trailingContent = {
                        val isLiked by remember {
                            Database.likedAt( song.id ).map { it != null }
                        }.collectAsState( false, Dispatchers.IO )

                        Column(
                            Modifier.width( TabToolBar.TOOLBAR_ICON_SIZE )
                        ) {
                            IconButton(
                                icon = if ( isLiked ) R.drawable.heart else R.drawable.heart_outline,
                                color = colorPalette().favoritesIcon,
                                onClick = {
                                    if ( !isNetworkConnected( context ) && isYouTubeSyncEnabled() ) {
                                        Toaster.e( R.string.no_connection )
                                    } else if ( !isYouTubeSyncEnabled() ){
                                        mediaItemToggleLike( song.asMediaItem )
                                    } else {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            addToYtLikedSong( song.asMediaItem )
                                        }
                                    }
                                },
                                modifier = Modifier.padding( all = 4.dp ).size( 20.dp )
                            )

                            if( !song.isLocal )
                                IconButton(
                                    icon = R.drawable.share_social,
                                    color = colorPalette().text,
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra( Intent.EXTRA_TEXT, "https://music.youtube.com/watch?v=${song.id}" )
                                        }

                                        context.startActivity(
                                            Intent.createChooser( intent, null )
                                        )
                                    },
                                    modifier = Modifier.padding( all = 4.dp ).size( 20.dp )
                                )
                        }
                    }
                )

                HorizontalDivider( Modifier.height(1.dp) )
            }

            if( styleState.value == MenuStyle.List )
                ListMenu()
            else
                GridMenu()
        }
    }
}