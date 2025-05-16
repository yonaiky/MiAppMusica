package app.kreate.android.themed.rimusic.screen.home.onDevice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastMap
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import it.fast4x.rimusic.EXPLICIT_PREFIX
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.ui.components.SwipeablePlaylistItem
import it.fast4x.rimusic.ui.components.tab.toolbar.Button
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.Preference.HOME_ON_DEVICE_SONGS_SORT_BY
import it.fast4x.rimusic.utils.Preference.HOME_SONGS_SORT_ORDER
import it.fast4x.rimusic.utils.addNext
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.enqueue
import it.fast4x.rimusic.utils.forcePlayAtIndex
import it.fast4x.rimusic.utils.isAtLeastAndroid13
import it.fast4x.rimusic.utils.parentalControlEnabledKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.showFoldersOnDeviceKey
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import me.knighthat.component.FolderItem
import me.knighthat.component.SongItem
import me.knighthat.component.Sort
import me.knighthat.component.tab.ItemSelector
import me.knighthat.component.tab.Search
import me.knighthat.utils.PathUtils
import me.knighthat.utils.Toaster
import me.knighthat.utils.getLocalSongs

@UnstableApi
@ExperimentalFoundationApi
@Composable
fun OnDeviceSong(
    navController: NavController,
    lazyListState: LazyListState,
    itemSelector: ItemSelector<Song>,
    search: Search,
    buttons: MutableList<Button>,
    itemsOnDisplay: MutableList<Song>,
    getSongs: () -> List<Song>,
) {
    // Essentials
    val context = LocalContext.current
    val binder = LocalPlayerServiceBinder.current

    //<editor-fold defaultstate="collapsed" desc="Settings">
    val parentalControlEnabled by rememberPreference( parentalControlEnabledKey, false )
    val showFolder4LocalSongs by rememberPreference( showFoldersOnDeviceKey, true )
    //</editor-fold>

    var songsOnDevice by remember {
        mutableStateOf( emptyMap<Song, String>() )
    }
    var currentPath by remember( songsOnDevice.values ) {
        mutableStateOf( PathUtils.findCommonPath( songsOnDevice.values ) )
    }

    //<editor-fold defaultstate="collapsed" desc="Permission handler">
    val permission = rememberSaveable {
        if( isAtLeastAndroid13 ) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    }
    var isPermissionGranted by remember { mutableStateOf(
        ContextCompat.checkSelfPermission( context, permission ) == PackageManager.PERMISSION_GRANTED
    ) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermissionGranted = it }
    //</editor-fold>

    val odSort = Sort( HOME_ON_DEVICE_SONGS_SORT_BY, HOME_SONGS_SORT_ORDER )

    LaunchedEffect( isPermissionGranted, odSort.sortBy, odSort.sortOrder ) {
        context.getLocalSongs( odSort.sortBy, odSort.sortOrder )
               .distinctUntilChanged()
               .onEach { lazyListState.scrollToItem( 0, 0 ) }
               .collect {
                   songsOnDevice = it

                   it.keys.toList()
               }
    }
    LaunchedEffect( songsOnDevice, search.inputValue, currentPath ) {
        songsOnDevice.keys.filter { !parentalControlEnabled || !it.title.startsWith( EXPLICIT_PREFIX, true ) }
                          .filter {
                              // [showFolder4LocalSongs] must be false and
                              // this song must be inside [currentPath] to show song
                              !showFolder4LocalSongs
                                      || currentPath.equals( songsOnDevice[it], true )
                                      || "$currentPath/".equals( songsOnDevice[it], true )
                          }
                          .filter {
                              // Without cleaning, user can search explicit songs with "e:"
                              // I kinda want this to be a feature, but it seems unnecessary
                              val containsTitle = it.cleanTitle().contains( search.inputValue, true )
                              val containsArtist = it.cleanArtistsText().contains( search.inputValue, true )

                              containsTitle || containsArtist
                          }
                          .let {
                              itemsOnDisplay.clear()
                              itemsOnDisplay.addAll( it )
                          }
    }
    LaunchedEffect( Unit ) {
        buttons.add( 0, odSort )

        if( isPermissionGranted ) return@LaunchedEffect

        try {
            permissionLauncher.launch( permission )
        } catch ( e: Exception ) {
            e.message?.let( Toaster::e )
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues( bottom = Dimensions.bottomSpacer )
    ) {
        if( showFolder4LocalSongs && songsOnDevice.isNotEmpty() ) {
            item( "folder_paths" ) {
                PathUtils.AddressBar(
                    paths = songsOnDevice.values,
                    currentPath = currentPath,
                    onSpecificAddressClick = { currentPath = it }
                )
            }

            items(
                items = PathUtils.getAvailablePaths( songsOnDevice.values, currentPath ),
                key = { it }
            ) {
                FolderItem( it ) { currentPath += "/$it" }
            }
        }

        itemsIndexed(
            items = itemsOnDisplay,
            key = { _, song -> song.id }
        ) { index, song ->
            val mediaItem = song.asMediaItem

            SwipeablePlaylistItem(
                mediaItem = mediaItem,
                onPlayNext = { binder?.player?.addNext( mediaItem ) },
                onEnqueue = {
                    binder?.player?.enqueue(mediaItem)
                }
            ) {
                SongItem(
                    song = song,
                    itemSelector = itemSelector,
                    navController = navController,
                    modifier = Modifier.animateItem(),
                    onClick = {
                        search.hideIfEmpty()

                        val mediaItems = getSongs().fastMap( Song::asMediaItem )
                        binder?.player?.forcePlayAtIndex( mediaItems, index )
                    }
                )
            }
        }
    }
}