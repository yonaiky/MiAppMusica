package it.fast4x.rimusic.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import it.fast4x.innertube.Innertube
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.AlbumSwipeAction
import it.fast4x.rimusic.enums.DownloadedStateMedia
import it.fast4x.rimusic.enums.PlaylistSwipeAction
import it.fast4x.rimusic.enums.QueueSwipeAction
import it.fast4x.rimusic.service.isLocal
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.utils.albumItemToggleBookmarked
import it.fast4x.rimusic.utils.albumSwipeLeftActionKey
import it.fast4x.rimusic.utils.albumSwipeRightActionKey
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.downloadedStateMedia
import it.fast4x.rimusic.utils.getDownloadState
import it.fast4x.rimusic.utils.isSwipeToActionEnabledKey
import it.fast4x.rimusic.utils.mediaItemToggleLike
import it.fast4x.rimusic.utils.playlistSwipeLeftActionKey
import it.fast4x.rimusic.utils.playlistSwipeRightActionKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.queueSwipeLeftActionKey
import it.fast4x.rimusic.utils.queueSwipeRightActionKey
import kotlinx.coroutines.flow.distinctUntilChanged
import me.knighthat.colorPalette
import timber.log.Timber

@Composable
fun SwipeableContent(
    swipeToLeftIcon: Int? = null,
    swipeToRightIcon: Int? = null,
    onSwipeToLeft: () -> Unit,
    onSwipeToRight: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance: Float -> distance * 0.25f },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) {onSwipeToRight();hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)}
            else if (value == SwipeToDismissBoxValue.EndToStart) {onSwipeToLeft();hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)}

            return@rememberSwipeToDismissBoxState false
        }
    )
    val isSwipeToActionEnabled by rememberPreference(isSwipeToActionEnabledKey, true)

    val current = LocalViewConfiguration.current
    CompositionLocalProvider(LocalViewConfiguration provides object : ViewConfiguration by current{
        override val touchSlop: Float
            get() = current.touchSlop * 5f
    }) {
        SwipeToDismissBox(
            gesturesEnabled = isSwipeToActionEnabled,
            modifier = modifier,
            //.padding(horizontal = 16.dp)
            //.clip(RoundedCornerShape(12.dp)),
            state = dismissState,
            backgroundContent = {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        //.background(colorPalette.background1)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> Arrangement.Start
                        SwipeToDismissBoxValue.EndToStart -> Arrangement.End
                        SwipeToDismissBoxValue.Settled -> Arrangement.Center
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> if (swipeToRightIcon == null) null else ImageVector.vectorResource(
                            swipeToRightIcon
                        )

                        SwipeToDismissBoxValue.EndToStart -> if (swipeToLeftIcon == null) null else ImageVector.vectorResource(
                            swipeToLeftIcon
                        )

                        SwipeToDismissBoxValue.Settled -> null
                    }
                    if (icon != null)
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = colorPalette().accent,
                        )
                }
            }
        ) {
            content()
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun SwipeableQueueItem(
    mediaItem: MediaItem,
    onPlayNext: (() -> Unit) = {},
    onDownload: (() -> Unit) = {},
    onRemoveFromQueue: (() -> Unit) = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val queueSwipeLeftAction by rememberPreference(queueSwipeLeftActionKey, QueueSwipeAction.RemoveFromQueue)
    val queueSwipeRightAction by rememberPreference(queueSwipeRightActionKey, QueueSwipeAction.PlayNext)

    fun getActionCallback(actionName: QueueSwipeAction): () -> Unit {
        return when (actionName) {
            QueueSwipeAction.PlayNext -> onPlayNext
            QueueSwipeAction.Download -> onDownload
            QueueSwipeAction.RemoveFromQueue -> onRemoveFromQueue
            else -> ({})
        }
    }
    val swipeLeftCallback = getActionCallback(queueSwipeLeftAction)
    val swipeRighCallback = getActionCallback(queueSwipeRightAction)

    var likedAt by rememberSaveable {
        mutableStateOf<Long?>(null)
    }
    val downloadState = getDownloadState(mediaItem.mediaId)
    var downloadedStateMedia by remember { mutableStateOf(DownloadedStateMedia.NOT_CACHED_OR_DOWNLOADED) }
    downloadedStateMedia = if (!mediaItem.isLocal) downloadedStateMedia(mediaItem.mediaId)
    else DownloadedStateMedia.DOWNLOADED

    SwipeableContent(
        swipeToLeftIcon = queueSwipeLeftAction.getStateIcon(
            queueSwipeLeftAction,
            likedAt,
            downloadState,
            downloadedStateMedia
        ),
        swipeToRightIcon = queueSwipeRightAction.getStateIcon(
            queueSwipeRightAction,
            likedAt,
            downloadState,
            downloadedStateMedia
        ),
        onSwipeToLeft = swipeLeftCallback,
        onSwipeToRight = swipeRighCallback,
        modifier = modifier
    ) {
        content()
    }

}

@OptIn(UnstableApi::class)
@Composable
fun SwipeablePlaylistItem(
    mediaItem: MediaItem,
    onPlayNext: (() -> Unit) = {},
    onDownload: (() -> Unit) = {},
    onEnqueue: (() -> Unit) = {},
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var likedAt by rememberSaveable {
        mutableStateOf<Long?>(null)
    }
    val downloadState = getDownloadState(mediaItem.mediaId)
    var downloadedStateMedia by remember { mutableStateOf(DownloadedStateMedia.NOT_CACHED_OR_DOWNLOADED) }
    downloadedStateMedia = if (!mediaItem.isLocal) downloadedStateMedia(mediaItem.mediaId)
    else DownloadedStateMedia.DOWNLOADED

    LaunchedEffect(mediaItem.mediaId) {
        Database.likedAt(mediaItem.mediaId).distinctUntilChanged().collect { likedAt = it }
    }
    val onFavourite: () -> Unit = {
        mediaItemToggleLike(mediaItem)
        val message: String
        if( likedAt != null ) {
            val mTitle: String = mediaItem.mediaMetadata.title?.toString() ?: ""
            val mArtist: String = mediaItem.mediaMetadata.artist?.toString() ?: ""

            message = "\"$mTitle - $mArtist\" ${context.resources.getString(R.string.removed_from_favorites)}"
        } else
            message = context.resources.getString(R.string.added_to_favorites)

        SmartMessage(
            message,
            durationLong = likedAt != null,
            context = context
        )
    }

    val playlistSwipeLeftAction by rememberPreference(playlistSwipeLeftActionKey, PlaylistSwipeAction.Favourite)
    val playlistSwipeRightAction by rememberPreference(playlistSwipeRightActionKey, PlaylistSwipeAction.PlayNext)

    fun getActionCallback(actionName: PlaylistSwipeAction): () -> Unit {
        return when (actionName) {
            PlaylistSwipeAction.PlayNext -> onPlayNext
            PlaylistSwipeAction.Download -> onDownload
            PlaylistSwipeAction.Favourite -> onFavourite
            PlaylistSwipeAction.Enqueue -> onEnqueue
            else -> ({})
        }
    }
    val swipeLeftCallback = getActionCallback(playlistSwipeLeftAction)
    val swipeRighCallback = getActionCallback(playlistSwipeRightAction)

    Timber.d("SwipeablePlaylistItem mediaItem: ${mediaItem.mediaId}, downloadState: $downloadState")
    SwipeableContent(
        swipeToLeftIcon =  playlistSwipeLeftAction.getStateIcon(
            playlistSwipeLeftAction,
            likedAt,
            downloadState,
            downloadedStateMedia
        ), // if (likedAt == null) R.drawable.heart_outline else R.drawable.heart,
        swipeToRightIcon =  playlistSwipeRightAction.getStateIcon(
            playlistSwipeRightAction,
            likedAt,
            downloadState,
            downloadedStateMedia
        ), // R.drawable.play_skip_forward,
        onSwipeToLeft = swipeLeftCallback, // { updateLike = true },
        onSwipeToRight = swipeRighCallback // onSwipeToRight
    ) {
        content()
    }

}

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun SwipeableAlbumItem(
    albumItem: Innertube.AlbumItem,
    onSwipeToLeft: () -> Unit,
    onSwipeToRight: () -> Unit,
    content: @Composable () -> Unit
) {
    val albumSwipeLeftAction by rememberPreference(albumSwipeLeftActionKey, AlbumSwipeAction.PlayNext)
    val albumSwipeRightAction by rememberPreference(albumSwipeRightActionKey, AlbumSwipeAction.Bookmark)

    val context = LocalContext.current
    var bookmarkedAt by rememberSaveable {
        mutableStateOf<Long?>(null)
    }
    LaunchedEffect(albumItem.key) {
        Database.albumBookmarkedAt(albumItem.key).distinctUntilChanged().collect { bookmarkedAt = it }
    }
    var updateLike by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(updateLike) {
        if (updateLike) {
            albumItemToggleBookmarked(albumItem)
            updateLike = false
            if (bookmarkedAt == null)
                SmartMessage(context.resources.getString(R.string.added_to_favorites), context = context)
            else
                SmartMessage("\"" + albumItem.info?.name + " - " + albumItem.authors?.joinToString("") { it.name + "\" " + context.resources.getString(R.string.removed_from_favorites)}, context = context, durationLong = true)
        }
    }

    SwipeableContent(
        swipeToLeftIcon =  albumSwipeLeftAction.icon, // R.drawable.play_skip_forward,
        swipeToRightIcon =  albumSwipeRightAction.icon, // if (bookmarkedAt == null) R.drawable.bookmark_outline else R.drawable.bookmark,
        onSwipeToLeft = onSwipeToLeft,
        onSwipeToRight = onSwipeToRight
    ) {
        content()
    }

}