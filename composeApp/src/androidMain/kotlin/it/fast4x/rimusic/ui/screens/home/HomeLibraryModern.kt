package it.fast4x.rimusic.ui.screens.home

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import it.fast4x.compose.persist.persistList
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.MONTHLY_PREFIX
import it.fast4x.rimusic.PINNED_PREFIX
import it.fast4x.rimusic.PIPED_PREFIX
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.LibraryItemSize
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PlaylistSortBy
import it.fast4x.rimusic.enums.PlaylistsType
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.query
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.components.ButtonsRow
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.themed.FloatingActionsContainerWithScrollToTop
import it.fast4x.rimusic.ui.components.themed.HeaderInfo
import it.fast4x.rimusic.ui.components.themed.InputTextDialog
import it.fast4x.rimusic.ui.components.themed.Menu
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.ui.components.themed.MultiFloatingActionsContainer
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.items.PlaylistItem
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.utils.CheckMonthlyPlaylist
import it.fast4x.rimusic.utils.ImportPipedPlaylists
import it.fast4x.rimusic.utils.PlayShuffledSongs
import it.fast4x.rimusic.utils.autosyncKey
import it.fast4x.rimusic.utils.createPipedPlaylist
import it.fast4x.rimusic.utils.enableCreateMonthlyPlaylistsKey
import it.fast4x.rimusic.utils.getPipedSession
import it.fast4x.rimusic.utils.isPipedEnabledKey
import it.fast4x.rimusic.utils.libraryItemSizeKey
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.pipedApiTokenKey
import it.fast4x.rimusic.utils.playlistSortByKey
import it.fast4x.rimusic.utils.playlistTypeKey
import it.fast4x.rimusic.utils.rememberEncryptedPreference
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.showFloatingIconKey
import it.fast4x.rimusic.utils.showMonthlyPlaylistsKey
import it.fast4x.rimusic.utils.showPinnedPlaylistsKey
import it.fast4x.rimusic.utils.showPipedPlaylistsKey
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
import timber.log.Timber


