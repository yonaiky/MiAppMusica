package it.fast4x.rimusic.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import it.fast4x.compose.persist.persist
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.MODIFIED_PREFIX
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.AlbumSortBy
import it.fast4x.rimusic.enums.LibraryItemSize
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.query
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.AlbumsItemMenu
import it.fast4x.rimusic.ui.components.themed.FloatingActionsContainerWithScrollToTop
import it.fast4x.rimusic.ui.components.themed.HeaderInfo
import it.fast4x.rimusic.ui.components.themed.InputTextDialog
import it.fast4x.rimusic.ui.components.themed.Menu
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.ui.components.themed.MultiFloatingActionsContainer
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.components.themed.SortMenu
import it.fast4x.rimusic.ui.items.AlbumItem
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.utils.PlayShuffledSongs
import it.fast4x.rimusic.utils.addNext
import it.fast4x.rimusic.utils.albumSortByKey
import it.fast4x.rimusic.utils.albumSortOrderKey
import it.fast4x.rimusic.utils.albumsItemSizeKey
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.enqueue
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.showFloatingIconKey
import it.fast4x.rimusic.utils.showSearchTabKey
import it.fast4x.rimusic.utils.thumbnailRoundnessKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.knighthat.colorPalette
import me.knighthat.component.header.TabToolBar
import me.knighthat.component.tab.TabHeader
import me.knighthat.component.tab.toolbar.Search
import me.knighthat.component.tab.toolbar.Sort
import me.knighthat.thumbnailShape
import kotlin.enums.EnumEntries
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalTextApi
@UnstableApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun HomeAlbumsModern(
    onAlbumClick: (Album) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val menuState = LocalMenuState.current
    val binder = LocalPlayerServiceBinder.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var items by persist<List<Album>>(tag = "home/albums", emptyList())

    // Search states
    val visibleState = rememberSaveable { mutableStateOf(false) }
    val focusState = rememberSaveable { mutableStateOf( false ) }
    val inputState = rememberSaveable { mutableStateOf( "" ) }
    // Sort states
    val sortBy = rememberPreference(albumSortByKey, AlbumSortBy.DateAdded)
    val sortOrder = rememberPreference(albumSortOrderKey, SortOrder.Descending)

    val search = remember {
        object: Search {
            override val visibleState = visibleState
            override val focusState = focusState
            override val inputState = inputState
        }
    }
    val sort = remember {
        object: Sort<AlbumSortBy> {
            override val menuState = menuState
            override val sortOrderState = sortOrder
            override val sortByEnum = AlbumSortBy.entries
            override val sortByState = sortBy
        }
    }

    // Mutable
    var isSearchBarVisible by search.visibleState
    var isSearchBarFocused by search.focusState
    val searchInput by search.inputState

    LaunchedEffect(sort.sortByState.value, sort.sortOrderState.value, searchInput) {
        Database.albums(sort.sortByState.value, sort.sortOrderState.value).collect { items = it }
    }

    if ( searchInput.isNotBlank() )
        items = items.filter {
            it.title?.contains( searchInput, true) ?: false
                    || it.year?.contains( searchInput, true) ?: false
                    || it.authorsText?.contains( searchInput, true) ?: false
        }

    var itemSize by rememberPreference(albumsItemSizeKey, LibraryItemSize.Small.size)
    val thumbnailSizeDp = itemSize.dp + 24.dp
    val thumbnailSizePx = thumbnailSizeDp.px

    val navigationBarPosition by rememberPreference(
        navigationBarPositionKey,
        NavigationBarPosition.Bottom
    )

    val lazyListState = rememberLazyListState()

    var isRotated by rememberSaveable { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 360F else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    val lazyGridState = rememberLazyGridState()
    Box(
        modifier = Modifier
            .background(colorPalette().background0)
            .fillMaxHeight()
            .fillMaxWidth(
                if (navigationBarPosition == NavigationBarPosition.Left ||
                    navigationBarPosition == NavigationBarPosition.Top ||
                    navigationBarPosition == NavigationBarPosition.Bottom
                ) 1f
                else Dimensions.contentWidthRightBar
            )
    ) {
        Column( Modifier.fillMaxSize() ) {
            // Sticky tab's title
            TabHeader( R.string.albums ) {
                HeaderInfo( items.size.toString(), R.drawable.album )
            }

            // Sticky tab's tool bar
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            ) {
                sort.ToolBarButton()

                search.ToolBarButton()

                TabToolBar.Icon(
                    iconId = R.drawable.dice,
                    enabled = items.isNotEmpty(),
                    modifier = Modifier.rotate( rotationAngle )
                ) {
                    isRotated = !isRotated

                    val randIndex = Random( System.currentTimeMillis() ).nextInt( items.size )
                    onAlbumClick( items[randIndex] )
                }

                TabToolBar.Icon(
                    iconId = R.drawable.shuffle,
                    onShortClick = {
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                Database.songsInAllBookmarkedAlbums()
                                    .collect { PlayShuffledSongs(songsList = it, binder = binder, context = context) }
                            }
                        }

                    },
                    onLongClick = {
                        SmartMessage(
                            context.resources.getString(R.string.shuffle),
                            context = context
                        )
                    }
                )

                TabToolBar.Icon( R.drawable.resize ) {
                    menuState.display {
                        Menu {
                            MenuEntry(
                                icon = R.drawable.arrow_forward,
                                text = stringResource(R.string.small),
                                onClick = {
                                    itemSize = LibraryItemSize.Small.size
                                    menuState.hide()
                                }
                            )
                            MenuEntry(
                                icon = R.drawable.arrow_forward,
                                text = stringResource(R.string.medium),
                                onClick = {
                                    itemSize = LibraryItemSize.Medium.size
                                    menuState.hide()
                                }
                            )
                            MenuEntry(
                                icon = R.drawable.arrow_forward,
                                text = stringResource(R.string.big),
                                onClick = {
                                    itemSize = LibraryItemSize.Big.size
                                    menuState.hide()
                                }
                            )
                        }
                    }
                }
            }

            // Sticky search bar
            search.SearchBar( this )

            LazyVerticalGrid(
                state = lazyGridState,
                columns = GridCells.Adaptive(itemSize.dp + 24.dp),
                //contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
                modifier = Modifier
                    .background(colorPalette().background0)
                    .fillMaxSize()
            ) {
                items(
                    items = items,
                    key = Album::id
                ) { album ->
                    var songs = remember { listOf<Song>() }
                    query {
                        songs = Database.albumSongsList(album.id)
                    }

                    var showDialogChangeAlbumTitle by remember {
                        mutableStateOf(false)
                    }
                    var showDialogChangeAlbumAuthors by remember {
                        mutableStateOf(false)
                    }
                    var showDialogChangeAlbumCover by remember {
                        mutableStateOf(false)
                    }

                    var onDismiss: () -> Unit = {}
                    var titleId = 0
                    var defValue = ""
                    var placeholderTextId: Int = 0
                    var queryBlock: (Database, String, String) -> Int = { _, _, _ -> 0}

                    if( showDialogChangeAlbumCover ) {
                        onDismiss = { showDialogChangeAlbumCover = false }
                        titleId = R.string.update_cover
                        defValue = album.thumbnailUrl.toString()
                        placeholderTextId = R.string.cover
                        queryBlock = Database::updateAlbumCover
                    } else if( showDialogChangeAlbumTitle ) {
                        onDismiss = { showDialogChangeAlbumTitle = false }
                        titleId = R.string.update_title
                        defValue = album.title.toString()
                        placeholderTextId = R.string.title
                        queryBlock = Database::updateAlbumTitle
                    } else if( showDialogChangeAlbumAuthors ) {
                        onDismiss = { showDialogChangeAlbumAuthors = false }
                        titleId = R.string.update_authors
                        defValue = album.authorsText.toString()
                        placeholderTextId = R.string.authors
                        queryBlock = Database::updateAlbumAuthors
                    }

                    if( showDialogChangeAlbumTitle || showDialogChangeAlbumAuthors || showDialogChangeAlbumCover )
                        InputTextDialog(
                            onDismiss = onDismiss,
                            title = stringResource( titleId ),
                            value = defValue,
                            placeholder = stringResource( placeholderTextId ),
                            setValue = {
                                if (it.isNotEmpty())
                                    query { queryBlock( Database, album.id, it ) }
                            },
                            prefix = MODIFIED_PREFIX
                        )

                    var position by remember {
                        mutableIntStateOf(0)
                    }
                    val context = LocalContext.current

                    AlbumItem(
                        alternative = true,
                        showAuthors = true,
                        album = album,
                        thumbnailSizePx = thumbnailSizePx,
                        thumbnailSizeDp = thumbnailSizeDp,
                        modifier = Modifier
                            .combinedClickable(

                                onLongClick = {
                                    menuState.display {
                                        AlbumsItemMenu(
                                            onDismiss = menuState::hide,
                                            album = album,
                                            onChangeAlbumTitle = {
                                                showDialogChangeAlbumTitle = true
                                            },
                                            onChangeAlbumAuthors = {
                                                showDialogChangeAlbumAuthors = true
                                            },
                                            onChangeAlbumCover = {
                                                showDialogChangeAlbumCover = true
                                            },
                                            onPlayNext = {
                                                println("mediaItem ${songs}")
                                                binder?.player?.addNext(
                                                    songs.map(Song::asMediaItem), context
                                                )

                                            },
                                            onEnqueue = {
                                                println("mediaItem ${songs}")
                                                binder?.player?.enqueue(
                                                    songs.map(Song::asMediaItem), context
                                                )

                                            },
                                            onAddToPlaylist = { playlistPreview ->
                                                position =
                                                    playlistPreview.songCount.minus(1) ?: 0
                                                //Log.d("mediaItem", " maxPos in Playlist $it ${position}")
                                                if (position > 0) position++ else position =
                                                    0
                                                //Log.d("mediaItem", "next initial pos ${position}")
                                                //if (listMediaItems.isEmpty()) {
                                                songs.forEachIndexed { index, song ->
                                                    transaction {
                                                        Database.insert(song.asMediaItem)
                                                        Database.insert(
                                                            SongPlaylistMap(
                                                                songId = song.asMediaItem.mediaId,
                                                                playlistId = playlistPreview.playlist.id,
                                                                position = position + index
                                                            )
                                                        )
                                                    }
                                                    //Log.d("mediaItemPos", "added position ${position + index}")
                                                }
                                                //}
                                            }
                                        )
                                    }
                                },
                                onClick = {
                                    if ( isSearchBarVisible )
                                        if ( searchInput.isBlank() )
                                            isSearchBarVisible = false
                                        else
                                            isSearchBarFocused = false

                                    onAlbumClick( album )
                                }
                            )
                            .clip(thumbnailShape())
                    )
                }

                item(
                    key = "footer",
                    contentType = 0,
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Spacer(modifier = Modifier.height(Dimensions.bottomSpacer))
                }
            }
        }

        FloatingActionsContainerWithScrollToTop(lazyListState = lazyListState)

        val showFloatingIcon by rememberPreference(showFloatingIconKey, false)
        if ( UiType.ViMusic.isCurrent() && showFloatingIcon )
            MultiFloatingActionsContainer(
                iconId = R.drawable.search,
                onClick = onSearchClick,
                onClickSettings = onSettingsClick,
                onClickSearch = onSearchClick
            )
    }
}
