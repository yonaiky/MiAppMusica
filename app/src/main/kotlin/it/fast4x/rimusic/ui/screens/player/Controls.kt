package it.fast4x.rimusic.ui.screens.player

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.ColorPaletteName
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.PauseBetweenSongs
import it.fast4x.rimusic.enums.PlayerControlsType
import it.fast4x.rimusic.enums.PlayerPlayButtonType
import it.fast4x.rimusic.enums.PlayerThumbnailSize
import it.fast4x.rimusic.enums.PlayerTimelineType
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.models.Info
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.ui.UiMedia
import it.fast4x.rimusic.query
import it.fast4x.rimusic.ui.components.SeekBar
import it.fast4x.rimusic.ui.components.SeekBarCustom
import it.fast4x.rimusic.ui.components.SeekBarWaved
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.components.themed.PlaybackParamsDialog
import it.fast4x.rimusic.ui.components.themed.SelectorDialog
import it.fast4x.rimusic.ui.screens.player.components.controls.ControlsEssential
import it.fast4x.rimusic.ui.screens.player.components.controls.ControlsModern
import it.fast4x.rimusic.ui.screens.player.components.controls.InfoAlbumAndArtistEssential
import it.fast4x.rimusic.ui.screens.player.components.controls.InfoAlbumAndArtistModern
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.collapsedPlayerProgressBar
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.UiTypeKey
import it.fast4x.rimusic.utils.bold
import it.fast4x.rimusic.utils.colorPaletteNameKey
import it.fast4x.rimusic.utils.disableScrollingTextKey
import it.fast4x.rimusic.utils.downloadedStateMedia
import it.fast4x.rimusic.utils.effectRotationKey
import it.fast4x.rimusic.utils.forceSeekToNext
import it.fast4x.rimusic.utils.formatAsDuration
import it.fast4x.rimusic.utils.isCompositionLaunched
import it.fast4x.rimusic.utils.isGradientBackgroundEnabledKey
import it.fast4x.rimusic.utils.pauseBetweenSongsKey
import it.fast4x.rimusic.utils.playbackSpeedKey
import it.fast4x.rimusic.utils.playerControlsTypeKey
import it.fast4x.rimusic.utils.playerPlayButtonTypeKey
import it.fast4x.rimusic.utils.playerThumbnailSizeKey
import it.fast4x.rimusic.utils.playerTimelineTypeKey
import it.fast4x.rimusic.utils.positionAndDurationState
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.utils.showRemainingSongTimeKey
import it.fast4x.rimusic.utils.trackLoopEnabledKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalTextApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@UnstableApi
@Composable
fun Controls(
    navController: NavController,
    onCollapse: () -> Unit,
    layoutState: PlayerSheetState,
    media: UiMedia,
    mediaId: String,
    title: String?,
    artist: String?,
    artistIds: List<Info>?,
    albumId: String?,
    shouldBePlaying: Boolean,
    position: Long,
    duration: Long,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current


    val binder = LocalPlayerServiceBinder.current
    binder?.player ?: return



    var scrubbingPosition by remember(mediaId) {
        mutableStateOf<Long?>(null)
    }

    //val onGoToArtist = artistRoute::global
    //val onGoToAlbum = albumRoute::global


    var likedAt by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    /*
    var nextmediaItemIndex = binder.player.nextMediaItemIndex ?: -1
    var nextmediaItemtitle = ""


    if (nextmediaItemIndex.toShort() > -1)
        nextmediaItemtitle = binder.player.getMediaItemAt(nextmediaItemIndex).mediaMetadata.title.toString()
    */

    var isRotated by rememberSaveable { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 360F else 0f,
        animationSpec = tween(durationMillis = 200), label = ""
    )
    var effectRotationEnabled by rememberPreference(effectRotationKey, true)
    var disableScrollingText by rememberPreference(disableScrollingTextKey, false)
    var playerTimelineType by rememberPreference(playerTimelineTypeKey, PlayerTimelineType.Default)
    var playerPlayButtonType by rememberPreference(
        playerPlayButtonTypeKey,
        PlayerPlayButtonType.Rectangular
    )

    val scope = rememberCoroutineScope()
    val animatedPosition = remember { Animatable(position.toFloat()) }
    var isSeeking by remember { mutableStateOf(false) }


    val compositionLaunched = isCompositionLaunched()
    LaunchedEffect(mediaId) {
        if (compositionLaunched) animatedPosition.animateTo(0f)
    }
    LaunchedEffect(position) {
        if (!isSeeking && !animatedPosition.isRunning)
            animatedPosition.animateTo(
                position.toFloat(), tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                )
            )
    }
    //val durationVisible by remember(isSeeking) { derivedStateOf { isSeeking } }


    LaunchedEffect(mediaId) {
        Database.likedAt(mediaId).distinctUntilChanged().collect { likedAt = it }
    }

    var isDownloaded by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    isDownloaded = downloadedStateMedia(mediaId)

    //val menuState = LocalMenuState.current





    var showSelectDialog by remember { mutableStateOf(false) }

    val playerThumbnailSize by rememberPreference(
        playerThumbnailSizeKey,
        PlayerThumbnailSize.Medium
    )
    val isGradientBackgroundEnabled by rememberPreference(isGradientBackgroundEnabledKey, false)

    /*
    var windows by remember {
        mutableStateOf(binder.player.currentTimeline.windows)
    }
    var queuedSongs by remember {
        mutableStateOf<List<Song>>(emptyList())
    }
    LaunchedEffect(mediaId, windows) {
        Database.getSongsList(
            windows.map {
                it.mediaItem.mediaId
            }
        ).collect{ queuedSongs = it}
    }

    var totalPlayTimes = 0L
    queuedSongs.forEach {
        totalPlayTimes += it.durationText?.let { it1 ->
            durationTextToMillis(it1)
        }?.toLong() ?: 0
    }
     */

    /*
    var showLyrics by rememberSaveable {
        mutableStateOf(false)
    }
     */

    val pauseBetweenSongs by rememberPreference(pauseBetweenSongsKey, PauseBetweenSongs.`0`)

    var playbackSpeed by rememberPreference(playbackSpeedKey, 1f)
    var showSpeedPlayerDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val showRemainingSongTime by rememberPreference(showRemainingSongTimeKey, true)


    if (showSpeedPlayerDialog) {
        PlaybackParamsDialog(
            onDismiss = { showSpeedPlayerDialog = false },
            speedValue = { playbackSpeed = it },
            pitchValue = {}
        )
    }

    val playerControlsType by rememberPreference(playerControlsTypeKey, PlayerControlsType.Modern)

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = playerThumbnailSize.size.dp)
    ) {

        if (playerControlsType == PlayerControlsType.Modern)
            InfoAlbumAndArtistModern(
                binder = binder,
                navController = navController,
                media = media,
                title = title,
                albumId = albumId,
                mediaId = mediaId,
                likedAt = likedAt,
                onCollapse = onCollapse,
                disableScrollingText = disableScrollingText,
                artist = artist,
                artistIds = artistIds,
            )

        if (playerControlsType == PlayerControlsType.Essential)
            InfoAlbumAndArtistEssential(
                binder = binder,
                navController = navController,
                media = media,
                title = title,
                albumId = albumId,
                mediaId = mediaId,
                likedAt = likedAt,
                onCollapse = onCollapse,
                disableScrollingText = disableScrollingText,
                artist = artist,
                artistIds = artistIds,
            )

        Spacer(
            modifier = Modifier
                .height(25.dp)
        )


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        ) {

            /*
            val visualizerData = remember {
                mutableStateOf(VisualizerData())
            }
            val amp = getAmplitudes(
                binder = binder,
                context = context,
                visualizerData = visualizerData
            )
            //println("mediaItem Controls amplitudes ${amp}")

            AudioWaveform(
                amplitudes = amp,
                progress = position.toFloat(),
                onProgressChange = {},
                amplitudeType = AmplitudeType.Avg,
                waveformBrush = SolidColor(colorPalette.text),
                progressBrush = SolidColor(colorPalette.favoritesIcon)

            )
             */

            if (playerTimelineType != PlayerTimelineType.Default && playerTimelineType != PlayerTimelineType.Wavy)
                SeekBarCustom(
                    type = playerTimelineType,
                    value = scrubbingPosition ?: position,
                    minimumValue = 0,
                    maximumValue = duration,
                    onDragStart = {
                        scrubbingPosition = it
                    },
                    onDrag = { delta ->
                        scrubbingPosition = if (duration != C.TIME_UNSET) {
                            scrubbingPosition?.plus(delta)?.coerceIn(0, duration)
                        } else {
                            null
                        }
                    },
                    onDragEnd = {
                        scrubbingPosition?.let(binder.player::seekTo)
                        scrubbingPosition = null
                    },
                    color = colorPalette.collapsedPlayerProgressBar,
                    backgroundColor = colorPalette.textSecondary,
                    shape = RoundedCornerShape(8.dp)
                )

            if (playerTimelineType == PlayerTimelineType.Default)
                SeekBar(
                    value = scrubbingPosition ?: position,
                    minimumValue = 0,
                    maximumValue = duration,
                    onDragStart = {
                        scrubbingPosition = it
                    },
                    onDrag = { delta ->
                        scrubbingPosition = if (duration != C.TIME_UNSET) {
                            scrubbingPosition?.plus(delta)?.coerceIn(0, duration)
                        } else {
                            null
                        }
                    },
                    onDragEnd = {
                        scrubbingPosition?.let(binder.player::seekTo)
                        scrubbingPosition = null
                    },
                    color = colorPalette.collapsedPlayerProgressBar,
                    backgroundColor = colorPalette.textSecondary,
                    shape = RoundedCornerShape(8.dp),
                )




            if (playerTimelineType == PlayerTimelineType.Wavy) {
                SeekBarWaved(
                    position = { animatedPosition.value },
                    range = 0f..media.duration.toFloat(),
                    onSeekStarted = {
                        scrubbingPosition = it.toLong()

                        //isSeeking = true
                        scope.launch {
                            animatedPosition.animateTo(it)
                        }

                    },
                    onSeek = { delta ->
                        scrubbingPosition = if (duration != C.TIME_UNSET) {
                            scrubbingPosition?.plus(delta)?.coerceIn(0F, duration.toFloat())
                                ?.toLong()
                        } else {
                            null
                        }

                        if (media.duration != C.TIME_UNSET) {
                            //isSeeking = true
                            scope.launch {
                                animatedPosition.snapTo(
                                    animatedPosition.value.plus(delta)
                                        .coerceIn(0f, media.duration.toFloat())
                                )
                            }
                        }

                    },
                    onSeekFinished = {
                        scrubbingPosition?.let(binder.player::seekTo)
                        scrubbingPosition = null
                        /*
                    isSeeking = false
                    animatedPosition.let {
                        binder.player.seekTo(it.targetValue.toLong())
                    }
                     */
                    },
                    color = colorPalette.collapsedPlayerProgressBar,
                    isActive = binder.player.isPlaying,
                    backgroundColor = colorPalette.textSecondary,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }



        Spacer(
            modifier = Modifier
                .height(8.dp)
        )


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            BasicText(
                text = formatAsDuration(scrubbingPosition ?: position),
                style = typography.xxs.semiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (duration != C.TIME_UNSET) {
                val positionAndDuration = binder.player.positionAndDurationState()
                var timeRemaining by remember { mutableIntStateOf(0) }
                timeRemaining =
                    positionAndDuration.value.second.toInt() - positionAndDuration.value.first.toInt()
                var paused by remember { mutableStateOf(false) }

                if (pauseBetweenSongs != PauseBetweenSongs.`0`)
                    LaunchedEffect(timeRemaining) {
                        if (
                        //formatAsDuration(timeRemaining.toLong()) == "0:00"
                            timeRemaining.toLong() < 500
                        ) {
                            paused = true
                            binder.player.pause()
                            delay(pauseBetweenSongs.number)
                            //binder.player.seekTo(position+2000)
                            binder.player.play()
                            paused = false
                        }
                    }

                if (!paused) {

                    if (showRemainingSongTime)
                        BasicText(
                            text = "-${formatAsDuration(timeRemaining.toLong())}",
                            style = typography.xxs.semiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                        )

                    /*
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                    ) {
                        Image(
                            painter = painterResource(R.drawable.time),
                            colorFilter = ColorFilter.tint(colorPalette.accent),
                            modifier = Modifier
                                .size(20.dp)
                                .padding(horizontal = 5.dp),
                            contentDescription = "Background Image",
                            contentScale = ContentScale.Fit
                        )
                        BasicText(
                            text = " ${formatAsTime(totalPlayTimes)}",
                            style = typography.xxs.semiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                     */

                } else {
                    Image(
                        painter = painterResource(R.drawable.pause),
                        colorFilter = ColorFilter.tint(colorPalette.accent),
                        modifier = Modifier
                            .size(20.dp),
                        contentDescription = "Background Image",
                        contentScale = ContentScale.Fit
                    )
                }

                /*
                BasicText(
                    text = "-${formatAsDuration(timeRemaining.toLong())} / ${formatAsDuration(duration)}",
                    style = typography.xxs.semiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                 */

                BasicText(
                    text = formatAsDuration(duration),
                    style = typography.xxs.semiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

            }
        }

        Spacer(
            modifier = Modifier
                .weight(0.4f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            if (playerControlsType == PlayerControlsType.Essential)
                ControlsEssential(
                    binder = binder,
                    position = position,
                    playbackSpeed = playbackSpeed,
                    shouldBePlaying = shouldBePlaying,
                    likedAt = likedAt,
                    mediaId = mediaId,
                    playerPlayButtonType = playerPlayButtonType,
                    rotationAngle = rotationAngle,
                    isGradientBackgroundEnabled = isGradientBackgroundEnabled,
                    onShowSpeedPlayerDialog = { showSpeedPlayerDialog = true }
                )

            if (playerControlsType == PlayerControlsType.Modern)
                ControlsModern(
                    binder = binder,
                    position = position,
                    playbackSpeed = playbackSpeed,
                    shouldBePlaying = shouldBePlaying,
                    playerPlayButtonType = playerPlayButtonType,
                    rotationAngle = rotationAngle,
                    isGradientBackgroundEnabled = isGradientBackgroundEnabled,
                    onShowSpeedPlayerDialog = { showSpeedPlayerDialog = true }
                )
        }

        Spacer(
            modifier = Modifier
                .weight(0.5f)
        )

    }

}


/*
@ExperimentalTextApi
@ExperimentalAnimationApi
@UnstableApi
@Composable
private fun PlayerMenu(
    binder: PlayerService.Binder,
    mediaItem: MediaItem,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    BaseMediaItemMenu(
        mediaItem = mediaItem,
        onStartRadio = {
            binder.stopRadio()
            binder.player.seamlessPlay(mediaItem)
            binder.setupRadio(NavigationEndpoint.Endpoint.Watch(videoId = mediaItem.mediaId))
        },
        onGoToEqualizer = {
            try {
                activityResultLauncher.launch(
                    Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                        putExtra(AudioEffect.EXTRA_AUDIO_SESSION, binder.player.audioSessionId)
                        putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                        putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                    }
                )
            } catch (e: ActivityNotFoundException) {
                context.toast("Couldn't find an application to equalize audio")
            }
        },
        onShowSleepTimer = {},
        onDismiss = onDismiss
    )
}

@Composable
private fun Duration(
    position: Float,
    duration: Long,
) {
    val typography = LocalAppearance.current.typography
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicText(
            text = formatAsDuration(position.toLong()),
            style = typography.xxs.semiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (duration != C.TIME_UNSET) {
            BasicText(
                text = formatAsDuration(duration),
                style = typography.xxs.semiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
*/