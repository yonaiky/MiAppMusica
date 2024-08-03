package it.fast4x.rimusic.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.BackgroundProgress
import it.fast4x.rimusic.enums.CarouselSize
import it.fast4x.rimusic.enums.ClickLyricsText
import it.fast4x.rimusic.enums.IconLikeType
import it.fast4x.rimusic.enums.LyricsColor
import it.fast4x.rimusic.enums.MiniPlayerType
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PlayerBackgroundColors
import it.fast4x.rimusic.enums.PlayerControlsType
import it.fast4x.rimusic.enums.PlayerInfoType
import it.fast4x.rimusic.enums.PlayerPlayButtonType
import it.fast4x.rimusic.enums.PlayerThumbnailSize
import it.fast4x.rimusic.enums.PlayerTimelineSize
import it.fast4x.rimusic.enums.PlayerTimelineType
import it.fast4x.rimusic.enums.PlayerType
import it.fast4x.rimusic.enums.PlayerVisualizerType
import it.fast4x.rimusic.enums.PrevNextSongs
import it.fast4x.rimusic.enums.SongsNumber
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.enums.ThumbnailType
import it.fast4x.rimusic.ui.components.themed.HeaderIconButton
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.actionspacedevenlyKey
import it.fast4x.rimusic.utils.backgroundProgressKey
import it.fast4x.rimusic.utils.clickLyricsTextKey
import it.fast4x.rimusic.utils.disablePlayerHorizontalSwipeKey
import it.fast4x.rimusic.utils.disableScrollingTextKey
import it.fast4x.rimusic.utils.effectRotationKey
import it.fast4x.rimusic.utils.expandedplayertoggleKey
import it.fast4x.rimusic.utils.iconLikeTypeKey
import it.fast4x.rimusic.utils.isAtLeastAndroid13
import it.fast4x.rimusic.utils.isLandscape
import it.fast4x.rimusic.utils.isShowingThumbnailInLockscreenKey
import it.fast4x.rimusic.utils.lastPlayerPlayButtonTypeKey
import it.fast4x.rimusic.utils.lyricsColorKey
import it.fast4x.rimusic.utils.miniPlayerTypeKey
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.playerBackgroundColorsKey
import it.fast4x.rimusic.utils.playerControlsTypeKey
import it.fast4x.rimusic.utils.playerEnableLyricsPopupMessageKey
import it.fast4x.rimusic.utils.playerInfoTypeKey
import it.fast4x.rimusic.utils.playerPlayButtonTypeKey
import it.fast4x.rimusic.utils.playerSwapControlsWithTimelineKey
import it.fast4x.rimusic.utils.playerThumbnailSizeKey
import it.fast4x.rimusic.utils.playerTimelineSizeKey
import it.fast4x.rimusic.utils.playerTimelineTypeKey
import it.fast4x.rimusic.utils.playerVisualizerTypeKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.secondary
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.utils.showBackgroundLyricsKey
import it.fast4x.rimusic.utils.showButtonPlayerAddToPlaylistKey
import it.fast4x.rimusic.utils.showButtonPlayerArrowKey
import it.fast4x.rimusic.utils.showButtonPlayerDownloadKey
import it.fast4x.rimusic.utils.showButtonPlayerLoopKey
import it.fast4x.rimusic.utils.showButtonPlayerLyricsKey
import it.fast4x.rimusic.utils.showButtonPlayerMenuKey
import it.fast4x.rimusic.utils.showButtonPlayerShuffleKey
import it.fast4x.rimusic.utils.showButtonPlayerSleepTimerKey
import it.fast4x.rimusic.utils.showButtonPlayerSystemEqualizerKey
import it.fast4x.rimusic.utils.showDownloadButtonBackgroundPlayerKey
import it.fast4x.rimusic.utils.showLikeButtonBackgroundPlayerKey
import it.fast4x.rimusic.utils.showNextSongsInPlayerKey
import it.fast4x.rimusic.utils.showRemainingSongTimeKey
import it.fast4x.rimusic.utils.showTopActionsBarKey
import it.fast4x.rimusic.utils.showTotalTimeQueueKey
import it.fast4x.rimusic.utils.showlyricsthumbnailKey
import it.fast4x.rimusic.utils.showthumbnailKey
import it.fast4x.rimusic.utils.thumbnailRoundnessKey
import it.fast4x.rimusic.utils.thumbnailTapEnabledKey
import it.fast4x.rimusic.utils.transparentBackgroundPlayerActionBarKey
import it.fast4x.rimusic.utils.transparentbarKey
import it.fast4x.rimusic.utils.blackgradientKey
import it.fast4x.rimusic.utils.bold
import it.fast4x.rimusic.utils.visualizerEnabledKey
import it.fast4x.rimusic.utils.bottomgradientKey
import it.fast4x.rimusic.utils.buttonzoomoutKey
import it.fast4x.rimusic.utils.carouselKey
import it.fast4x.rimusic.utils.carouselSizeKey
import it.fast4x.rimusic.utils.expandedlyricsKey
import it.fast4x.rimusic.utils.fadingedgeKey
import it.fast4x.rimusic.utils.showalbumcoverKey
import it.fast4x.rimusic.utils.showsongsKey
import it.fast4x.rimusic.utils.showvisthumbnailKey
import it.fast4x.rimusic.utils.textoutlineKey
import it.fast4x.rimusic.utils.thumbnailTypeKey
import it.fast4x.rimusic.utils.thumbnailpauseKey
import it.fast4x.rimusic.utils.playerTypeKey
import it.fast4x.rimusic.utils.prevNextSongsKey
import it.fast4x.rimusic.utils.showButtonPlayerDiscoverKey
import it.fast4x.rimusic.utils.statsfornerdsKey
import it.fast4x.rimusic.utils.tapqueueKey
import it.fast4x.rimusic.utils.noblurKey
import it.fast4x.rimusic.utils.keepPlayerMinimizedKey