@ExperimentalMaterial3Api
@UnstableApi
@ExperimentalMaterialApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun HomeLibraryModern(
    onPlaylistClick: (Playlist) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Timber.d("HomeLibraryModern - start")
    //val windowInsets = LocalPlayerAwareWindowInsets.current
    val menuState = LocalMenuState.current
    val binder = LocalPlayerServiceBinder.current

    var isCreatingANewPlaylist by rememberSaveable {
        mutableStateOf(false)
    }

    val isPipedEnabled by rememberPreference(isPipedEnabledKey, false)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pipedSession = getPipedSession()


    if (isCreatingANewPlaylist) {
        InputTextDialog(
            onDismiss = { isCreatingANewPlaylist = false },
            title = stringResource(R.string.enter_the_playlist_name),
            value = "",
            placeholder = stringResource(R.string.enter_the_playlist_name),
            setValue = { text ->

                if (isPipedEnabled && pipedSession.token.isNotEmpty()) {
                    createPipedPlaylist(
                        context = context,
                        coroutineScope = coroutineScope,
                        pipedSession = pipedSession.toApiSession(),
                        name = text
                    )
                } else {
                    query {
                        Database.insert(Playlist(name = text))
                    }
                }
            }
        )
    }

    if (isPipedEnabled)
        ImportPipedPlaylists()



    // Search states
    val visibleState = rememberSaveable { mutableStateOf(false) }
    val focusState = rememberSaveable { mutableStateOf( false ) }
    val inputState = rememberSaveable { mutableStateOf("") }
    // Sort states
    val sortBy = rememberPreference(playlistSortByKey, PlaylistSortBy.DateAdded)
    val sortOrder = rememberEncryptedPreference(pipedApiTokenKey, SortOrder.Descending)

    val search = remember {
        object: Search {
            override val visibleState = visibleState
            override val focusState = focusState
            override val inputState = inputState
        }
    }
    val sort = remember {
        object: Sort<PlaylistSortBy> {
            override val menuState = menuState
            override val sortOrderState = sortOrder
            override val sortByEnum = PlaylistSortBy.entries
            override val sortByState = sortBy
        }
    }

    // Mutable
    var isSearchBarVisible by search.visibleState
    var isSearchBarFocused by search.focusState
    val searchInput by search.inputState

    var items by persistList<PlaylistPreview>("home/playlists")

    LaunchedEffect(sort.sortByState.value, sort.sortOrderState.value, searchInput) {
        Database.playlistPreviews(sort.sortByState.value, sort.sortOrderState.value).collect { items = it }
    }

    if ( searchInput.isNotBlank() )
        items = items.filter {
            it.playlist.name.contains( searchInput, true )
        }

    var itemSize by rememberPreference(libraryItemSizeKey, LibraryItemSize.Small.size)

    val thumbnailSizeDp = itemSize.dp + 24.dp
    val thumbnailSizePx = thumbnailSizeDp.px

    var plistId by remember {
        mutableStateOf(0L)
    }

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            //requestPermission(activity, "Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED")

            context.applicationContext.contentResolver.openInputStream(uri)
                ?.use { inputStream ->
                    csvReader().open(inputStream) {
                        readAllWithHeaderAsSequence().forEachIndexed { index, row: Map<String, String> ->
                            println("mediaItem index song ${index}")
                            transaction {
                                plistId = row["PlaylistName"]?.let {
                                    Database.playlistExistByName(
                                        it
                                    )
                                } ?: 0L

                                if (plistId == 0L) {
                                    plistId = row["PlaylistName"]?.let {
                                        Database.insert(
                                            Playlist(
                                                name = it,
                                                browseId = row["PlaylistBrowseId"]
                                            )
                                        )
                                    }!!
                                }
                                /**/
                                if (row["MediaId"] != null && row["Title"] != null) {
                                    val song =
                                        row["MediaId"]?.let {
                                            row["Title"]?.let { it1 ->
                                                Song(
                                                    id = it,
                                                    title = it1,
                                                    artistsText = row["Artists"],
                                                    durationText = row["Duration"],
                                                    thumbnailUrl = row["ThumbnailUrl"]
                                                )
                                            }
                                        }
                                    transaction {
                                        if (song != null) {
                                            Database.insert(song)
                                            Database.insert(
                                                SongPlaylistMap(
                                                    songId = song.id,
                                                    playlistId = plistId,
                                                    position = index
                                                )
                                            )
                                        }
                                    }


                                }
                                /**/

                            }

                        }
                    }
                }
        }


    val navigationBarPosition by rememberPreference(
        navigationBarPositionKey,
        NavigationBarPosition.Bottom
    )

    val lazyGridState = rememberLazyGridState()

    val enableCreateMonthlyPlaylists by rememberPreference(enableCreateMonthlyPlaylistsKey, true)

    if (enableCreateMonthlyPlaylists)
        CheckMonthlyPlaylist()

    val showPinnedPlaylists by rememberPreference(showPinnedPlaylistsKey, true)
    val showMonthlyPlaylists by rememberPreference(showMonthlyPlaylistsKey, true)
    val showPipedPlaylists by rememberPreference(showPipedPlaylistsKey, true)
    var playlistType by rememberPreference(playlistTypeKey, PlaylistsType.Playlist)

    var buttonsList = listOf(PlaylistsType.Playlist to stringResource(R.string.playlists))
    if (showPipedPlaylists) buttonsList +=
        PlaylistsType.PipedPlaylist to stringResource(R.string.piped_playlists)
    if (showPinnedPlaylists) buttonsList +=
        PlaylistsType.PinnedPlaylist to stringResource(R.string.pinned_playlists)
    if (showMonthlyPlaylists) buttonsList +=
        PlaylistsType.MonthlyPlaylist to stringResource(R.string.monthly_playlists)

    var autosync by rememberPreference(autosyncKey, false)

    Box(
        modifier = Modifier
            .background(colorPalette().background0)
            //.fillMaxSize()
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
            TabHeader( R.string.playlists ) {
                HeaderInfo( items.size.toString(), R.drawable.playlist )
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

                TabToolBar.Icon(
                    iconId = R.drawable.sync,
                    tint = if (autosync) colorPalette().text else colorPalette().textDisabled,
                    onShortClick = { autosync = !autosync },
                    onLongClick = {
                        SmartMessage(
                            context.resources.getString(R.string.autosync),
                            context = context
                        )
                    }
                )

                search.ToolBarButton()

                TabToolBar.Icon(
                    iconId = R.drawable.shuffle,
                    onLongClick = {
                        SmartMessage(
                            context.resources.getString(R.string.shuffle),
                            context = context
                        )
                    },
                    onShortClick = {
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                when( playlistType ) {
                                    PlaylistsType.Playlist -> Database.songsInAllPlaylists()
                                    PlaylistsType.PinnedPlaylist -> Database.songsInAllPinnedPlaylists()
                                    PlaylistsType.MonthlyPlaylist -> Database.songsInAllMonthlyPlaylists()
                                    PlaylistsType.PipedPlaylist -> Database.songsInAllPipedPlaylists()
                                }.collect {
                                    PlayShuffledSongs( it, context, binder )
                                }
                            }
                        }

                    }
                )

                TabToolBar.Icon(
                    iconId =  R.drawable.add_in_playlist,
                    onShortClick = { isCreatingANewPlaylist = true },
                    onLongClick = {
                        SmartMessage(
                            context.resources.getString(R.string.create_new_playlist),
                            context = context
                        )
                    }
                )

                TabToolBar.Icon(
                    iconId = R.drawable.resource_import,
                    size = 30.dp,
                    onShortClick = {
                        try {
                            importLauncher.launch(
                                arrayOf(
                                    "text/*"
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            SmartMessage(
                                context.resources.getString(R.string.info_not_find_app_open_doc),
                                type = PopupType.Warning, context = context
                            )
                        }
                    },
                    onLongClick = {
                        SmartMessage(
                            context.resources.getString(R.string.import_playlist),
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
                modifier = Modifier
                    .background(colorPalette().background0)
            ) {
                item(
                    key = "separator",
                    contentType = 0,
                    span = { GridItemSpan(maxLineSpan) }) {
                    ButtonsRow(
                        chips = buttonsList,
                        currentValue = playlistType,
                        onValueUpdate = { playlistType = it },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }

                val listPrefix =
                    when( playlistType ) {
                        PlaylistsType.Playlist -> ""    // Matches everything
                        PlaylistsType.PinnedPlaylist -> PINNED_PREFIX
                        PlaylistsType.MonthlyPlaylist -> MONTHLY_PREFIX
                        PlaylistsType.PipedPlaylist -> PIPED_PREFIX
                    }
                val condition: (PlaylistPreview) -> Boolean = {
                    it.playlist.name.startsWith( listPrefix, true )
                }
                items(
                    items = items.filter( condition ),
                    key = { it.playlist.id }
                ) { preview ->
                    PlaylistItem(
                        playlist = preview,
                        thumbnailSizeDp = thumbnailSizeDp,
                        thumbnailSizePx = thumbnailSizePx,
                        alternative = true,
                        modifier = Modifier.fillMaxSize()
                                           .animateItem( fadeInSpec = null, fadeOutSpec = null )
                                           .clickable(onClick = {
                                               if ( isSearchBarVisible )
                                                   if ( searchInput.isBlank() )
                                                       isSearchBarVisible = false
                                                   else
                                                       isSearchBarFocused = false

                                               onPlaylistClick( preview.playlist )
                                           })
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

        FloatingActionsContainerWithScrollToTop(lazyGridState = lazyGridState)

        val showFloatingIcon by rememberPreference(showFloatingIconKey, false)
        if (UiType.ViMusic.isCurrent() && showFloatingIcon)
            MultiFloatingActionsContainer(
                iconId = R.drawable.search,
                onClick = onSearchClick,
                onClickSettings = onSettingsClick,
                onClickSearch = onSearchClick
            )
    }
}