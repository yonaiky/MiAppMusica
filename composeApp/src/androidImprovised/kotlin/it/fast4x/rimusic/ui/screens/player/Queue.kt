package it.fast4x.rimusic.ui.screens.player


import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.navigation.NavController
import com.valentinilk.shimmer.shimmer
import it.fast4x.compose.persist.persist
import it.fast4x.compose.persist.persistList
import it.fast4x.compose.reordering.draggedItem
import it.fast4x.compose.reordering.rememberReorderingState
import it.fast4x.compose.reordering.reorder
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.enums.QueueLoopType
import it.fast4x.rimusic.enums.QueueType
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.service.isLocal
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.SwipeableQueueItem
import it.fast4x.rimusic.ui.components.navigation.header.TabToolBar
import it.fast4x.rimusic.ui.components.tab.toolbar.Button
import it.fast4x.rimusic.ui.components.tab.toolbar.Dialog
import it.fast4x.rimusic.ui.components.themed.FloatingActionsContainerWithScrollToTop
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.components.themed.IconInfo
import it.fast4x.rimusic.ui.components.themed.PlaylistsMenu
import it.fast4x.rimusic.ui.components.themed.QueuedMediaItemMenu
import it.fast4x.rimusic.ui.components.themed.Search
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.items.SongItemPlaceholder
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.DisposableListener
import it.fast4x.rimusic.utils.PositionLock
import it.fast4x.rimusic.utils.addNext
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.asSong
import it.fast4x.rimusic.utils.disableScrollingTextKey
import it.fast4x.rimusic.utils.enqueue
import it.fast4x.rimusic.utils.isDownloadedSong
import it.fast4x.rimusic.utils.isLandscape
import it.fast4x.rimusic.utils.isNowPlaying
import it.fast4x.rimusic.utils.manageDownload
import it.fast4x.rimusic.utils.queueTypeKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.shouldBePlaying
import it.fast4x.rimusic.utils.showButtonPlayerDiscoverKey
import it.fast4x.rimusic.utils.windows
import me.knighthat.component.SongItem
import me.knighthat.component.tab.ExportSongsToCSVDialog
import me.knighthat.component.tab.ItemSelector
import me.knighthat.component.ui.screens.player.DeleteFromQueue
import me.knighthat.component.ui.screens.player.Discover
import me.knighthat.component.ui.screens.player.QueueArrow
import me.knighthat.component.ui.screens.player.Repeat
import me.knighthat.component.ui.screens.player.ShuffleQueue
import timber.log.Timber


@ExperimentalTextApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@androidx.media3.common.util.UnstableApi
@Composable
fun Queue(
    navController: NavController,
    onDismiss: (QueueLoopType) -> Unit,
    onDiscoverClick: (Boolean) -> Unit,
) {
    // Essentials
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val windowInsets = WindowInsets.systemBars
    val menuState = LocalMenuState.current
    val binder = LocalPlayerServiceBinder.current
    val player = binder?.player ?: return

    // Settings
    val disableScrollingText by rememberPreference(disableScrollingTextKey, false)

    val bgColor = when( rememberPreference(queueTypeKey, QueueType.Essential).value ) {
        QueueType.Essential -> colorPalette().background0
        QueueType.Modern    -> Color.Transparent
    }

    val rippleIndication = ripple(bounded = false)

    Box( Modifier.fillMaxSize() ) {
        var items by persist(
            tag = "queue/songs",
            player.currentTimeline.windows.map { it.mediaItem.asSong }
        )
        player.DisposableListener {
            object : Player.Listener {
                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    items = timeline.windows.map { it.mediaItem.asSong }
                }
            }
        }
        var itemsOnDisplay by persistList<Song>( "queue/on_display" )

        val lazyListState = rememberLazyListState()
        val reorderingState = rememberReorderingState(
            lazyListState = lazyListState,
            key = items,
            onDragEnd = player::moveMediaItem,
            extraItemCount = 0
        )

        val positionLock = PositionLock.init( SortOrder.Ascending )

        val itemSelector = ItemSelector<Song>()
        LaunchedEffect( itemSelector.isActive ) {
            // Setting this field to true means disable it
            if( itemSelector.isActive )
                positionLock.isFirstIcon = true
        }

        fun getSongs() = itemSelector.ifEmpty { items }

        val search = Search.init()
        LaunchedEffect( items, search.input ) {
            items.filter {
                    // Without cleaning, user can search explicit songs with "e:"
                    // I kinda want this to be a feature, but it seems unnecessary
                    val containsTitle = it.cleanTitle().contains( search.input, true )
                    val containsArtist = it.artistsText?.contains( search.input, true ) ?: false

                    containsTitle || containsArtist
                }
                .let {
                    itemsOnDisplay = it

                    // Keep scroll at top to prevent weird artifact
                    lazyListState.scrollToItem( 0, 0 )
                }
        }

        val plistName = remember { mutableStateOf("") }
        val exportDialog = ExportSongsToCSVDialog(
            playlistId = -1,        // Doesn't belong to any playlist
            playlistName = plistName.value,
            songs = ::getSongs
        )
        val shuffle = ShuffleQueue( player, reorderingState )
        val discover = Discover( onDiscoverClick )
        val repeat = Repeat.init()
        val deleteDialog = DeleteFromQueue {
            if( itemSelector.isEmpty() ) {
                player.stop()
                player.clearMediaItems()
                player.release()
            } else
                itemSelector.map( items::indexOf )
                            .sorted()
                            // Goes backward to prevent item from being skipped
                            // due to the previous element is removed and the indices
                            // are updated.
                            .reversed()
                            .forEach( player::removeMediaItem )

            itemSelector.isActive = false

            onDismiss()
        }
        val addToPlaylist = PlaylistsMenu.init(
            navController = navController,
            mediaItems = { getSongs().map( Song::asMediaItem ) },
            onFailure = { throwable, preview ->
                Timber.e( "Failed to add songs to playlist ${preview.playlist.name} on HomeSongs" )
                throwable.printStackTrace()
            },
            finalAction = {
                // Turn of selector clears the selected list
                itemSelector.isActive = false
            }
        )
        val queueArrow = QueueArrow { onDismiss( repeat.type ) }

        // Dialog renders
        exportDialog.Render()
        (deleteDialog as Dialog).Render()

        Column {
            Box( Modifier.background( bgColor ).weight( 1f ) ) {
                LazyColumn(
                    state = reorderingState.lazyListState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = windowInsets
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .add( WindowInsets(bottom = Dimensions.bottomSpacer) )
                        .asPaddingValues()
                ) {
                    itemsIndexed(
                        items = itemsOnDisplay,
                        key = { _, song -> song.id }
                    ) { index, song ->

                        val isLocal by remember { derivedStateOf { song.isLocal } }
                        val isDownloaded = isLocal || isDownloadedSong(song.id)
                        var forceRecompose by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier.fillMaxWidth()
                                               .draggedItem(
                                                   reorderingState = reorderingState,
                                                   index = index
                                               )
                        ) {
                            // Drag anchor
                            if ( !positionLock.isLocked() ) {
                                Box(
                                    modifier = Modifier.padding( end = 16.dp ) // Accommodate horizontal padding of SongItem
                                                       .size( 24.dp )
                                                       .zIndex( 2f )
                                                       .align( Alignment.CenterEnd ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    IconButton(
                                        icon = R.drawable.reorder,
                                        color = colorPalette().textDisabled,
                                        indication = rippleIndication,
                                        onClick = {},
                                        modifier = Modifier.reorder(
                                            reorderingState = reorderingState,
                                            index = index
                                        )
                                    )
                                }
                            }

                            val mediaItem = song.asMediaItem
                            SwipeableQueueItem(
                                mediaItem = mediaItem,
                                onPlayNext = {
                                    binder.player.addNext(
                                        mediaItem,
                                        context
                                    )
                                },
                                onDownload = {
                                    binder.cache.removeResource(song.id)
                                    if (!isLocal)
                                        manageDownload(
                                            context = context,
                                            mediaItem = mediaItem,
                                            downloadState = isDownloaded
                                        )
                                },
                                onRemoveFromQueue = {
                                    player.removeMediaItem(index)
                                    SmartMessage("${context.resources.getString(R.string.deleted)} ${song.title}", type = PopupType.Warning, context = context)
                                },
                                onEnqueue = {
                                    binder.player.enqueue(
                                        mediaItem,
                                        context
                                    )
                                }
                            ) {
                                SongItem(
                                    song = song,
                                    navController = navController,
                                    modifier = Modifier
                                        .combinedClickable(
                                            onLongClick = {
                                                menuState.display {
                                                    QueuedMediaItemMenu(
                                                        navController = navController,
                                                        mediaItem = mediaItem,
                                                        indexInQueue = if( player.isNowPlaying(song.id) ) null else index,
                                                        onDismiss = {
                                                            menuState.hide()
                                                            forceRecompose = true
                                                        },
                                                        onDownload = {
                                                            manageDownload(
                                                                context = context,
                                                                mediaItem = mediaItem,
                                                                downloadState = isDownloaded
                                                            )
                                                        },
                                                        disableScrollingText = disableScrollingText
                                                    )
                                                }
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            },
                                            onClick = {
                                                if( player.isNowPlaying(song.id) ) {
                                                    if(player.shouldBePlaying)
                                                        player.pause()
                                                    else
                                                        player.play()
                                                } else {
                                                    player.seekToDefaultPosition(index)
                                                    player.prepare()
                                                    player.playWhenReady = true
                                                }

                                                /*
                                                    Due to the small size of checkboxes,
                                                    we shouldn't disable [itemSelector]
                                                 */

                                                search.onItemSelected()
                                            }
                                        )
                                        .background( bgColor ),
                                    trailingContent = {
                                        // It must watch for [selectedItems.size] for changes
                                        // Otherwise, state will stay the same
                                        val checkedState = remember( itemSelector.size ) {
                                            mutableStateOf( song in itemSelector )
                                        }

                                        if( itemSelector.isActive || !positionLock.isLocked() )
                                        // Create a fake box to store drag anchor and checkbox
                                            Box( Modifier.width( 24.dp ) ) {

                                                if( itemSelector.isActive )
                                                    Checkbox(
                                                        checked = checkedState.value,
                                                        onCheckedChange = {
                                                            checkedState.value = it
                                                            if ( it )
                                                                itemSelector.add( song )
                                                            else
                                                                itemSelector.remove( song )
                                                        },
                                                        colors = CheckboxDefaults.colors(
                                                            checkedColor = colorPalette().accent,
                                                            uncheckedColor = colorPalette().text
                                                        ),
                                                        modifier = Modifier.scale( .7f )
                                                                           .size( 24.dp )
                                                                           .padding( all = 0.dp )
                                                    )
                                            }
                                        else if( !itemSelector.isActive )
                                            checkedState.value = false
                                    }
                                )
                            }
                        }
                    }

                    if( binder.isLoadingRadio )
                        item {
                            Column( Modifier.shimmer() ) {
                                repeat(3) { index ->
                                    SongItemPlaceholder( Modifier.alpha( 1f - index * 0.125f ) )
                                }
                            }
                        }
                }
            }

            // Search box
            Box(
                modifier = Modifier.fillMaxWidth()
                                   .background( colorPalette().background1 ),
            ) { search.SearchBar( this@Column ) }

            Box(
                modifier = Modifier.fillMaxWidth()
                                   .clickable { onDismiss( repeat.type ) }
                                   .background (colorPalette().background1 )
                                   .height( 60.dp ) //bottom bar queue
            ) {
                if( !isLandscape ) {
                    // Move mini player up as search bar appears
                    val yOffset = if( search.isVisible ) -125 else -65

                    Box(
                        Modifier.absoluteOffset( 0.dp, yOffset.dp )
                                .align( Alignment.TopCenter )
                    ) { MiniPlayer( {}, {} ) }
                }

                if ( !queueArrow.isEnabled )
                    Image(
                        painter = painterResource( R.drawable.horizontal_bold_line_rounded ),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette().text),
                        modifier = Modifier.absoluteOffset( 0.dp, (-10).dp )
                                           .align( Alignment.TopCenter )
                                           .size( 30.dp )
                    )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding( horizontal = 8.dp )
                                       .fillMaxWidth()
                ) {
                    // Number of songs
                    IconInfo(
                        title = player.mediaItemCount.toString(),
                        icon = painterResource( R.drawable.musical_notes ),
                        modifier = Modifier.width( 40.dp )
                    )

                    TabToolBar.Buttons(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.weight( 1f ),
                        buttons = mutableListOf<Button>().apply {
                            add( search )
                            if( rememberPreference( showButtonPlayerDiscoverKey, false ).value )
                                add( discover )
                            add( positionLock )
                            add( repeat )
                            add( shuffle )
                            add( itemSelector )
                            add( deleteDialog )
                            add( addToPlaylist )
                            add( exportDialog )
                        }
                    )

                    if( queueArrow.isEnabled )
                        queueArrow.ToolBarButton()
                }
            }
        }

        FloatingActionsContainerWithScrollToTop(
            lazyListState = reorderingState.lazyListState,
            modifier = Modifier.padding(bottom = Dimensions.miniPlayerHeight)
        )
    }
}
