package it.fast4x.rimusic.ui.screens.home

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavController
import com.coder.vincent.smart_toast.SmartToast
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import it.fast4x.compose.persist.persistList
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.BuiltInPlaylist
import it.fast4x.rimusic.enums.MaxSongs
import it.fast4x.rimusic.enums.MaxTopPlaylistItems
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.enums.SongSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.models.SongWithContentLength
import it.fast4x.rimusic.query
import it.fast4x.rimusic.service.DownloadUtil
import it.fast4x.rimusic.service.LOCAL_KEY_PREFIX
import it.fast4x.rimusic.service.isLocal
import it.fast4x.rimusic.ui.components.ButtonsRow
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.Popup
import it.fast4x.rimusic.ui.components.themed.ConfirmationDialog
import it.fast4x.rimusic.ui.components.themed.FloatingActionsContainerWithScrollToTop
import it.fast4x.rimusic.ui.components.themed.HeaderIconButton
import it.fast4x.rimusic.ui.components.themed.HeaderInfo
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.components.themed.InHistoryMediaItemMenu
import it.fast4x.rimusic.ui.components.themed.InputTextDialog
import it.fast4x.rimusic.ui.components.themed.MultiFloatingActionsContainer
import it.fast4x.rimusic.ui.components.themed.PlaylistsItemMenu
import it.fast4x.rimusic.ui.components.themed.SmartToast
import it.fast4x.rimusic.ui.components.themed.SortMenu
import it.fast4x.rimusic.ui.components.themed.Title
import it.fast4x.rimusic.ui.components.themed.TitleSection
import it.fast4x.rimusic.ui.items.SongItem
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.ui.styling.onOverlay
import it.fast4x.rimusic.ui.styling.overlay
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.utils.MaxTopPlaylistItemsKey
import it.fast4x.rimusic.utils.UiTypeKey
import it.fast4x.rimusic.utils.addNext
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.autoShuffleKey
import it.fast4x.rimusic.utils.builtInPlaylistKey
import it.fast4x.rimusic.utils.center
import it.fast4x.rimusic.utils.color
import it.fast4x.rimusic.utils.downloadedStateMedia
import it.fast4x.rimusic.utils.enqueue
import it.fast4x.rimusic.utils.forcePlayAtIndex
import it.fast4x.rimusic.utils.forcePlayFromBeginning
import it.fast4x.rimusic.utils.getDownloadState
import it.fast4x.rimusic.utils.includeLocalSongsKey
import it.fast4x.rimusic.utils.manageDownload
import it.fast4x.rimusic.utils.maxSongsInQueueKey
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.preferences
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.secondary
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.utils.showFloatingIconKey
import it.fast4x.rimusic.utils.showSearchTabKey
import it.fast4x.rimusic.utils.songSortByKey
import it.fast4x.rimusic.utils.songSortOrderKey
import it.fast4x.rimusic.utils.thumbnailRoundnessKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


@ExperimentalTextApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@UnstableApi
@Composable
fun HomeSongsModern(
    navController: NavController,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val (colorPalette, typography, thumbnailShape) = LocalAppearance.current
    val binder = LocalPlayerServiceBinder.current
    val menuState = LocalMenuState.current
    val uiType  by rememberPreference(UiTypeKey, UiType.RiMusic)

    val thumbnailSizeDp = Dimensions.thumbnails.song
    val thumbnailSizePx = thumbnailSizeDp.px

    var sortBy by rememberPreference(songSortByKey, SongSortBy.DateAdded)
    var sortOrder by rememberPreference(songSortOrderKey, SortOrder.Descending)

    var items by persistList<Song>("home/songs")

    /*
    var filterDownloaded by remember {
        mutableStateOf(false)
    }
     */

    var filter: String? by rememberSaveable { mutableStateOf(null) }
    var builtInPlaylist by rememberPreference(
        builtInPlaylistKey,
        BuiltInPlaylist.Favorites
    )

    var downloadState by remember {
        mutableStateOf(Download.STATE_STOPPED)
    }

    val context = LocalContext.current

    var thumbnailRoundness by rememberPreference(
        thumbnailRoundnessKey,
        ThumbnailRoundness.Heavy
    )

    var showHiddenSongs by remember {
        mutableStateOf(0)
    }

    var includeLocalSongs by rememberPreference(includeLocalSongsKey, true)
    var autoShuffle by rememberPreference(autoShuffleKey, false)

    val maxTopPlaylistItems by rememberPreference(
        MaxTopPlaylistItemsKey,
        MaxTopPlaylistItems.`10`
    )

    when (builtInPlaylist) {
        BuiltInPlaylist.All -> {
            LaunchedEffect(sortBy, sortOrder, filter, showHiddenSongs, includeLocalSongs) {
                Database.songs(sortBy, sortOrder, showHiddenSongs).collect { items = it }
            }
        }
        BuiltInPlaylist.Downloaded, BuiltInPlaylist.Favorites, BuiltInPlaylist.Offline, BuiltInPlaylist.Top -> {
            LaunchedEffect(Unit, builtInPlaylist, sortBy, sortOrder, filter, autoShuffle) {

                    if (builtInPlaylist == BuiltInPlaylist.Downloaded) {
                        val downloads = DownloadUtil.downloads.value
                        Database.listAllSongsAsFlow()
                            .map {
                                it.filter { song ->
                                    downloads[song.id]?.state == Download.STATE_COMPLETED
                                }
                            }
                            .collect {
                                items = it
                            }
                    }

                    if (builtInPlaylist == BuiltInPlaylist.Favorites) {
                        Database.songsFavorites(sortBy, sortOrder)
                            .collect {
                                items =
                                    if (autoShuffle)
                                        it.shuffled()
                                    else it
                            }
                    }

                if (builtInPlaylist == BuiltInPlaylist.Offline) {
                    Database
                        .songsOffline(sortBy, sortOrder)
                        .flowOn(Dispatchers.IO)
                        .map { songs ->
                            songs.filter { song ->
                                song.contentLength?.let {
                                    binder?.cache?.isCached(song.song.id, 0, song.contentLength)
                                } ?: false
                            }.map(SongWithContentLength::song)
                        }
                        .collect {
                            items = it
                        }
                }
                if (builtInPlaylist == BuiltInPlaylist.Top) {
                    Database.trending(maxTopPlaylistItems.number.toInt())
                        .collect {
                            items = it
                        }
                }



            }
        }
        BuiltInPlaylist.OnDevice -> {}
    }

    if (!includeLocalSongs)
        items = items
            .filter {
                !it.id.startsWith(LOCAL_KEY_PREFIX)
            }

    if (builtInPlaylist == BuiltInPlaylist.Downloaded) {
        when (sortOrder) {
            SortOrder.Ascending -> {
                when (sortBy) {
                    SongSortBy.Title -> items = items.sortedBy { it.title }
                    SongSortBy.PlayTime -> items = items.sortedBy { it.totalPlayTimeMs }
                    SongSortBy.Duration -> items = items.sortedBy { it.durationText }
                    SongSortBy.Artist -> items = items.sortedBy { it.artistsText }
                    SongSortBy.DatePlayed -> {}
                    SongSortBy.DateLiked -> items = items.sortedBy { it.likedAt }
                    SongSortBy.DateAdded -> {}
                }
            }
            SortOrder.Descending -> {
                when (sortBy) {
                    SongSortBy.Title -> items = items.sortedByDescending { it.title }
                    SongSortBy.PlayTime -> items = items.sortedByDescending { it.totalPlayTimeMs }
                    SongSortBy.Duration -> items = items.sortedByDescending { it.durationText }
                    SongSortBy.Artist -> items = items.sortedByDescending { it.artistsText }
                    SongSortBy.DatePlayed -> {}
                    SongSortBy.DateLiked -> items = items.sortedByDescending { it.likedAt }
                    SongSortBy.DateAdded -> {}
                }
            }
        }

    }

    var filterCharSequence: CharSequence
    filterCharSequence = filter.toString()
    //Log.d("mediaItemFilter", "<${filter}>  <${filterCharSequence}>")
    if (!filter.isNullOrBlank())
        items = items
            .filter {
                it.title.contains(filterCharSequence,true) ?: false
                        || it.artistsText?.contains(filterCharSequence,true) ?: false
            }

    var searching by rememberSaveable { mutableStateOf(false) }

    val sortOrderIconRotation by animateFloatAsState(
        targetValue = if (sortOrder == SortOrder.Ascending) 0f else 180f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing), label = ""
    )


    val navigationBarPosition by rememberPreference(navigationBarPositionKey, NavigationBarPosition.Left)

    val lazyListState = rememberLazyListState()

    val showSearchTab by rememberPreference(showSearchTabKey, false)
    val maxSongsInQueue  by rememberPreference(maxSongsInQueueKey, MaxSongs.`500`)

    var listMediaItems = remember {
        mutableListOf<MediaItem>()
    }

    var selectItems by remember {
        mutableStateOf(false)
    }

    var position by remember {
        mutableIntStateOf(0)
    }

    var plistName by remember {
        mutableStateOf("")
    }

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            context.applicationContext.contentResolver.openOutputStream(uri)
                ?.use { outputStream ->
                    csvWriter().open(outputStream){
                        writeRow("PlaylistBrowseId", "PlaylistName", "MediaId", "Title", "Artists", "Duration", "ThumbnailUrl")
                        if (listMediaItems.isEmpty()) {
                            items.forEach {
                                writeRow(
                                    "",
                                    plistName,
                                    it.id,
                                    it.title,
                                    it.artistsText,
                                    it.durationText,
                                    it.thumbnailUrl
                                )
                            }
                        } else {
                            listMediaItems.forEach {
                                writeRow(
                                    "",
                                    plistName,
                                    it.mediaId,
                                    it.mediaMetadata.title,
                                    it.mediaMetadata.artist,
                                    "",
                                    it.mediaMetadata.artworkUri
                                )
                            }
                        }
                    }
                }

        }

    var isExporting by rememberSaveable {
        mutableStateOf(false)
    }

    if (isExporting) {
        InputTextDialog(
            onDismiss = {
                isExporting = false
            },
            title = stringResource(R.string.enter_the_playlist_name),
            value = when (builtInPlaylist) {
                BuiltInPlaylist.All -> context.resources.getString(R.string.songs)
                BuiltInPlaylist.OnDevice -> context.resources.getString(R.string.on_device)
                BuiltInPlaylist.Favorites -> context.resources.getString(R.string.favorites)
                BuiltInPlaylist.Downloaded -> context.resources.getString(R.string.downloaded)
                BuiltInPlaylist.Offline -> context.resources.getString(R.string.cached)
                BuiltInPlaylist.Top -> context.resources.getString(R.string.playlist_top)
            },
            placeholder = stringResource(R.string.enter_the_playlist_name),
            setValue = { text ->
                plistName = text
                try {
                    @SuppressLint("SimpleDateFormat")
                    val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
                    exportLauncher.launch("RMPlaylist_${text.take(20)}_${dateFormat.format(
                        Date()
                    )}")
                } catch (e: ActivityNotFoundException) {
                    SmartToast("Couldn't find an application to create documents",
                        type = PopupType.Warning)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .background(colorPalette.background0)
            //.fillMaxSize()
            .fillMaxHeight()
            //.fillMaxWidth(if (navigationBarPosition == NavigationBarPosition.Left) 1f else Dimensions.contentWidthRightBar)
            .fillMaxWidth(
                if (navigationBarPosition == NavigationBarPosition.Left ||
                    navigationBarPosition == NavigationBarPosition.Top ||
                    navigationBarPosition == NavigationBarPosition.Bottom
                ) 1f
                else Dimensions.contentWidthRightBar
            )
    ) {
        LazyColumn(
            state = lazyListState,
            //contentPadding = LocalPlayerAwareWindowInsets.current
            //    .only(WindowInsetsSides.Vertical + WindowInsetsSides.End).asPaddingValues(),
        ) {
            item(
                key = "header",
                contentType = 0
            ) {

                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(all = 12.dp)
                        .fillMaxSize()
                ) {
                    TitleSection(title = stringResource(R.string.songs))
                    HeaderInfo(
                        title = "${items.size}",
                        icon = painterResource(R.drawable.musical_notes)
                    )
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )
                    HeaderIconButton(
                        icon = R.drawable.arrow_up,
                        color = colorPalette.text,
                        onClick = {},
                        modifier = Modifier
                            .graphicsLayer { rotationZ = sortOrderIconRotation }
                            .combinedClickable(
                                onClick = { sortOrder = !sortOrder },
                                onLongClick = {
                                    menuState.display {
                                        SortMenu(
                                            title = stringResource(R.string.sorting_order),
                                            onDismiss = menuState::hide,
                                            onTitle = { sortBy = SongSortBy.Title },
                                            onDatePlayed = { sortBy = SongSortBy.DatePlayed },
                                            onDateAdded = { sortBy = SongSortBy.DateAdded },
                                            onPlayTime = { sortBy = SongSortBy.PlayTime },
                                            onDateLiked = { sortBy = SongSortBy.DateLiked },
                                            onArtist = { sortBy = SongSortBy.Artist },
                                            onDuration = { sortBy = SongSortBy.Duration }
                                        )
                                    }
                                }
                            )
                    )
                    HeaderIconButton(
                        onClick = { searching = !searching },
                        icon = R.drawable.search_circle,
                        color = colorPalette.text,
                        iconSize = 24.dp,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                    )
                    HeaderIconButton(
                        onClick = {},
                        icon = if (showHiddenSongs == 0) R.drawable.eye_off else R.drawable.eye,
                        color = colorPalette.text,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .combinedClickable(
                                onClick = { showHiddenSongs = if (showHiddenSongs == 0) -1 else 0 },
                                onLongClick = {
                                    SmartToast(context.getString(R.string.info_show_hide_hidden_songs))
                                }
                            )
                    )

                    HeaderIconButton(
                        icon = R.drawable.shuffle,
                        enabled = items.isNotEmpty(),
                        color = if (items.isNotEmpty()) colorPalette.text else colorPalette.textDisabled,
                        onClick = {},
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .combinedClickable(
                                onClick = {
                                    if (items.isNotEmpty()) {
                                        val itemsLimited =
                                            if (items.size > maxSongsInQueue.number) items
                                                .shuffled()
                                                .take(maxSongsInQueue.number.toInt()) else items
                                        binder?.stopRadio()
                                        binder?.player?.forcePlayFromBeginning(
                                            itemsLimited
                                                .shuffled()
                                                .map(Song::asMediaItem)
                                        )
                                    }
                                },
                                onLongClick = {
                                    SmartToast(context.getString(R.string.info_shuffle))
                                }
                            )
                    )

                    HeaderIconButton(
                        onClick = {  },
                        icon = R.drawable.devices,
                        color = if (includeLocalSongs) colorPalette.text else colorPalette.textDisabled,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .combinedClickable(
                                onClick = {
                                    includeLocalSongs = !includeLocalSongs
                                },
                                onLongClick = {
                                    SmartToast(context.getString(R.string.info_includes_excludes_songs_on_the_device))
                                }
                            )
                    )

                    HeaderIconButton(
                        icon = R.drawable.ellipsis_horizontal,
                        color = colorPalette.text,
                        onClick = {},
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .combinedClickable(
                                onClick = {
                                    menuState.display {
                                        PlaylistsItemMenu(
                                            navController = navController,
                                            modifier = Modifier.fillMaxHeight(0.4f),
                                            onDismiss = menuState::hide,
                                            onSelectUnselect = {
                                                selectItems = !selectItems
                                                if (!selectItems) {
                                                    listMediaItems.clear()
                                                }
                                            },
                                            onPlayNext = {
                                                if (listMediaItems.isEmpty()) {
                                                    binder?.player?.addNext(items.map(Song::asMediaItem))
                                                } else {
                                                    binder?.player?.addNext(listMediaItems)
                                                    listMediaItems.clear()
                                                    selectItems = false
                                                }
                                            },
                                            onEnqueue = {
                                                if (listMediaItems.isEmpty()) {
                                                    binder?.player?.enqueue(items.map(Song::asMediaItem))
                                                } else {
                                                    binder?.player?.enqueue(listMediaItems)
                                                    listMediaItems.clear()
                                                    selectItems = false
                                                }
                                            },
                                            onAddToPlaylist = { playlistPreview ->
                                                position =
                                                    playlistPreview.songCount.minus(1) ?: 0
                                                if (position > 0) position++ else position = 0

                                                items.forEachIndexed { index, song ->
                                                    runCatching {
                                                        Database.insert(song.asMediaItem)
                                                        Database.insert(
                                                            SongPlaylistMap(
                                                                songId = song.asMediaItem.mediaId,
                                                                playlistId = playlistPreview.playlist.id,
                                                                position = position + index
                                                            )
                                                        )
                                                    }.onFailure {
                                                        SmartToast(context.resources.getString(R.string.error))
                                                    }
                                                }
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    SmartToast(
                                                        context.resources.getString(R.string.done),
                                                        type = PopupType.Success
                                                    )
                                                }
                                            },
                                            onExport = {
                                                isExporting = true
                                            }
                                        )
                                    }
                                },
                                onLongClick = {
                                    SmartToast(context.getString(R.string.info_add_in_playlist))
                                }
                            )
                    )

                    /*
                    Spacer(
                        modifier = Modifier
                            .weight(0.3f)
                    )

                    BasicText(
                        text = when (sortBy) {
                            SongSortBy.Title -> stringResource(R.string.sort_title)
                            SongSortBy.DatePlayed -> stringResource(R.string.sort_date_played)
                            SongSortBy.PlayTime -> stringResource(R.string.sort_listening_time)
                            SongSortBy.DateAdded -> stringResource(R.string.sort_date_added)
                            SongSortBy.DateLiked -> stringResource(R.string.sort_date_liked)
                            SongSortBy.Artist -> stringResource(R.string.sort_artist)
                            SongSortBy.Duration -> stringResource(R.string.sort_duration)
                        },
                        style = typography.xs.semiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable {
                                menuState.display{
                                    SortMenu(
                                        title = stringResource(R.string.sorting_order),
                                        onDismiss = menuState::hide,
                                        onDatePlayed = { sortBy = SongSortBy.DatePlayed },
                                        onTitle = { sortBy = SongSortBy.Title },
                                        onDateAdded = { sortBy = SongSortBy.DateAdded },
                                        onPlayTime = { sortBy = SongSortBy.PlayTime },
                                        onDateLiked = { sortBy = SongSortBy.DateLiked },
                                        onArtist = { sortBy = SongSortBy.Artist },
                                        onDuration = { sortBy = SongSortBy.Duration }
                                    )
                                }

                            }
                    )
                    */



                }

                /*        */

                    AnimatedVisibility(visible = searching) {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                //.requiredHeight(30.dp)
                                .padding(all = 10.dp)
                                .fillMaxWidth()
                        ) {
                        val focusRequester = remember { FocusRequester() }
                        val focusManager = LocalFocusManager.current
                        val keyboardController = LocalSoftwareKeyboardController.current

                        LaunchedEffect(searching) {
                            focusRequester.requestFocus()
                        }

                        BasicTextField(
                            value = filter ?: "",
                            onValueChange = { filter = it },
                            textStyle = typography.xs.semiBold,
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (filter.isNullOrBlank()) filter = ""
                                focusManager.clearFocus()
                            }),
                            cursorBrush = SolidColor(colorPalette.text),
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
                                        color = colorPalette.favoritesIcon,
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
                                        visible = filter?.isEmpty() ?: true,
                                        enter = fadeIn(tween(100)),
                                        exit = fadeOut(tween(100)),
                                    ) {
                                        BasicText(
                                            text = stringResource(R.string.search),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = typography.xs.semiBold.secondary.copy(color = colorPalette.textDisabled)
                                        )
                                    }

                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth()
                                .background(
                                    colorPalette.background4,
                                    shape = thumbnailRoundness.shape()
                                )
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    if (!it.hasFocus) {
                                        keyboardController?.hide()
                                        if (filter?.isBlank() == true) {
                                            filter = null
                                            searching = false
                                        }
                                    }
                                }
                        )
                    }

                }
                /*        */
            }

            item(
                key = "filterX"
            ) {
                ButtonsRow(
                    chips = listOf(
                        BuiltInPlaylist.All to stringResource(R.string.all),
                        BuiltInPlaylist.Favorites to stringResource(R.string.favorites),
                        BuiltInPlaylist.Offline to stringResource(R.string.cached),
                        BuiltInPlaylist.Downloaded to stringResource(R.string.downloaded),
                        BuiltInPlaylist.Top to String.format(stringResource(R.string.my_playlist_top),maxTopPlaylistItems.number),
                        BuiltInPlaylist.OnDevice to stringResource(R.string.on_device),
                    ),
                    currentValue = builtInPlaylist,
                    onValueUpdate = { builtInPlaylist = it },
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            itemsIndexed(
                items = items,
                key = { _, song -> song.id },
                contentType = { _, song -> song },
            ) { index, song ->

                var isHiding by remember {
                    mutableStateOf(false)
                }

                if (isHiding) {
                    ConfirmationDialog(
                        text = stringResource(R.string.hidesong),
                        onDismiss = { isHiding = false },
                        onConfirm = {
                            query {
                                menuState.hide()
                                binder?.cache?.removeResource(song.id)
                                binder?.downloadCache?.removeResource(song.id)
                                Database.incrementTotalPlayTimeMs(song.id, -song.totalPlayTimeMs)
                            }
                        }
                    )
                }

                val isLocal by remember { derivedStateOf { song.asMediaItem.isLocal } }
                downloadState = getDownloadState(song.asMediaItem.mediaId)
                val isDownloaded = if (!isLocal) downloadedStateMedia(song.asMediaItem.mediaId) else true
                val checkedState = remember { mutableStateOf(false) }

                SongItem(
                    song = song,
                    isDownloaded =  isDownloaded,
                    onDownloadClick = {
                        binder?.cache?.removeResource(song.asMediaItem.mediaId)
                        query {
                            Database.insert(
                                Song(
                                    id = song.asMediaItem.mediaId,
                                    title = song.asMediaItem.mediaMetadata.title.toString(),
                                    artistsText = song.asMediaItem.mediaMetadata.artist.toString(),
                                    thumbnailUrl = song.thumbnailUrl,
                                    durationText = null
                                )
                            )
                        }
                        if (!isLocal)
                        manageDownload(
                            context = context,
                            songId = song.id,
                            songTitle = song.title,
                            downloadState = isDownloaded
                        )
                    },
                    downloadState = downloadState,
                    thumbnailSizePx = thumbnailSizePx,
                    thumbnailSizeDp = thumbnailSizeDp,
                    onThumbnailContent = {
                        if (sortBy == SongSortBy.PlayTime) {
                            BasicText(
                                text = song.formattedTotalPlayTime,
                                style = typography.xxs.semiBold.center.color(colorPalette.onOverlay),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, colorPalette.overlay)
                                        ),
                                        shape = thumbnailShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .align(Alignment.BottomCenter)
                            )
                        }
                        if (builtInPlaylist == BuiltInPlaylist.Top)
                            BasicText(
                                text = (index + 1).toString(),
                                style = typography.m.semiBold.center.color(colorPalette.onOverlay),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                colorPalette.overlay
                                            )
                                        ),
                                        shape = thumbnailShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .align(Alignment.Center)
                            )
                    },
                    trailingContent = {
                        if (selectItems)
                            Checkbox(
                                checked = checkedState.value,
                                onCheckedChange = {
                                    checkedState.value = it
                                    if (it) listMediaItems.add(song.asMediaItem) else
                                        listMediaItems.remove(song.asMediaItem)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colorPalette.accent,
                                    uncheckedColor = colorPalette.text
                                ),
                                modifier = Modifier
                                    .scale(0.7f)
                            )
                        else checkedState.value = false
                    },
                    modifier = Modifier
                        .combinedClickable(
                            onLongClick = {
                                menuState.display {
                                    InHistoryMediaItemMenu(
                                        navController = navController,
                                        song = song,
                                        onDismiss = menuState::hide,
                                        onHideFromDatabase = { isHiding = true }
                                    )
                                }
                            },
                            onClick = {
                                searching = false
                                filter = null
                                val itemsLimited =
                                    if (items.size > maxSongsInQueue.number) items.take(
                                        maxSongsInQueue.number.toInt()
                                    ) else items
                                binder?.stopRadio()
                                binder?.player?.forcePlayAtIndex(
                                    itemsLimited.map(Song::asMediaItem),
                                    index
                                )
                            }
                        )
                        .animateItemPlacement()
                )
            }
        }

        FloatingActionsContainerWithScrollToTop(lazyListState = lazyListState)

        val showFloatingIcon by rememberPreference(showFloatingIconKey, false)
        if(uiType == UiType.ViMusic || showFloatingIcon)
            MultiFloatingActionsContainer(
                iconId = R.drawable.search,
                onClick = onSearchClick,
                onClickSettings = onSettingsClick,
                onClickSearch = onSearchClick
            )

            /*
        FloatingActionsContainerWithScrollToTop(
                lazyListState = lazyListState,
                iconId = R.drawable.search,
                onClick = onSearchClick
            )

             */





    }
}
