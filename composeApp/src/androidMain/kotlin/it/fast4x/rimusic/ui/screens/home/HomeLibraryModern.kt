package it.fast4x.rimusic.ui.screens.home

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
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
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.components.themed.InputTextDialog
import it.fast4x.rimusic.ui.components.themed.Menu
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.ui.components.themed.MultiFloatingActionsContainer
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.components.themed.SortMenu
import it.fast4x.rimusic.ui.items.PlaylistItem
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.favoritesIcon
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
import it.fast4x.rimusic.utils.secondary
import it.fast4x.rimusic.utils.semiBold
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
import me.knighthat.typography
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

    var sortBy by rememberPreference(playlistSortByKey, PlaylistSortBy.DateAdded)
    var sortOrder by rememberEncryptedPreference(pipedApiTokenKey, SortOrder.Descending)

    var searching by rememberSaveable { mutableStateOf(false) }
    var isSearchInputFocused by rememberSaveable { mutableStateOf( false ) }
    var filter by rememberSaveable { mutableStateOf("") }

    var items by persistList<PlaylistPreview>("home/playlists")

    LaunchedEffect(sortBy, sortOrder, filter) {
        Database.playlistPreviews(sortBy, sortOrder).collect { items = it }
    }

    if ( filter.isNotBlank() )
        items = items
            .filter {
                it.playlist.name.contains( filter, true )
            }

    val sortOrderIconRotation by animateFloatAsState(
        targetValue = if (sortOrder == SortOrder.Ascending) 0f else 180f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing), label = ""
    )

    var itemSize by rememberPreference(libraryItemSizeKey, LibraryItemSize.Small.size)

    val thumbnailSizeDp = itemSize.dp + 24.dp
    val thumbnailSizePx = thumbnailSizeDp.px

    //val endPaddingValues = windowInsets.only(WindowInsetsSides.End).asPaddingValues()

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

    val showSearchTab by rememberPreference(showSearchTabKey, false)

    val enableCreateMonthlyPlaylists by rememberPreference(enableCreateMonthlyPlaylistsKey, true)

    //println("mediaItem ${getCalculatedMonths(0)} ${getCalculatedMonths(1)}")
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

    val thumbnailRoundness by rememberPreference(
        thumbnailRoundnessKey,
        ThumbnailRoundness.Heavy
    )
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
        TabHeader( R.string.playlists ) {
            HeaderInfo( items.size.toString(), R.drawable.playlist )
        }

        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Adaptive(itemSize.dp + 24.dp),
            modifier = Modifier
                .padding( top = TabHeader.height() )
                .align(Alignment.BottomStart)
                .background(colorPalette().background0)

        ) {
            item(
                key = "headerButtons",
                contentType = 0,
                span = { GridItemSpan(maxLineSpan) }
            ) {

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                ) {

                    TabToolBar.Icon(
                        iconId = R.drawable.arrow_up,
                        onShortClick = { sortOrder = !sortOrder },
                        onLongClick = {
                            menuState.display {
                                SortMenu(
                                    title = stringResource(R.string.sorting_order),
                                    onDismiss = menuState::hide,
                                    onName = { sortBy = PlaylistSortBy.Name },
                                    onSongNumber = {
                                        sortBy = PlaylistSortBy.SongCount
                                    },
                                    onDateAdded = { sortBy = PlaylistSortBy.DateAdded },
                                    onPlayTime = { sortBy = PlaylistSortBy.MostPlayed },
                                )
                            }
                        }
                    )

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

                    TabToolBar.Icon( iconId  = R.drawable.search_circle ) {
                        searching = !searching
                        isSearchInputFocused = searching
                    }

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
                                    when (playlistType) {
                                        PlaylistsType.Playlist -> {
                                            Database.songsInAllPlaylists()
                                                .collect {
                                                    PlayShuffledSongs(
                                                        songsList = it,
                                                        binder = binder,
                                                        context = context
                                                    )
                                                }
                                        }

                                        PlaylistsType.PipedPlaylist -> {
                                            Database.songsInAllPipedPlaylists()
                                                .collect {
                                                    PlayShuffledSongs(
                                                        songsList = it,
                                                        binder = binder,
                                                        context = context
                                                    )
                                                }
                                        }

                                        PlaylistsType.PinnedPlaylist -> {
                                            Database.songsInAllPinnedPlaylists()
                                                .collect {
                                                    PlayShuffledSongs(
                                                        songsList = it,
                                                        binder = binder,
                                                        context = context
                                                    )
                                                }
                                        }

                                        PlaylistsType.MonthlyPlaylist -> {
                                            Database.songsInAllMonthlyPlaylists()
                                                .collect {
                                                    PlayShuffledSongs(
                                                        songsList = it,
                                                        binder = binder,
                                                        context = context
                                                    )
                                                }
                                        }
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
            }

            if (searching)
                item(
                    key = "headerFilter",
                    contentType = 0,
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    /*        */
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            //.requiredHeight(30.dp)
                            .padding(all = 10.dp)
                            .fillMaxWidth()
                    ) {
                        AnimatedVisibility(visible = searching) {
                            val focusRequester = remember { FocusRequester() }
                            val focusManager = LocalFocusManager.current

                            LaunchedEffect(searching) {
                                if (isSearchInputFocused) focusRequester.requestFocus()
                            }

                            var searchInput by remember { mutableStateOf(TextFieldValue(filter)) }
                            BasicTextField(
                                value = searchInput,
                                onValueChange = {
                                    searchInput = it.copy(
                                        selection = TextRange(it.text.length)
                                    )
                                    filter = it.text
                                },
                                textStyle = typography().xs.semiBold,
                                singleLine = true,
                                maxLines = 1,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                    searching = filter.isNotBlank()
                                }),
                                cursorBrush = SolidColor(colorPalette().text),
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 10.dp)
                                    ) {
                                        IconButton(
                                            onClick = {},
                                            icon = R.drawable.search,
                                            color = colorPalette().favoritesIcon,
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .size(16.dp)
                                        )
                                    }
                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 30.dp)
                                    ) {
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = filter.isBlank(),
                                            enter = fadeIn(tween(100)),
                                            exit = fadeOut(tween(100)),
                                        ) {
                                            BasicText(
                                                text = stringResource(R.string.search),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = typography().xs.semiBold.secondary.copy(
                                                    color = colorPalette().textDisabled
                                                )
                                            )
                                        }

                                        innerTextField()
                                    }
                                },
                                modifier = Modifier
                                    .height(30.dp)
                                    .fillMaxWidth()
                                    .background(
                                        colorPalette().background4,
                                        shape = thumbnailRoundness.shape()
                                    )
                                    .focusRequester(focusRequester)
                            )
                        }
                    }
                }

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

            if (playlistType == PlaylistsType.Playlist) {
                items(items = items,
                    /*
                    .filter {
                    !it.playlist.name.startsWith(PINNED_PREFIX, 0, true) &&
                            !it.playlist.name.startsWith(MONTHLY_PREFIX, 0, true)
                    }

                     */
                    key = { it.playlist.id }) { playlistPreview ->

                    PlaylistItem(
                        playlist = playlistPreview,
                        thumbnailSizeDp = thumbnailSizeDp,
                        thumbnailSizePx = thumbnailSizePx,
                        alternative = true,
                        modifier = Modifier
                            .clickable(onClick = {
                                onPlaylistClick(playlistPreview.playlist)

                                if (searching)
                                    if (filter.isBlank())
                                        searching = false
                                    else
                                        isSearchInputFocused = false
                            })
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .fillMaxSize()
                    )
                }
            }

            if (playlistType == PlaylistsType.PipedPlaylist)
                items(items = items.filter {
                    it.playlist.name.startsWith(PIPED_PREFIX, 0, true)
                }, key = { it.playlist.id }) { playlistPreview ->
                    PlaylistItem(
                        playlist = playlistPreview,
                        thumbnailSizeDp = thumbnailSizeDp,
                        thumbnailSizePx = thumbnailSizePx,
                        alternative = true,
                        modifier = Modifier
                            .clickable(onClick = {
                                onPlaylistClick(playlistPreview.playlist)

                                if (searching)
                                    if (filter.isBlank())
                                        searching = false
                                    else
                                        isSearchInputFocused = false
                            })
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .fillMaxSize()
                    )
                }

            if (playlistType == PlaylistsType.PinnedPlaylist)
                items(items = items.filter {
                    it.playlist.name.startsWith(PINNED_PREFIX, 0, true)
                }, key = { it.playlist.id }) { playlistPreview ->
                    PlaylistItem(
                        playlist = playlistPreview,
                        thumbnailSizeDp = thumbnailSizeDp,
                        thumbnailSizePx = thumbnailSizePx,
                        alternative = true,
                        modifier = Modifier
                            .clickable(onClick = {
                                onPlaylistClick(playlistPreview.playlist)

                                if (searching)
                                    if (filter.isBlank())
                                        searching = false
                                    else
                                        isSearchInputFocused = false
                            })
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .fillMaxSize()
                    )
                }

            if (playlistType == PlaylistsType.MonthlyPlaylist)
                items(items = items.filter {
                    it.playlist.name.startsWith(MONTHLY_PREFIX, 0, true)
                }, key = { it.playlist.id }) { playlistPreview ->
                    PlaylistItem(
                        playlist = playlistPreview,
                        thumbnailSizeDp = thumbnailSizeDp,
                        thumbnailSizePx = thumbnailSizePx,
                        alternative = true,
                        modifier = Modifier
                            .clickable(onClick = {
                                onPlaylistClick(playlistPreview.playlist)

                                if (searching)
                                    if (filter.isBlank())
                                        searching = false
                                    else
                                        isSearchInputFocused = false
                            })
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .fillMaxSize()
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

        FloatingActionsContainerWithScrollToTop(lazyGridState = lazyGridState)

        val showFloatingIcon by rememberPreference(showFloatingIconKey, false)
        if (UiType.ViMusic.isCurrent() && showFloatingIcon)
            MultiFloatingActionsContainer(
                iconId = R.drawable.search,
                onClick = onSearchClick,
                onClickSettings = onSettingsClick,
                onClickSearch = onSearchClick
            )

        /*
    FloatingActionsContainerWithScrollToTop(
            lazyGridState = lazyGridState,
            iconId = R.drawable.search,
            onClick = onSearchClick
        )
         */
    }
}