@ExperimentalAnimationApi
@UnstableApi
@Composable
fun AppearanceSettings() {

    var isShowingThumbnailInLockscreen by rememberPreference(
        isShowingThumbnailInLockscreenKey,
        true
    )

    var showthumbnail by rememberPreference(showthumbnailKey, false)
    var transparentbar by rememberPreference(transparentbarKey, true)
    var blackgradient by rememberPreference(blackgradientKey, false)
    var showlyricsthumbnail by rememberPreference(showlyricsthumbnailKey, false)
    var playerPlayButtonType by rememberPreference(
        playerPlayButtonTypeKey,
        PlayerPlayButtonType.Rectangular
    )
    var bottomgradient by rememberPreference(bottomgradientKey, false)
    var textoutline by rememberPreference(textoutlineKey, false)

    var lastPlayerPlayButtonType by rememberPreference(
        lastPlayerPlayButtonTypeKey,
        PlayerPlayButtonType.Rectangular
    )
    var disablePlayerHorizontalSwipe by rememberPreference(disablePlayerHorizontalSwipeKey, false)

    var disableScrollingText by rememberPreference(disableScrollingTextKey, false)
    var showLikeButtonBackgroundPlayer by rememberPreference(
        showLikeButtonBackgroundPlayerKey,
        true
    )
    var showDownloadButtonBackgroundPlayer by rememberPreference(
        showDownloadButtonBackgroundPlayerKey,
        true
    )
    var visualizerEnabled by rememberPreference(visualizerEnabledKey, false)
    /*
    var playerVisualizerType by rememberPreference(
        playerVisualizerTypeKey,
        PlayerVisualizerType.Disabled
    )
    */
    var playerTimelineType by rememberPreference(playerTimelineTypeKey, PlayerTimelineType.Default)
    var playerThumbnailSize by rememberPreference(
        playerThumbnailSizeKey,
        PlayerThumbnailSize.Biggest
    )
    var playerTimelineSize by rememberPreference(
        playerTimelineSizeKey,
        PlayerTimelineSize.Biggest
    )

    var effectRotationEnabled by rememberPreference(effectRotationKey, true)

    var thumbnailTapEnabled by rememberPreference(thumbnailTapEnabledKey, false)


    var showButtonPlayerAddToPlaylist by rememberPreference(showButtonPlayerAddToPlaylistKey, true)
    var showButtonPlayerArrow by rememberPreference(showButtonPlayerArrowKey, false)
    var showButtonPlayerDownload by rememberPreference(showButtonPlayerDownloadKey, true)
    var showButtonPlayerLoop by rememberPreference(showButtonPlayerLoopKey, true)
    var showButtonPlayerLyrics by rememberPreference(showButtonPlayerLyricsKey, true)
    var expandedplayertoggle by rememberPreference(expandedplayertoggleKey, true)
    var showButtonPlayerShuffle by rememberPreference(showButtonPlayerShuffleKey, true)
    var showButtonPlayerSleepTimer by rememberPreference(showButtonPlayerSleepTimerKey, false)
    var showButtonPlayerMenu by rememberPreference(showButtonPlayerMenuKey, false)
    var showButtonPlayerSystemEqualizer by rememberPreference(
        showButtonPlayerSystemEqualizerKey,
        false
    )
    var showButtonPlayerDiscover by rememberPreference(showButtonPlayerDiscoverKey, false)

    val navigationBarPosition by rememberPreference(
        navigationBarPositionKey,
        NavigationBarPosition.Bottom
    )

    //var isGradientBackgroundEnabled by rememberPreference(isGradientBackgroundEnabledKey, false)
    var showTotalTimeQueue by rememberPreference(showTotalTimeQueueKey, true)
    var backgroundProgress by rememberPreference(
        backgroundProgressKey,
        BackgroundProgress.Both
    )
    var showNextSongsInPlayer by rememberPreference(showNextSongsInPlayerKey, false)
    var showRemainingSongTime by rememberPreference(showRemainingSongTimeKey, true)
    var clickLyricsText by rememberPreference(clickLyricsTextKey, ClickLyricsText.FullScreen)
    var showBackgroundLyrics by rememberPreference(showBackgroundLyricsKey, false)
    val (colorPalette, typography, thumbnailShape) = LocalAppearance.current
    var searching by rememberSaveable { mutableStateOf(false) }
    var filter: String? by rememberSaveable { mutableStateOf(null) }
    // var filterCharSequence: CharSequence
    var filterCharSequence: CharSequence = filter.toString()
    var thumbnailRoundness by rememberPreference(
        thumbnailRoundnessKey,
        ThumbnailRoundness.Heavy

    )

    var miniPlayerType by rememberPreference(
        miniPlayerTypeKey,
        MiniPlayerType.Modern
    )
    var playerBackgroundColors by rememberPreference(
        playerBackgroundColorsKey,
        PlayerBackgroundColors.BlurredCoverColor
    )

    var showTopActionsBar by rememberPreference(showTopActionsBarKey, true)
    var playerControlsType by rememberPreference(playerControlsTypeKey, PlayerControlsType.Modern)
    var playerInfoType by rememberPreference(playerInfoTypeKey, PlayerInfoType.Modern)
    var transparentBackgroundActionBarPlayer by rememberPreference(
        transparentBackgroundPlayerActionBarKey,
        false
    )
    var iconLikeType by rememberPreference(iconLikeTypeKey, IconLikeType.Essential)
    var playerSwapControlsWithTimeline by rememberPreference(
        playerSwapControlsWithTimelineKey,
        false
    )
    var playerEnableLyricsPopupMessage by rememberPreference(
        playerEnableLyricsPopupMessageKey,
        true
    )
    var actionspacedevenly by rememberPreference(actionspacedevenlyKey, false)
    var thumbnailType by rememberPreference(thumbnailTypeKey, ThumbnailType.Modern)
    var showvisthumbnail by rememberPreference(showvisthumbnailKey, false)
    var expandedlyrics by rememberPreference(expandedlyricsKey, true)
    var buttonzoomout by rememberPreference(buttonzoomoutKey, false)
    var thumbnailpause by rememberPreference(thumbnailpauseKey, false)
    var showsongs by rememberPreference(showsongsKey, SongsNumber.`2`)
    var showalbumcover by rememberPreference(showalbumcoverKey, true)
    var prevNextSongs by rememberPreference(prevNextSongsKey, PrevNextSongs.twosongs)
    var tapqueue by rememberPreference(tapqueueKey, true)
    var statsfornerds by rememberPreference(statsfornerdsKey, false)

    var playerType by rememberPreference(playerTypeKey, PlayerType.Essential)
    var noblur by rememberPreference(noblurKey, true)
    var fadingedge by rememberPreference(fadingedgeKey, false)
    var carousel by rememberPreference(carouselKey, true)
    var carouselSize by rememberPreference(carouselSizeKey, CarouselSize.Biggest)
    var keepPlayerMinimized by rememberPreference(keepPlayerMinimizedKey,false)

    Column(
        modifier = Modifier
            .background(colorPalette.background0)
            //.fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth(
                if (navigationBarPosition == NavigationBarPosition.Left ||
                    navigationBarPosition == NavigationBarPosition.Top ||
                    navigationBarPosition == NavigationBarPosition.Bottom
                ) 1f
                else Dimensions.contentWidthRightBar
            )
            .verticalScroll(rememberScrollState())
        /*
        .padding(
            LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
                .asPaddingValues()
        )

         */
    ) {
        HeaderWithIcon(
            title = stringResource(R.string.player_appearance),
            iconId = R.drawable.color_palette,
            enabled = false,
            showIcon = true,
            modifier = Modifier,
            onClick = {}
        )

        HeaderIconButton(
            modifier = Modifier.padding(horizontal = 5.dp),
            onClick = { searching = !searching },
            icon = R.drawable.search_circle,
            color = colorPalette.text,
            iconSize = 24.dp
        )
        /*   Search   */
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth()
        ) {
            AnimatedVisibility(visible = searching) {
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
                                    style = typography.xs.semiBold.secondary.copy(color = colorPalette.textDisabled),
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
        /*  Search  */

        //SettingsEntryGroupText(stringResource(R.string.user_interface))

        //SettingsGroupSpacer()
        SettingsEntryGroupText(title = stringResource(R.string.player))

        if (playerBackgroundColors != PlayerBackgroundColors.BlurredCoverColor)
            showthumbnail = true
        if (!visualizerEnabled) showvisthumbnail = false
        if (!showthumbnail) {showlyricsthumbnail = false; showvisthumbnail = false}
        if (playerType == PlayerType.Modern) {showlyricsthumbnail = false; showvisthumbnail = false; thumbnailpause = false; keepPlayerMinimized = false}
        if (showlyricsthumbnail) expandedlyrics = false

        if (filter.isNullOrBlank() || stringResource(R.string.show_player_top_actions_bar).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.show_player_top_actions_bar),
                text = "",
                isChecked = showTopActionsBar,
                onCheckedChange = { showTopActionsBar = it }
            )
        if (filter.isNullOrBlank() || stringResource(R.string.playertype).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.playertype),
                selectedValue = playerType,
                onValueSelected = {
                    playerType = it
                },
                valueText = {
                    when (it) {
                        PlayerType.Modern -> stringResource(R.string.pcontrols_modern)
                        PlayerType.Essential -> stringResource(R.string.pcontrols_essential)
                    }
                },
            )

        if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) {
            if (filter.isNullOrBlank() || stringResource(R.string.show_thumbnail).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.show_thumbnail),
                    text = "",
                    isChecked = showthumbnail,
                    onCheckedChange = {showthumbnail = it},
                )
        }
        AnimatedVisibility(visible = showthumbnail) {
            Column {
                if (playerType == PlayerType.Modern) {
                    if (filter.isNullOrBlank() || stringResource(R.string.fadingedge).contains(
                            filterCharSequence,
                            true
                        )
                    )
                        SwitchSettingEntry(
                            title = stringResource(R.string.fadingedge),
                            text = "",
                            isChecked = fadingedge,
                            onCheckedChange = { fadingedge = it },
                            modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                        )
                }

                if (playerType == PlayerType.Modern && !isLandscape) {
                    if (filter.isNullOrBlank() || stringResource(R.string.carousel).contains(
                            filterCharSequence,
                            true
                        )
                    )
                        SwitchSettingEntry(
                            title = stringResource(R.string.carousel),
                            text = "",
                            isChecked = carousel,
                            onCheckedChange = { carousel = it },
                            modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                        )
                    if (carousel) {
                        if (filter.isNullOrBlank() || stringResource(R.string.carouselsize).contains(
                                filterCharSequence,
                                true
                            )
                        )
                            EnumValueSelectorSettingsEntry(
                                title = stringResource(R.string.carouselsize),
                                selectedValue = carouselSize,
                                onValueSelected = { carouselSize = it },
                                valueText = {
                                    when (it) {
                                        CarouselSize.Small -> stringResource(R.string.small)
                                        CarouselSize.Medium -> stringResource(R.string.medium)
                                        CarouselSize.Big -> stringResource(R.string.big)
                                        CarouselSize.Biggest -> stringResource(R.string.biggest)
                                        CarouselSize.Expanded -> stringResource(R.string.expanded)
                                    }
                                },
                                modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                            )
                    }
                }
                if (playerType == PlayerType.Essential) {
                    if (filter.isNullOrBlank() || stringResource(R.string.thumbnailpause).contains(
                            filterCharSequence,
                            true
                        )
                    )
                        SwitchSettingEntry(
                            title = stringResource(R.string.thumbnailpause),
                            text = "",
                            isChecked = thumbnailpause,
                            onCheckedChange = { thumbnailpause = it },
                            modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                        )

                    if (filter.isNullOrBlank() || stringResource(R.string.show_lyrics_thumbnail).contains(
                            filterCharSequence,
                            true
                        )
                    )
                        SwitchSettingEntry(
                            title = stringResource(R.string.show_lyrics_thumbnail),
                            text = "",
                            isChecked = showlyricsthumbnail,
                            onCheckedChange = { showlyricsthumbnail = it },
                            modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                        )
                    if (visualizerEnabled) {
                        if (filter.isNullOrBlank() || stringResource(R.string.showvisthumbnail).contains(
                                filterCharSequence,
                                true
                            )
                        )
                            SwitchSettingEntry(
                                title = stringResource(R.string.showvisthumbnail),
                                text = "",
                                isChecked = showvisthumbnail,
                                onCheckedChange = { showvisthumbnail = it },
                                modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                            )
                    }
                }

                if (filter.isNullOrBlank() || stringResource(R.string.player_thumbnail_size).contains(
                        filterCharSequence,
                        true
                    )
                )
                    EnumValueSelectorSettingsEntry(
                        title = stringResource(R.string.player_thumbnail_size),
                        selectedValue = playerThumbnailSize,
                        onValueSelected = { playerThumbnailSize = it },
                        valueText = {
                            when (it) {
                                PlayerThumbnailSize.Small -> stringResource(R.string.small)
                                PlayerThumbnailSize.Medium -> stringResource(R.string.medium)
                                PlayerThumbnailSize.Big -> stringResource(R.string.big)
                                PlayerThumbnailSize.Biggest -> stringResource(R.string.biggest)
                                PlayerThumbnailSize.Expanded -> stringResource(R.string.expanded)
                            }
                        },
                        modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                    )
                if (filter.isNullOrBlank() || stringResource(R.string.thumbnailtype).contains(
                        filterCharSequence,
                        true
                    )
                )
                    EnumValueSelectorSettingsEntry(
                        title = stringResource(R.string.thumbnailtype),
                        selectedValue = thumbnailType,
                        onValueSelected = {
                            thumbnailType = it
                        },
                        valueText = {
                            when (it) {
                                ThumbnailType.Modern -> stringResource(R.string.pcontrols_modern)
                                ThumbnailType.Essential -> stringResource(R.string.pcontrols_essential)
                            }
                        },
                        modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                    )

                if (filter.isNullOrBlank() || stringResource(R.string.thumbnail_roundness).contains(
                        filterCharSequence,
                        true
                    )
                )
                    EnumValueSelectorSettingsEntry(
                        title = stringResource(R.string.thumbnail_roundness),
                        selectedValue = thumbnailRoundness,
                        onValueSelected = { thumbnailRoundness = it },
                        trailingContent = {
                            Spacer(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = colorPalette.accent,
                                        shape = thumbnailRoundness.shape()
                                    )
                                    .background(
                                        color = colorPalette.background1,
                                        shape = thumbnailRoundness.shape()
                                    )
                                    .size(36.dp)
                            )
                        },
                        valueText = {
                            when (it) {
                                ThumbnailRoundness.None -> stringResource(R.string.none)
                                ThumbnailRoundness.Light -> stringResource(R.string.light)
                                ThumbnailRoundness.Heavy -> stringResource(R.string.heavy)
                                ThumbnailRoundness.Medium -> stringResource(R.string.medium)
                            }
                        },
                        modifier = Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)
                    )
            }
        }
        if (!showthumbnail) {
            if (filter.isNullOrBlank() || stringResource(R.string.noblur).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.noblur),
                    text = "",
                    isChecked = noblur,
                    onCheckedChange = { noblur = it }
                )

            if (filter.isNullOrBlank() || stringResource(R.string.statsfornerdsplayer).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.statsfornerdsplayer),
                    text = "",
                    isChecked = statsfornerds,
                    onCheckedChange = { statsfornerds = it }
                )
        }
        if (!showlyricsthumbnail && !isLandscape)
            if (filter.isNullOrBlank() || stringResource(R.string.expandedlyrics).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.expandedlyrics),
                    text = stringResource(R.string.expandedlyricsinfo),
                    isChecked = expandedlyrics,
                    onCheckedChange = { expandedlyrics = it }
                )

        if (filter.isNullOrBlank() || stringResource(R.string.timelinesize).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.timelinesize),
                selectedValue = playerTimelineSize,
                onValueSelected = { playerTimelineSize = it },
                valueText = {
                    when (it) {
                        PlayerTimelineSize.Small -> stringResource(R.string.small)
                        PlayerTimelineSize.Medium -> stringResource(R.string.medium)
                        PlayerTimelineSize.Big -> stringResource(R.string.big)
                        PlayerTimelineSize.Biggest -> stringResource(R.string.biggest)
                        PlayerTimelineSize.Expanded -> stringResource(R.string.expanded)
                    }
                }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.pinfo_type).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.pinfo_type),
                titleSecondary = stringResource(R.string.pinfo_album_and_artist_name),
                selectedValue = playerInfoType,
                onValueSelected = {
                    playerInfoType = it
                },
                valueText = {
                    when (it) {
                        PlayerInfoType.Modern -> stringResource(R.string.pcontrols_modern)
                        PlayerInfoType.Essential -> stringResource(R.string.pcontrols_essential)
                    }
                },
            )

        if (filter.isNullOrBlank() || stringResource(R.string.miniplayertype).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.miniplayertype),
                selectedValue = miniPlayerType,
                onValueSelected = {
                    miniPlayerType = it
                },
                valueText = {
                    when (it) {
                        MiniPlayerType.Modern -> stringResource(R.string.pcontrols_modern)
                        MiniPlayerType.Essential -> stringResource(R.string.pcontrols_essential)
                    }
                },
            )

        if (filter.isNullOrBlank() || stringResource(R.string.player_swap_controls_with_timeline).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.player_swap_controls_with_timeline),
                text = "",
                isChecked = playerSwapControlsWithTimeline,
                onCheckedChange = { playerSwapControlsWithTimeline = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.timeline).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.timeline),
                selectedValue = playerTimelineType,
                onValueSelected = { playerTimelineType = it },
                valueText = {
                    when (it) {
                        PlayerTimelineType.Default -> stringResource(R.string._default)
                        PlayerTimelineType.Wavy -> stringResource(R.string.wavy_timeline)
                        PlayerTimelineType.BodiedBar -> stringResource(R.string.bodied_bar)
                        PlayerTimelineType.PinBar -> stringResource(R.string.pin_bar)
                        PlayerTimelineType.FakeAudioBar -> stringResource(R.string.fake_audio_bar)
                        PlayerTimelineType.ThinBar -> stringResource(R.string.thin_bar)
                        //PlayerTimelineType.ColoredBar -> "Colored bar"
                    }
                }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.transparentbar).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.transparentbar),
                text = "",
                isChecked = transparentbar,
                onCheckedChange = { transparentbar = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.pcontrols_type).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.pcontrols_type),
                selectedValue = playerControlsType,
                onValueSelected = {
                    playerControlsType = it
                },
                valueText = {
                    when (it) {
                        PlayerControlsType.Modern -> stringResource(R.string.pcontrols_modern)
                        PlayerControlsType.Essential -> stringResource(R.string.pcontrols_essential)
                    }
                },
            )


        if (filter.isNullOrBlank() || stringResource(R.string.play_button).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.play_button),
                selectedValue = playerPlayButtonType,
                onValueSelected = {
                    playerPlayButtonType = it
                    lastPlayerPlayButtonType = it
                },
                valueText = {
                    when (it) {
                        PlayerPlayButtonType.Disabled -> stringResource(R.string.vt_disabled)
                        PlayerPlayButtonType.Default -> stringResource(R.string._default)
                        PlayerPlayButtonType.Rectangular -> stringResource(R.string.rectangular)
                        PlayerPlayButtonType.Square -> stringResource(R.string.square)
                        PlayerPlayButtonType.CircularRibbed -> stringResource(R.string.circular_ribbed)
                    }
                },
            )

        if (filter.isNullOrBlank() || stringResource(R.string.buttonzoomout).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.buttonzoomout),
                text = "",
                isChecked = buttonzoomout,
                onCheckedChange = { buttonzoomout = it }
            )


        if (filter.isNullOrBlank() || stringResource(R.string.play_button).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.icon_like_button),
                selectedValue = iconLikeType,
                onValueSelected = {
                    iconLikeType = it
                },
                valueText = {
                    when (it) {
                        IconLikeType.Essential -> stringResource(R.string.pcontrols_essential)
                        IconLikeType.Apple -> stringResource(R.string.icon_like_apple)
                        IconLikeType.Breaked -> stringResource(R.string.icon_like_breaked)
                        IconLikeType.Gift -> stringResource(R.string.icon_like_gift)
                        IconLikeType.Shape -> stringResource(R.string.icon_like_shape)
                        IconLikeType.Striped -> stringResource(R.string.icon_like_striped)
                        IconLikeType.Brilliant -> stringResource(R.string.icon_like_brilliant)
                    }
                },
            )

        /*

        if (filter.isNullOrBlank() || stringResource(R.string.use_gradient_background).contains(filterCharSequence,true))
            SwitchSettingEntry(
                title = stringResource(R.string.use_gradient_background),
                text = "",
                isChecked = isGradientBackgroundEnabled,
                onCheckedChange = { isGradientBackgroundEnabled = it }
            )
         */

        if (filter.isNullOrBlank() || stringResource(R.string.background_colors).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.background_colors),
                selectedValue = playerBackgroundColors,
                onValueSelected = {
                    playerBackgroundColors = it
                },
                valueText = {
                    when (it) {
                        PlayerBackgroundColors.CoverColor -> stringResource(R.string.bg_colors_background_from_cover)
                        PlayerBackgroundColors.ThemeColor -> stringResource(R.string.bg_colors_background_from_theme)
                        PlayerBackgroundColors.CoverColorGradient -> stringResource(R.string.bg_colors_gradient_background_from_cover)
                        PlayerBackgroundColors.ThemeColorGradient -> stringResource(R.string.bg_colors_gradient_background_from_theme)
                        PlayerBackgroundColors.FluidThemeColorGradient -> stringResource(R.string.bg_colors_fluid_gradient_background_from_theme)
                        PlayerBackgroundColors.FluidCoverColorGradient -> stringResource(R.string.bg_colors_fluid_gradient_background_from_cover)
                        PlayerBackgroundColors.BlurredCoverColor -> stringResource(R.string.bg_colors_blurred_cover_background)
                    }
                },
            )

        if ((playerBackgroundColors == PlayerBackgroundColors.CoverColorGradient) || (playerBackgroundColors == PlayerBackgroundColors.ThemeColorGradient))
            if (filter.isNullOrBlank() || stringResource(R.string.blackgradient).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.blackgradient),
                    text = "",
                    isChecked = blackgradient,
                    onCheckedChange = { blackgradient = it }
                )
        if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
            if (filter.isNullOrBlank() || stringResource(R.string.bottomgradient).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.bottomgradient),
                    text = "",
                    isChecked = bottomgradient,
                    onCheckedChange = { bottomgradient = it }
                )
        if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
           if (filter.isNullOrBlank() || stringResource(R.string.textoutline).contains(
                filterCharSequence,
                true
                )
           )
               SwitchSettingEntry(
                   title = stringResource(R.string.textoutline),
                   text = "",
                   isChecked = textoutline,
                   onCheckedChange = { textoutline = it }
               )
       if (filter.isNullOrBlank() || stringResource(R.string.show_total_time_of_queue).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.show_total_time_of_queue),
                text = "",
                isChecked = showTotalTimeQueue,
                onCheckedChange = { showTotalTimeQueue = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.show_remaining_song_time).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.show_remaining_song_time),
                text = "",
                isChecked = showRemainingSongTime,
                onCheckedChange = { showRemainingSongTime = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.show_next_songs_in_player).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.show_next_songs_in_player),
                text = "",
                isChecked = showNextSongsInPlayer,
                onCheckedChange = { showNextSongsInPlayer = it }
            )
        AnimatedVisibility( visible = showNextSongsInPlayer) {
          Column {
              if (filter.isNullOrBlank() || stringResource(R.string.showtwosongs).contains(filterCharSequence,true))
                  EnumValueSelectorSettingsEntry(
                      title = stringResource(R.string.songs_number_to_show),
                      selectedValue = showsongs,
                      onValueSelected = {
                          showsongs = it
                      },
                      valueText = {
                          it.name
                      },
                      modifier = Modifier
                          .padding(start = 25.dp)
                  )


            if (filter.isNullOrBlank() || stringResource(R.string.showalbumcover).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.showalbumcover),
                    text = "",
                    isChecked = showalbumcover,
                    onCheckedChange = { showalbumcover = it },
                      modifier = Modifier.padding(start = 25.dp)
                  )
          }
        }

        if (filter.isNullOrBlank() || stringResource(R.string.disable_scrolling_text).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.disable_scrolling_text),
                text = stringResource(R.string.scrolling_text_is_used_for_long_texts),
                isChecked = disableScrollingText,
                onCheckedChange = { disableScrollingText = it }
            )
        if (playerType == PlayerType.Essential) {
            if (filter.isNullOrBlank() || stringResource(R.string.disable_horizontal_swipe).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.disable_horizontal_swipe),
                    text = stringResource(R.string.disable_song_switching_via_swipe),
                    isChecked = disablePlayerHorizontalSwipe,
                    onCheckedChange = { disablePlayerHorizontalSwipe = it }
                )
        }

        if (filter.isNullOrBlank() || stringResource(R.string.player_rotating_buttons).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.player_rotating_buttons),
                text = stringResource(R.string.player_enable_rotation_buttons),
                isChecked = effectRotationEnabled,
                onCheckedChange = { effectRotationEnabled = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.toggle_lyrics).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.toggle_lyrics),
                text = stringResource(R.string.by_tapping_on_the_thumbnail),
                isChecked = thumbnailTapEnabled,
                onCheckedChange = { thumbnailTapEnabled = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.click_lyrics_text).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.click_lyrics_text),
                selectedValue = clickLyricsText,
                onValueSelected = {
                    clickLyricsText = it
                },
                valueText = {
                    when (it) {
                        ClickLyricsText.Player -> stringResource(R.string.player)
                        ClickLyricsText.FullScreen -> stringResource(R.string.full_screen)
                        ClickLyricsText.Both -> stringResource(R.string.both)
                    }
                },
            )
        if (showlyricsthumbnail)
            if (filter.isNullOrBlank() || stringResource(R.string.show_background_in_lyrics).contains(
                    filterCharSequence,
                    true
                )
            )
                SwitchSettingEntry(
                    title = stringResource(R.string.show_background_in_lyrics),
                    text = "",
                    isChecked = showBackgroundLyrics,
                    onCheckedChange = { showBackgroundLyrics = it }
                )

        if (filter.isNullOrBlank() || stringResource(R.string.player_enable_lyrics_popup_message).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.player_enable_lyrics_popup_message),
                text = "",
                isChecked = playerEnableLyricsPopupMessage,
                onCheckedChange = { playerEnableLyricsPopupMessage = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.background_progress_bar).contains(
                filterCharSequence,
                true
            )
        )
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.background_progress_bar),
                selectedValue = backgroundProgress,
                onValueSelected = {
                    backgroundProgress = it
                },
                valueText = {
                    when (it) {
                        BackgroundProgress.Player -> stringResource(R.string.player)
                        BackgroundProgress.MiniPlayer -> stringResource(R.string.minimized_player)
                        BackgroundProgress.Both -> stringResource(R.string.both)
                        BackgroundProgress.Disabled -> stringResource(R.string.vt_disabled)
                    }
                },
            )


        if (filter.isNullOrBlank() || stringResource(R.string.visualizer).contains(
                filterCharSequence,
                true
            )
        ) {
            SwitchSettingEntry(
                title = stringResource(R.string.visualizer),
                text = "",
                isChecked = visualizerEnabled,
                onCheckedChange = { visualizerEnabled = it }
            )
            /*
            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.visualizer),
                selectedValue = playerVisualizerType,
                onValueSelected = { playerVisualizerType = it },
                valueText = {
                    when (it) {
                        PlayerVisualizerType.Fancy -> stringResource(R.string.vt_fancy)
                        PlayerVisualizerType.Circular -> stringResource(R.string.vt_circular)
                        PlayerVisualizerType.Disabled -> stringResource(R.string.vt_disabled)
                        PlayerVisualizerType.Stacked -> stringResource(R.string.vt_stacked)
                        PlayerVisualizerType.Oneside -> stringResource(R.string.vt_one_side)
                        PlayerVisualizerType.Doubleside -> stringResource(R.string.vt_double_side)
                        PlayerVisualizerType.DoublesideCircular -> stringResource(R.string.vt_double_side_circular)
                        PlayerVisualizerType.Full -> stringResource(R.string.vt_full)
                    }
                }
            )
            */
            ImportantSettingsDescription(text = stringResource(R.string.visualizer_require_mic_permission))
        }

        SettingsGroupSpacer()
        SettingsEntryGroupText(title = stringResource(R.string.player_action_bar))

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_transparent_background).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_transparent_background),
                text = "",
                isChecked = transparentBackgroundActionBarPlayer,
                onCheckedChange = { transparentBackgroundActionBarPlayer = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.actionspacedevenly).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.actionspacedevenly),
                text = "",
                isChecked = actionspacedevenly,
                onCheckedChange = { actionspacedevenly = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.tapqueue).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.tapqueue),
                text = "",
                isChecked = tapqueue,
                onCheckedChange = { tapqueue = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_discover_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_discover_button),
                text = "",
                isChecked = showButtonPlayerDiscover,
                onCheckedChange = { showButtonPlayerDiscover = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_download_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_download_button),
                text = "",
                isChecked = showButtonPlayerDownload,
                onCheckedChange = { showButtonPlayerDownload = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_add_to_playlist_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_add_to_playlist_button),
                text = "",
                isChecked = showButtonPlayerAddToPlaylist,
                onCheckedChange = { showButtonPlayerAddToPlaylist = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_loop_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_loop_button),
                text = "",
                isChecked = showButtonPlayerLoop,
                onCheckedChange = { showButtonPlayerLoop = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_shuffle_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_shuffle_button),
                text = "",
                isChecked = showButtonPlayerShuffle,
                onCheckedChange = { showButtonPlayerShuffle = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_lyrics_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_lyrics_button),
                text = "",
                isChecked = showButtonPlayerLyrics,
                onCheckedChange = { showButtonPlayerLyrics = it }
            )
        if (!isLandscape || !showthumbnail) {
            if (!showlyricsthumbnail and !expandedlyrics) {
                if (filter.isNullOrBlank() || stringResource(R.string.expandedplayer).contains(
                        filterCharSequence,
                        true
                    )
                )
                    SwitchSettingEntry(
                        title = stringResource(R.string.expandedplayer),
                        text = "",
                        isChecked = expandedplayertoggle,
                        onCheckedChange = { expandedplayertoggle = it }
                    )
            }
        }

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_sleep_timer_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_sleep_timer_button),
                text = "",
                isChecked = showButtonPlayerSleepTimer,
                onCheckedChange = { showButtonPlayerSleepTimer = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.show_equalizer).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.show_equalizer),
                text = "",
                isChecked = showButtonPlayerSystemEqualizer,
                onCheckedChange = { showButtonPlayerSystemEqualizer = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_arrow_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_arrow_button),
                text = "",
                isChecked = showButtonPlayerArrow,
                onCheckedChange = { showButtonPlayerArrow = it }
            )

        if (filter.isNullOrBlank() || stringResource(R.string.action_bar_show_menu_button).contains(
                filterCharSequence,
                true
            )
        )
            SwitchSettingEntry(
                title = stringResource(R.string.action_bar_show_menu_button),
                text = "",
                isChecked = showButtonPlayerMenu,
                onCheckedChange = { showButtonPlayerMenu = it }
            )

        SettingsGroupSpacer()
        SettingsEntryGroupText(title = stringResource(R.string.background_player))

        if (filter.isNullOrBlank() || stringResource(R.string.show_favorite_button).contains(
                filterCharSequence,
                true
            )
        ) {
            SwitchSettingEntry(
                title = stringResource(R.string.show_favorite_button),
                text = stringResource(R.string.show_favorite_button_in_lock_screen_and_notification_area),
                isChecked = showLikeButtonBackgroundPlayer,
                onCheckedChange = { showLikeButtonBackgroundPlayer = it }
            )
            ImportantSettingsDescription(text = stringResource(R.string.restarting_rimusic_is_required))
        }
        if (filter.isNullOrBlank() || stringResource(R.string.show_download_button).contains(
                filterCharSequence,
                true
            )
        ) {
            SwitchSettingEntry(
                title = stringResource(R.string.show_download_button),
                text = stringResource(R.string.show_download_button_in_lock_screen_and_notification_area),
                isChecked = showDownloadButtonBackgroundPlayer,
                onCheckedChange = { showDownloadButtonBackgroundPlayer = it }
            )

            ImportantSettingsDescription(text = stringResource(R.string.restarting_rimusic_is_required))
        }

        //SettingsGroupSpacer()
        //SettingsEntryGroupText(title = stringResource(R.string.text))


        if (filter.isNullOrBlank() || stringResource(R.string.show_song_cover).contains(
                filterCharSequence,
                true
            )
        )
            if (!isAtLeastAndroid13) {
                SettingsGroupSpacer()

                SettingsEntryGroupText(title = stringResource(R.string.lockscreen))

                SwitchSettingEntry(
                    title = stringResource(R.string.show_song_cover),
                    text = stringResource(R.string.use_song_cover_on_lockscreen),
                    isChecked = isShowingThumbnailInLockscreen,
                    onCheckedChange = { isShowingThumbnailInLockscreen = it }
                )
            }
        SettingsGroupSpacer(
            modifier = Modifier.height(Dimensions.bottomSpacer)
        )
    }
}
