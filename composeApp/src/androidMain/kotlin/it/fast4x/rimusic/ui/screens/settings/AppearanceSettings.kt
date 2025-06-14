package it.fast4x.rimusic.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingComponents
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.AnimatedGradient
import it.fast4x.rimusic.enums.BackgroundProgress
import it.fast4x.rimusic.enums.CarouselSize
import it.fast4x.rimusic.enums.ColorPaletteMode
import it.fast4x.rimusic.enums.ColorPaletteName
import it.fast4x.rimusic.enums.IconLikeType
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
import it.fast4x.rimusic.enums.QueueType
import it.fast4x.rimusic.enums.SongsNumber
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.enums.ThumbnailType
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.components.themed.AppearancePresetDialog
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.RestartPlayerService
import it.fast4x.rimusic.utils.isAtLeastAndroid7
import it.fast4x.rimusic.utils.isLandscape
import it.fast4x.rimusic.utils.semiBold
import me.knighthat.component.tab.Search
import me.knighthat.utils.Toaster

@Composable
fun DefaultAppearanceSettings() {
    var showthumbnail by Settings.PLAYER_SHOW_THUMBNAIL
    showthumbnail = true
    var transparentbar by Settings.TRANSPARENT_TIMELINE
    transparentbar = true
    var blackgradient by Settings.BLACK_GRADIENT
    blackgradient = false
    var showlyricsthumbnail by Settings.LYRICS_SHOW_THUMBNAIL
    showlyricsthumbnail = false
    var playerPlayButtonType by Settings.PLAYER_PLAY_BUTTON_TYPE
    playerPlayButtonType = PlayerPlayButtonType.Disabled
    var bottomgradient by Settings.PLAYER_BOTTOM_GRADIENT
    bottomgradient = false
    var textoutline by Settings.TEXT_OUTLINE
    textoutline = false
    var disablePlayerHorizontalSwipe by Settings.PLAYER_THUMBNAIL_HORIZONTAL_SWIPE_DISABLED
    disablePlayerHorizontalSwipe = false
    var disableScrollingText by Settings.SCROLLING_TEXT_DISABLED
    disableScrollingText = false
    var visualizerEnabled by Settings.PLAYER_VISUALIZER
    visualizerEnabled = false
    var playerTimelineType by Settings.PLAYER_TIMELINE_TYPE
    playerTimelineType = PlayerTimelineType.FakeAudioBar
    var playerThumbnailSize by Settings.PLAYER_PORTRAIT_THUMBNAIL_SIZE
    playerThumbnailSize = PlayerThumbnailSize.Biggest
    var playerTimelineSize by Settings.PLAYER_TIMELINE_SIZE
    playerTimelineSize = PlayerTimelineSize.Biggest
    var effectRotationEnabled by Settings.ROTATION_EFFECT
    effectRotationEnabled = true
    var thumbnailTapEnabled by Settings.PLAYER_TAP_THUMBNAIL_FOR_LYRICS
    thumbnailTapEnabled = true
    var showButtonPlayerAddToPlaylist by Settings.PLAYER_ACTION_ADD_TO_PLAYLIST
    showButtonPlayerAddToPlaylist = true
    var showButtonPlayerArrow by Settings.PLAYER_ACTION_OPEN_QUEUE_ARROW
    showButtonPlayerArrow = false
    var showButtonPlayerDownload by Settings.PLAYER_ACTION_DOWNLOAD
    showButtonPlayerDownload = true
    var showButtonPlayerLoop by Settings.PLAYER_ACTION_LOOP
    showButtonPlayerLoop = true
    var showButtonPlayerLyrics by Settings.PLAYER_ACTION_SHOW_LYRICS
    showButtonPlayerLyrics = true
    var expandedplayertoggle by Settings.PLAYER_ACTION_TOGGLE_EXPAND
    expandedplayertoggle = true
    var showButtonPlayerShuffle by Settings.PLAYER_ACTION_SHUFFLE
    showButtonPlayerShuffle = true
    var showButtonPlayerSleepTimer by Settings.PLAYER_ACTION_SLEEP_TIMER
    showButtonPlayerSleepTimer = false
    var showButtonPlayerMenu by Settings.PLAYER_ACTION_SHOW_MENU
    showButtonPlayerMenu = false
    var showButtonPlayerSystemEqualizer by Settings.PLAYER_ACTION_OPEN_EQUALIZER
    showButtonPlayerSystemEqualizer = false
    var showButtonPlayerDiscover by Settings.PLAYER_ACTION_DISCOVER
    showButtonPlayerDiscover = false
    var showButtonPlayerVideo by Settings.PLAYER_ACTION_TOGGLE_VIDEO
    showButtonPlayerVideo = false
    var navigationBarPosition by Settings.NAVIGATION_BAR_POSITION
    navigationBarPosition = NavigationBarPosition.Bottom
    var showTotalTimeQueue by Settings.PLAYER_SHOW_TOTAL_QUEUE_TIME
    showTotalTimeQueue = true
    var backgroundProgress by Settings.MINI_PLAYER_PROGRESS_BAR
    backgroundProgress = BackgroundProgress.MiniPlayer
    var showNextSongsInPlayer by Settings.PLAYER_SHOW_NEXT_IN_QUEUE
    showNextSongsInPlayer = false
    var showRemainingSongTime by Settings.PLAYER_SHOW_SONGS_REMAINING_TIME
    showRemainingSongTime = true
    var clickLyricsText by Settings.LYRICS_JUMP_ON_TAP
    clickLyricsText = true
    var showBackgroundLyrics by Settings.LYRICS_SHOW_ACCENT_BACKGROUND
    showBackgroundLyrics = false
    var thumbnailRoundness by Settings.THUMBNAIL_BORDER_RADIUS
    thumbnailRoundness = ThumbnailRoundness.Heavy
    var miniPlayerType by Settings.MINI_PLAYER_TYPE
    miniPlayerType = MiniPlayerType.Modern
    var playerBackgroundColors by Settings.PLAYER_BACKGROUND
    playerBackgroundColors = PlayerBackgroundColors.BlurredCoverColor
    var showTopActionsBar by Settings.PLAYER_SHOW_TOP_ACTIONS_BAR
    showTopActionsBar = true
    var playerControlsType by Settings.PLAYER_CONTROLS_TYPE
    playerControlsType = PlayerControlsType.Modern
    var playerInfoType by Settings.PLAYER_INFO_TYPE
    playerInfoType = PlayerInfoType.Modern
    var transparentBackgroundActionBarPlayer by Settings.PLAYER_TRANSPARENT_ACTIONS_BAR
    transparentBackgroundActionBarPlayer = false
    var iconLikeType by Settings.LIKE_ICON
    iconLikeType = IconLikeType.Essential
    var playerSwapControlsWithTimeline by Settings.PLAYER_IS_CONTROL_AND_TIMELINE_SWAPPED
    playerSwapControlsWithTimeline = false
    var playerEnableLyricsPopupMessage by Settings.PLAYER_ACTION_LYRICS_POPUP_MESSAGE
    playerEnableLyricsPopupMessage = true
    var actionspacedevenly by Settings.PLAYER_ACTION_BUTTONS_SPACED_EVENLY
    actionspacedevenly = false
    var thumbnailType by Settings.THUMBNAIL_TYPE
    thumbnailType = ThumbnailType.Modern
    var showvisthumbnail by Settings.PLAYER_SHOW_THUMBNAIL_ON_VISUALIZER
    showvisthumbnail = false
    var buttonzoomout by Settings.ZOOM_OUT_ANIMATION
    buttonzoomout = false
    var thumbnailpause by Settings.PLAYER_SHRINK_THUMBNAIL_ON_PAUSE
    thumbnailpause = false
    var showsongs by Settings.MAX_NUMBER_OF_NEXT_IN_QUEUE
    showsongs = SongsNumber.`2`
    var showalbumcover by Settings.PLAYER_SHOW_NEXT_IN_QUEUE_THUMBNAIL
    showalbumcover = true
    var tapqueue by Settings.PLAYER_ACTIONS_BAR_TAP_TO_OPEN_QUEUE
    tapqueue = true
    var swipeUpQueue by Settings.PLAYER_ACTIONS_BAR_SWIPE_UP_TO_OPEN_QUEUE
    swipeUpQueue = true
    var statsfornerds by Settings.PLAYER_STATS_FOR_NERDS
    statsfornerds = false
    var playerType by Settings.PLAYER_TYPE
    playerType = PlayerType.Essential
    var queueType by Settings.QUEUE_TYPE
    queueType = QueueType.Essential
    var noblur by Settings.PLAYER_BACKGROUND_BLUR
    noblur = true
    var fadingedge by Settings.PLAYER_BACKGROUND_FADING_EDGE
    fadingedge = false
    var carousel by Settings.PLAYER_THUMBNAILS_CAROUSEL
    carousel = true
    var carouselSize by Settings.CAROUSEL_SIZE
    carouselSize = CarouselSize.Biggest
    var keepPlayerMinimized by Settings.PLAYER_KEEP_MINIMIZED
    keepPlayerMinimized = false
    var playerInfoShowIcons by Settings.PLAYER_SONG_INFO_ICON
    playerInfoShowIcons = true
}

@ExperimentalAnimationApi
@UnstableApi
@Composable
fun AppearanceSettings(
    navController: NavController,
) {

    var showthumbnail by Settings.PLAYER_SHOW_THUMBNAIL
    var transparentbar by Settings.TRANSPARENT_TIMELINE
    var showlyricsthumbnail by Settings.LYRICS_SHOW_THUMBNAIL
    var expandedplayer by Settings.PLAYER_EXPANDED
    var playerPlayButtonType by Settings.PLAYER_PLAY_BUTTON_TYPE
    var bottomgradient by Settings.PLAYER_BOTTOM_GRADIENT
    var visualizerEnabled by Settings.PLAYER_VISUALIZER
    /*
    var playerVisualizerType by rememberPreference(
        playerVisualizerTypeKey,
        PlayerVisualizerType.Disabled
    )
    */
    var playerTimelineType by Settings.PLAYER_TIMELINE_TYPE
    var playerThumbnailSize by Settings.PLAYER_PORTRAIT_THUMBNAIL_SIZE
    var playerTimelineSize by Settings.PLAYER_TIMELINE_SIZE
    //

    var showButtonPlayerAddToPlaylist by Settings.PLAYER_ACTION_ADD_TO_PLAYLIST
    var showButtonPlayerArrow by Settings.PLAYER_ACTION_OPEN_QUEUE_ARROW
    var showButtonPlayerDownload by Settings.PLAYER_ACTION_DOWNLOAD
    var showButtonPlayerLoop by Settings.PLAYER_ACTION_LOOP
    var showButtonPlayerLyrics by Settings.PLAYER_ACTION_SHOW_LYRICS
    var expandedplayertoggle by Settings.PLAYER_ACTION_TOGGLE_EXPAND
    var showButtonPlayerShuffle by Settings.PLAYER_ACTION_SHUFFLE
    var showButtonPlayerSleepTimer by Settings.PLAYER_ACTION_SLEEP_TIMER
    var showButtonPlayerMenu by Settings.PLAYER_ACTION_SHOW_MENU
    var showButtonPlayerStartradio by Settings.PLAYER_ACTION_START_RADIO
    var showButtonPlayerSystemEqualizer by Settings.PLAYER_ACTION_OPEN_EQUALIZER
    var showButtonPlayerDiscover by Settings.PLAYER_ACTION_DISCOVER
    var showButtonPlayerVideo by Settings.PLAYER_ACTION_TOGGLE_VIDEO
    val navigationBarPosition by Settings.NAVIGATION_BAR_POSITION
    //var isGradientBackgroundEnabled by rememberPreference(isGradientBackgroundEnabledKey, false)
    var showTotalTimeQueue by Settings.PLAYER_SHOW_TOTAL_QUEUE_TIME
    var showNextSongsInPlayer by Settings.PLAYER_SHOW_NEXT_IN_QUEUE
    var showRemainingSongTime by Settings.PLAYER_SHOW_SONGS_REMAINING_TIME

    val search = Search()

    var thumbnailRoundness by Settings.THUMBNAIL_BORDER_RADIUS

    var playerBackgroundColors by Settings.PLAYER_BACKGROUND

    var showTopActionsBar by Settings.PLAYER_SHOW_TOP_ACTIONS_BAR
    var playerControlsType by Settings.PLAYER_CONTROLS_TYPE
    var playerInfoType by Settings.PLAYER_INFO_TYPE
    var transparentBackgroundActionBarPlayer by Settings.PLAYER_TRANSPARENT_ACTIONS_BAR
    var actionspacedevenly by Settings.PLAYER_ACTION_BUTTONS_SPACED_EVENLY
    var thumbnailType by Settings.THUMBNAIL_TYPE
    var showvisthumbnail by Settings.PLAYER_SHOW_THUMBNAIL_ON_VISUALIZER
    var thumbnailpause by Settings.PLAYER_SHRINK_THUMBNAIL_ON_PAUSE
    var statsfornerds by Settings.PLAYER_STATS_FOR_NERDS

    var playerType by Settings.PLAYER_TYPE
    var noblur by Settings.PLAYER_BACKGROUND_BLUR
    var fadingedge by Settings.PLAYER_BACKGROUND_FADING_EDGE
    var carouselSize by Settings.CAROUSEL_SIZE
    var playerInfoShowIcons by Settings.PLAYER_SONG_INFO_ICON
    var showCoverThumbnailAnimation by Settings.PLAYER_THUMBNAIL_ANIMATION

    var enableWallpaper by Settings.ENABLE_WALLPAPER
    var topPadding by Settings.PLAYER_TOP_PADDING
    var animatedGradient by Settings.ANIMATED_GRADIENT

    var appearanceChooser by remember{ mutableStateOf(false)}
    var restartService by rememberSaveable { mutableStateOf(false) }


    Column(
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

        search.ToolBarButton()
        search.SearchBar( this )

        //SettingsEntryGroupText(stringResource(R.string.user_interface))

        //SettingsGroupSpacer()
        SettingsEntryGroupText(title = stringResource(R.string.player))

        if (playerBackgroundColors != PlayerBackgroundColors.BlurredCoverColor)
            showthumbnail = true
        if (!visualizerEnabled) showvisthumbnail = false
        if (!showthumbnail) {showlyricsthumbnail = false; showvisthumbnail = false}
        if (playerType == PlayerType.Modern) {
            showlyricsthumbnail = false
            showvisthumbnail = false
            thumbnailpause = false
            //keepPlayerMinimized = false
        }
        var blurStrength by Settings.PLAYER_BACKGROUND_BLUR_STRENGTH
        var thumbnailFadeEx  by Settings.PLAYER_THUMBNAIL_FADE_EX
        var thumbnailFade  by Settings.PLAYER_THUMBNAIL_FADE
        var thumbnailSpacing  by Settings.PLAYER_THUMBNAIL_SPACING
        var colorPaletteName by Settings.COLOR_PALETTE
        var colorPaletteMode by Settings.THEME_MODE

        if (appearanceChooser){
            AppearancePresetDialog(
            onDismiss = {appearanceChooser = false},
            onClick0 = {
                showTopActionsBar = true
                showthumbnail = true
                playerBackgroundColors = PlayerBackgroundColors.BlurredCoverColor
                blurStrength = 50f
                thumbnailRoundness = ThumbnailRoundness.None
                playerInfoType = PlayerInfoType.Essential
                playerTimelineType = PlayerTimelineType.ThinBar
                playerTimelineSize = PlayerTimelineSize.Biggest
                playerControlsType = PlayerControlsType.Essential
                playerPlayButtonType = PlayerPlayButtonType.Disabled
                transparentbar = true
                playerType = PlayerType.Essential
                showlyricsthumbnail = false
                expandedplayer = true
                thumbnailType = ThumbnailType.Modern
                playerThumbnailSize = PlayerThumbnailSize.Big
                showTotalTimeQueue = false
                bottomgradient = true
                showRemainingSongTime = true
                showNextSongsInPlayer = false
                colorPaletteName = ColorPaletteName.Dynamic
                colorPaletteMode = ColorPaletteMode.System
                ///////ACTION BAR BUTTONS////////////////
                transparentBackgroundActionBarPlayer = true
                actionspacedevenly = true
                showButtonPlayerVideo = false
                showButtonPlayerDiscover = false
                showButtonPlayerDownload = false
                showButtonPlayerAddToPlaylist = true
                showButtonPlayerLoop = false
                showButtonPlayerShuffle = true
                showButtonPlayerLyrics = false
                expandedplayertoggle = false
                showButtonPlayerSleepTimer = false
                visualizerEnabled = false
                appearanceChooser = false
                showButtonPlayerArrow = false
                showButtonPlayerStartradio = false
                showButtonPlayerMenu = true
                ///////////////////////////
                appearanceChooser = false
            },
            onClick1 = {
                showTopActionsBar = true
                showthumbnail = true
                playerBackgroundColors = PlayerBackgroundColors.BlurredCoverColor
                blurStrength = 50f
                playerInfoType = PlayerInfoType.Essential
                playerPlayButtonType = PlayerPlayButtonType.Disabled
                playerTimelineType = PlayerTimelineType.ThinBar
                playerControlsType = PlayerControlsType.Essential
                transparentbar = true
                playerType = PlayerType.Modern
                expandedplayer = true
                fadingedge = true
                thumbnailFadeEx = 4f
                thumbnailSpacing = -32f
                thumbnailType = ThumbnailType.Essential
                carouselSize = CarouselSize.Big
                playerThumbnailSize = PlayerThumbnailSize.Biggest
                showTotalTimeQueue = false
                transparentBackgroundActionBarPlayer = true
                showRemainingSongTime = true
                bottomgradient = true
                showlyricsthumbnail = false
                thumbnailRoundness = ThumbnailRoundness.Medium
                showNextSongsInPlayer = true
                colorPaletteName = ColorPaletteName.Dynamic
                colorPaletteMode = ColorPaletteMode.System
                ///////ACTION BAR BUTTONS////////////////
                transparentBackgroundActionBarPlayer = true
                actionspacedevenly = true
                showButtonPlayerVideo = false
                showButtonPlayerDiscover = false
                showButtonPlayerDownload = false
                showButtonPlayerAddToPlaylist = true
                showButtonPlayerLoop = false
                showButtonPlayerShuffle = false
                showButtonPlayerLyrics = false
                expandedplayertoggle = true
                showButtonPlayerSleepTimer = false
                visualizerEnabled = false
                appearanceChooser = false
                showButtonPlayerArrow = false
                showButtonPlayerStartradio = false
                showButtonPlayerMenu = true
                ///////////////////////////
                appearanceChooser = false
            },
            onClick2 = {
                showTopActionsBar = false
                showthumbnail = false
                noblur = true
                topPadding = false
                playerBackgroundColors = PlayerBackgroundColors.BlurredCoverColor
                blurStrength = 50f
                playerPlayButtonType = PlayerPlayButtonType.Disabled
                playerInfoType = PlayerInfoType.Modern
                playerInfoShowIcons = false
                playerTimelineType = PlayerTimelineType.ThinBar
                playerControlsType = PlayerControlsType.Essential
                transparentbar = true
                playerType = PlayerType.Modern
                expandedplayer = true
                showTotalTimeQueue = false
                transparentBackgroundActionBarPlayer = true
                showRemainingSongTime = true
                bottomgradient = true
                showlyricsthumbnail = false
                showNextSongsInPlayer = false
                colorPaletteName = ColorPaletteName.Dynamic
                colorPaletteMode = ColorPaletteMode.System
                ///////ACTION BAR BUTTONS////////////////
                transparentBackgroundActionBarPlayer = true
                actionspacedevenly = true
                showButtonPlayerVideo = false
                showButtonPlayerDiscover = false
                showButtonPlayerDownload = false
                showButtonPlayerAddToPlaylist = false
                showButtonPlayerLoop = false
                showButtonPlayerShuffle = false
                showButtonPlayerLyrics = false
                expandedplayertoggle = false
                showButtonPlayerSleepTimer = false
                visualizerEnabled = false
                appearanceChooser = false
                showButtonPlayerArrow = false
                showButtonPlayerStartradio = false
                showButtonPlayerMenu = true
                ///////////////////////////
                appearanceChooser = false
            },
            onClick3 = {
                showTopActionsBar = false
                topPadding = false
                showthumbnail = true
                playerBackgroundColors = PlayerBackgroundColors.BlurredCoverColor
                blurStrength = 50f
                playerInfoType = PlayerInfoType.Essential
                playerTimelineType = PlayerTimelineType.FakeAudioBar
                playerTimelineSize = PlayerTimelineSize.Biggest
                playerControlsType = PlayerControlsType.Modern
                playerPlayButtonType = PlayerPlayButtonType.Disabled
                colorPaletteName = ColorPaletteName.PureBlack
                transparentbar = false
                playerType = PlayerType.Essential
                expandedplayer = false
                playerThumbnailSize = PlayerThumbnailSize.Expanded
                showTotalTimeQueue = false
                transparentBackgroundActionBarPlayer = true
                showRemainingSongTime = true
                bottomgradient = true
                showlyricsthumbnail = false
                thumbnailType = ThumbnailType.Essential
                thumbnailRoundness = ThumbnailRoundness.Light
                playerType = PlayerType.Modern
                fadingedge = true
                thumbnailFade = 5f
                showNextSongsInPlayer = false
                ///////ACTION BAR BUTTONS////////////////
                transparentBackgroundActionBarPlayer = true
                actionspacedevenly = true
                showButtonPlayerVideo = false
                showButtonPlayerDiscover = false
                showButtonPlayerDownload = false
                showButtonPlayerAddToPlaylist = false
                showButtonPlayerLoop = true
                showButtonPlayerShuffle = true
                showButtonPlayerLyrics = false
                expandedplayertoggle = false
                showButtonPlayerSleepTimer = false
                visualizerEnabled = false
                appearanceChooser = false
                showButtonPlayerArrow = true
                showButtonPlayerStartradio = false
                showButtonPlayerMenu = true
                ///////////////////////////
                appearanceChooser = false
            },
            onClick4 = {
                showTopActionsBar = false
                topPadding = true
                showthumbnail = true
                playerBackgroundColors = PlayerBackgroundColors.AnimatedGradient
                animatedGradient = AnimatedGradient.Linear
                playerInfoType = PlayerInfoType.Essential
                playerTimelineType = PlayerTimelineType.PinBar
                playerTimelineSize = PlayerTimelineSize.Biggest
                playerControlsType = PlayerControlsType.Essential
                playerPlayButtonType = PlayerPlayButtonType.Square
                colorPaletteName = ColorPaletteName.Dynamic
                colorPaletteMode = ColorPaletteMode.PitchBlack
                transparentbar = false
                playerType = PlayerType.Modern
                expandedplayer = false
                playerThumbnailSize = PlayerThumbnailSize.Biggest
                showTotalTimeQueue = false
                transparentBackgroundActionBarPlayer = true
                showRemainingSongTime = true
                showlyricsthumbnail = false
                thumbnailType = ThumbnailType.Modern
                thumbnailRoundness = ThumbnailRoundness.Heavy
                fadingedge = true
                thumbnailFade = 0f
                thumbnailFadeEx = 5f
                thumbnailSpacing = -32f
                showNextSongsInPlayer = false
                ///////ACTION BAR BUTTONS////////////////
                transparentBackgroundActionBarPlayer = true
                actionspacedevenly = true
                showButtonPlayerVideo = false
                showButtonPlayerDiscover = false
                showButtonPlayerDownload = true
                showButtonPlayerAddToPlaylist = false
                showButtonPlayerLoop = false
                showButtonPlayerShuffle = false
                showButtonPlayerLyrics = false
                expandedplayertoggle = true
                showButtonPlayerSleepTimer = false
                visualizerEnabled = false
                appearanceChooser = false
                showButtonPlayerArrow =false
                showButtonPlayerStartradio = false
                showButtonPlayerMenu = true
                ///////////////////////////
                appearanceChooser = false
            },
            onClick5 = {
                showTopActionsBar = true
                showthumbnail = true
                playerBackgroundColors = PlayerBackgroundColors.CoverColorGradient
                playerInfoType = PlayerInfoType.Essential
                playerTimelineType = PlayerTimelineType.Wavy
                playerTimelineSize = PlayerTimelineSize.Biggest
                playerControlsType = PlayerControlsType.Essential
                playerPlayButtonType = PlayerPlayButtonType.CircularRibbed
                colorPaletteName = ColorPaletteName.Dynamic
                colorPaletteMode = ColorPaletteMode.System
                transparentbar = false
                playerType = PlayerType.Essential
                expandedplayer = true
                playerThumbnailSize = PlayerThumbnailSize.Big
                showTotalTimeQueue = false
                transparentBackgroundActionBarPlayer = true
                showRemainingSongTime = true
                showlyricsthumbnail = false
                thumbnailType = ThumbnailType.Modern
                thumbnailRoundness = ThumbnailRoundness.Heavy
                showNextSongsInPlayer = false
                ///////ACTION BAR BUTTONS////////////////
                transparentBackgroundActionBarPlayer = true
                actionspacedevenly = true
                showButtonPlayerVideo = false
                showButtonPlayerDiscover = false
                showButtonPlayerDownload = false
                showButtonPlayerAddToPlaylist = false
                showButtonPlayerLoop = false
                showButtonPlayerShuffle = true
                showButtonPlayerLyrics = true
                expandedplayertoggle = false
                showButtonPlayerSleepTimer = false
                visualizerEnabled = false
                appearanceChooser = false
                showButtonPlayerArrow =false
                showButtonPlayerStartradio = false
                showButtonPlayerMenu = true
                ///////////////////////////
                appearanceChooser = false
            }
            )
        }

        if (!isLandscape) {
            Column {
                BasicText(
                    text = stringResource(R.string.appearancepresets),
                    style = typography().m.semiBold.copy(color = colorPalette().text),
                    modifier = Modifier
                        .padding(all = 12.dp)
                        .clickable(onClick = { appearanceChooser = true })
                )
                BasicText(
                    text = stringResource(R.string.appearancepresetssecondary),
                    style = typography().xs.semiBold.copy(color = colorPalette().textSecondary),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .padding(bottom = 10.dp)
                )
            }

            if (search.inputValue.isBlank() || stringResource(R.string.show_player_top_actions_bar).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_SHOW_TOP_ACTIONS_BAR,
                    titleId = R.string.show_player_top_actions_bar
                )

            if (!showTopActionsBar) {
                if (search.inputValue.isBlank() || stringResource(R.string.blankspace).contains(
                        search.inputValue,
                        true
                    )
                )
                    SettingComponents.BooleanEntry(
                        Settings.PLAYER_TOP_PADDING,
                        R.string.blankspace
                    )
            }
        }
        if (search.inputValue.isBlank() || stringResource(R.string.playertype).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.PLAYER_TYPE,
                R.string.playertype
            )

        if (search.inputValue.isBlank() || stringResource(R.string.queuetype).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.QUEUE_TYPE,
                R.string.queuetype
            )

        if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) {
            if (search.inputValue.isBlank() || stringResource(R.string.show_thumbnail).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_SHOW_THUMBNAIL,
                    R.string.show_thumbnail
                )
        }
        AnimatedVisibility(visible = !showthumbnail && playerType == PlayerType.Modern && !isLandscape) {
            if (search.inputValue.isBlank() || stringResource(R.string.swipe_Animation_No_Thumbnail).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.EnumEntry(
                    Settings.PLAYER_NO_THUMBNAIL_SWIPE_ANIMATION,
                    R.string.swipe_Animation_No_Thumbnail,
                    Modifier.padding(
                        start = if ( playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor )
                            25.dp
                        else
                            0.dp
                    ),
                )
        }
        AnimatedVisibility(visible = showthumbnail) {
            Column {
                if (playerType == PlayerType.Modern) {
                    if (search.inputValue.isBlank() || stringResource(R.string.fadingedge).contains(
                            search.inputValue,
                            true
                        )
                    )
                        SettingComponents.BooleanEntry(
                            Settings.PLAYER_BACKGROUND_FADING_EDGE,
                            R.string.fadingedge,
                            Modifier.padding(
                                start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
                                    25.dp
                                else
                                    0.dp
                            )
                        )
                }

                if (playerType == PlayerType.Modern && !isLandscape && (expandedplayertoggle || expandedplayer)) {
                    if (search.inputValue.isBlank() || stringResource(R.string.carousel).contains(
                            search.inputValue,
                            true
                        )
                    )
                        SettingComponents.BooleanEntry(
                            Settings.PLAYER_THUMBNAILS_CAROUSEL,
                            R.string.carousel,
                            Modifier.padding(
                                start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
                                    25.dp
                                else
                                    0.dp
                            )
                        )

                    if (search.inputValue.isBlank() || stringResource(R.string.carouselsize).contains(
                            search.inputValue,
                            true
                        )
                    )
                        SettingComponents.EnumEntry(
                            Settings.CAROUSEL_SIZE,
                            R.string.carouselsize,
                            Modifier.padding(
                                start = if ( playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor )
                                    25.dp
                                else
                                    0.dp
                            )
                        )
                }
                if (playerType == PlayerType.Essential) {

                    if (search.inputValue.isBlank() || stringResource(R.string.thumbnailpause).contains(
                            search.inputValue,
                            true
                        )
                    )
                        SettingComponents.BooleanEntry(
                            Settings.PLAYER_SHRINK_THUMBNAIL_ON_PAUSE,
                            R.string.thumbnailpause,
                            Modifier.padding(
                                start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
                                    25.dp
                                else
                                    0.dp
                            )
                        )

                    if (search.inputValue.isBlank() || stringResource(R.string.show_lyrics_thumbnail).contains(
                            search.inputValue,
                            true
                        )
                    )
                        SettingComponents.BooleanEntry(
                            Settings.LYRICS_SHOW_THUMBNAIL,
                            R.string.show_lyrics_thumbnail,
                            Modifier.padding(
                                start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
                                    25.dp
                                else
                                    0.dp
                            )
                        )
                    if (visualizerEnabled) {
                        if (search.inputValue.isBlank() || stringResource(R.string.showvisthumbnail).contains(
                                search.inputValue,
                                true
                            )
                        )
                            SettingComponents.BooleanEntry(
                                Settings.PLAYER_SHOW_THUMBNAIL_ON_VISUALIZER,
                                R.string.showvisthumbnail,
                                Modifier.padding(
                                    start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
                                        25.dp
                                    else
                                        0.dp
                                )
                            )
                    }
                }

                if (search.inputValue.isBlank() || stringResource(R.string.show_cover_thumbnail_animation).contains(
                        search.inputValue,
                        true
                    )
                ) {
                    SettingComponents.BooleanEntry(
                        Settings.PLAYER_THUMBNAIL_ANIMATION,
                        R.string.show_cover_thumbnail_animation,
                        Modifier.padding(
                            start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
                                25.dp
                            else
                                0.dp
                        )
                    )
                    AnimatedVisibility(visible = showCoverThumbnailAnimation) {
                        Column {
                            SettingComponents.EnumEntry(
                                Settings.PLAYER_THUMBNAIL_TYPE,
                                R.string.cover_thumbnail_animation_type,
                                Modifier.padding(
                                    start = if ( playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor )
                                        50.dp
                                    else
                                        25.dp
                                )
                            )
                        }
                    }
                }

                if (isLandscape) {
                    if (search.inputValue.isBlank() || stringResource(R.string.player_thumbnail_size).contains(
                            search.inputValue,
                            true
                        )
                    )
                        SettingComponents.EnumEntry(
                            Settings.PLAYER_LANDSCAPE_THUMBNAIL_SIZE,
                            R.string.player_thumbnail_size,
                            Modifier.padding(
                                start = if ( playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor )
                                    25.dp
                                else
                                    0.dp
                            )
                        )
                } else {
                    if (search.inputValue.isBlank() || stringResource(R.string.player_thumbnail_size).contains(
                            search.inputValue,
                            true
                        )
                    )
                        SettingComponents.EnumEntry(
                            Settings.PLAYER_PORTRAIT_THUMBNAIL_SIZE,
                            R.string.player_thumbnail_size,
                            Modifier.padding(
                                start = if ( playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor )
                                    25.dp
                                else
                                    0.dp
                            )
                        )
                }
                if (search.inputValue.isBlank() || stringResource(R.string.thumbnailtype).contains(
                        search.inputValue,
                        true
                    )
                )
                    SettingComponents.EnumEntry(
                        Settings.THUMBNAIL_TYPE,
                        R.string.thumbnailtype,
                        Modifier.padding(
                            start = if ( playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor )
                                25.dp
                            else
                                0.dp
                        )
                    )

                if (search.inputValue.isBlank() || stringResource(R.string.thumbnail_roundness).contains(
                        search.inputValue,
                        true
                    )
                )
                    SettingComponents.EnumEntry(
                        Settings.THUMBNAIL_BORDER_RADIUS,
                        R.string.thumbnail_roundness,
                        Modifier.padding(
                            start = if ( playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor )
                                25.dp
                            else
                                0.dp
                        ),
                        trailingContent = {
                            Spacer(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = colorPalette().accent,
                                        shape = thumbnailRoundness.shape
                                    )
                                    .background(
                                        color = colorPalette().background1,
                                        shape = thumbnailRoundness.shape
                                    )
                                    .size(36.dp)
                            )
                        }
                    )
            }
        }

        if (!showthumbnail) {
            if (search.inputValue.isBlank() || stringResource(R.string.noblur).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_BACKGROUND_BLUR,
                    R.string.noblur
                )
        }

        if (!(showthumbnail && playerType == PlayerType.Essential)){
            if (search.inputValue.isBlank() || stringResource(R.string.statsfornerdsplayer).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_STATS_FOR_NERDS,
                    R.string.statsfornerdsplayer
                )
        }

        if (search.inputValue.isBlank() || stringResource(R.string.timelinesize).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.PLAYER_TIMELINE_SIZE,
                R.string.timelinesize
            )

        if (search.inputValue.isBlank() || stringResource(R.string.pinfo_type).contains(
                search.inputValue,
                true
            )
        ) {
            SettingComponents.EnumEntry(
                Settings.PLAYER_INFO_TYPE,
                R.string.pinfo_type
            )
            SettingComponents.Description( R.string.pinfo_album_and_artist_name )

            AnimatedVisibility( visible = playerInfoType == PlayerInfoType.Modern) {
                Column {
                    if (search.inputValue.isBlank() || stringResource(R.string.pinfo_show_icons).contains(
                            search.inputValue,
                            true
                        )
                    )
                        SettingComponents.BooleanEntry(
                            Settings.PLAYER_SONG_INFO_ICON,
                            R.string.pinfo_show_icons,
                            Modifier.padding( start = 25.dp )
                        )
                }
            }

        }



        if (search.inputValue.isBlank() || stringResource(R.string.miniplayertype).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.MINI_PLAYER_TYPE,
                R.string.miniplayertype
            )

        if (search.inputValue.isBlank() || stringResource(R.string.player_swap_controls_with_timeline).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_IS_CONTROL_AND_TIMELINE_SWAPPED,
                R.string.player_swap_controls_with_timeline
            )

        if (search.inputValue.isBlank() || stringResource(R.string.timeline).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.PLAYER_TIMELINE_TYPE,
                R.string.timeline
            )

        if (search.inputValue.isBlank() || stringResource(R.string.transparentbar).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.TRANSPARENT_TIMELINE,
                R.string.transparentbar
            )

        if (search.inputValue.isBlank() || stringResource(R.string.pcontrols_type).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.PLAYER_CONTROLS_TYPE,
                R.string.pcontrols_type
            )


        if (search.inputValue.isBlank() || stringResource(R.string.play_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.PLAYER_PLAY_BUTTON_TYPE,
                R.string.play_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.buttonzoomout).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.ZOOM_OUT_ANIMATION,
                R.string.buttonzoomout
            )

        if (search.inputValue.isBlank() || stringResource(R.string.play_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.LIKE_ICON,
                R.string.play_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.background_colors).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.PLAYER_BACKGROUND,
                R.string.background_colors
            )

        AnimatedVisibility(visible = playerBackgroundColors == PlayerBackgroundColors.AnimatedGradient) {
            if (search.inputValue.isBlank() || stringResource(R.string.gradienttype).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.EnumEntry(
                    Settings.ANIMATED_GRADIENT,
                    R.string.gradienttype,
                    Modifier.padding(
                        start = if ( playerBackgroundColors == PlayerBackgroundColors.AnimatedGradient)
                            25.dp
                        else
                            0.dp
                    )
                )
        }
        AnimatedVisibility( playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor ) {
            if ( search.inputValue.isBlank() || stringResource( R.string.rotating_cover_title ).contains(search.inputValue, true) )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_ROTATING_ALBUM_COVER,
                    R.string.rotating_cover_title,
                    Modifier.padding( start = 25.dp )
                )
        }


        if ((playerBackgroundColors == PlayerBackgroundColors.CoverColorGradient) || (playerBackgroundColors == PlayerBackgroundColors.ThemeColorGradient))
            if (search.inputValue.isBlank() || stringResource(R.string.blackgradient).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.BLACK_GRADIENT,
                    R.string.blackgradient
                )

        if ((playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) && (playerType == PlayerType.Modern))
            if (search.inputValue.isBlank() || stringResource(R.string.albumCoverRotation).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_THUMBNAIL_ROTATION,
                    R.string.albumCoverRotation,
                    Modifier.padding( start = 25.dp )
                )

        if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor)
            if (search.inputValue.isBlank() || stringResource(R.string.bottomgradient).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_BOTTOM_GRADIENT,
                    R.string.bottomgradient
                )
        if (search.inputValue.isBlank() || stringResource(R.string.textoutline).contains(
              search.inputValue,
              true
              )
         )
            SettingComponents.BooleanEntry(
                Settings.TEXT_OUTLINE,
                R.string.textoutline
            )

       if (search.inputValue.isBlank() || stringResource(R.string.show_total_time_of_queue).contains(
                search.inputValue,
                true
            )
        )
           SettingComponents.BooleanEntry(
               Settings.PLAYER_SHOW_TOTAL_QUEUE_TIME,
               R.string.show_total_time_of_queue
           )

        if (search.inputValue.isBlank() || stringResource(R.string.show_remaining_song_time).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_SHOW_SONGS_REMAINING_TIME,
                R.string.show_remaining_song_time
            )

        if (search.inputValue.isBlank() || stringResource(R.string.show_next_songs_in_player).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_SHOW_NEXT_IN_QUEUE,
                R.string.show_next_songs_in_player
            )
        AnimatedVisibility( visible = showNextSongsInPlayer) {
          Column {
              if (search.inputValue.isBlank() || stringResource(R.string.showtwosongs).contains(search.inputValue,true))
                  SettingComponents.EnumEntry(
                      Settings.MAX_NUMBER_OF_NEXT_IN_QUEUE,
                      R.string.songs_number_to_show,
                      Modifier.padding( start = 25.dp )
                  )


            if (search.inputValue.isBlank() || stringResource(R.string.showalbumcover).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_SHOW_NEXT_IN_QUEUE_THUMBNAIL,
                    R.string.showalbumcover
                )
          }
        }

        if (search.inputValue.isBlank() || stringResource(R.string.disable_scrolling_text).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.SCROLLING_TEXT_DISABLED,
                R.string.disable_scrolling_text,
                R.string.scrolling_text_is_used_for_long_texts
            )

        if (search.inputValue.isBlank() || stringResource(if (playerType == PlayerType.Modern && !isLandscape) R.string.disable_horizontal_swipe else R.string.disable_vertical_swipe).contains(
                search.inputValue,
                true
            )
        ) {
            val (titleId, subtitleId) = if( playerType == PlayerType.Modern && !isLandscape )
                R.string.disable_vertical_swipe to R.string.disable_vertical_swipe_secondary
            else
                R.string.disable_horizontal_swipe to R.string.disable_song_switching_via_swipe

            SettingComponents.BooleanEntry(
                Settings.PLAYER_THUMBNAIL_HORIZONTAL_SWIPE_DISABLED,
                titleId,
                subtitleId
            )
        }

        if (search.inputValue.isBlank() || stringResource(R.string.player_rotating_buttons).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.ROTATION_EFFECT,
                R.string.player_rotating_buttons,
                R.string.player_enable_rotation_buttons
            )

        if (search.inputValue.isBlank() || stringResource(R.string.toggle_lyrics).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_TAP_THUMBNAIL_FOR_LYRICS,
                R.string.toggle_lyrics,
                R.string.by_tapping_on_the_thumbnail
            )

        if (search.inputValue.isBlank() || stringResource(R.string.click_lyrics_text).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.LYRICS_JUMP_ON_TAP,
                R.string.click_lyrics_text
            )
        if (showlyricsthumbnail)
            if (search.inputValue.isBlank() || stringResource(R.string.show_background_in_lyrics).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.LYRICS_SHOW_ACCENT_BACKGROUND,
                    R.string.show_background_in_lyrics
                )

        if (search.inputValue.isBlank() || stringResource(R.string.player_enable_lyrics_popup_message).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_LYRICS_POPUP_MESSAGE,
                R.string.player_enable_lyrics_popup_message
            )

        if (search.inputValue.isBlank() || stringResource(R.string.background_progress_bar).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.EnumEntry(
                Settings.MINI_PLAYER_PROGRESS_BAR,
                R.string.background_progress_bar
            )


        if (search.inputValue.isBlank() || stringResource(R.string.visualizer).contains(
                search.inputValue,
                true
            )
        ) {
            SettingComponents.BooleanEntry(
                Settings.PLAYER_VISUALIZER,
                R.string.visualizer
            )

            SettingComponents.Description( R.string.visualizer_require_mic_permission, isImportant = true )
        }

        SettingsGroupSpacer()
        SettingsEntryGroupText(title = stringResource(R.string.player_action_bar))

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_transparent_background).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_TRANSPARENT_ACTIONS_BAR,
                R.string.action_bar_transparent_background
            )

        if (search.inputValue.isBlank() || stringResource(R.string.actionspacedevenly).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_BUTTONS_SPACED_EVENLY,
                R.string.actionspacedevenly
            )

        if (search.inputValue.isBlank() || stringResource(R.string.tapqueue).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTIONS_BAR_TAP_TO_OPEN_QUEUE,
                R.string.tapqueue
            )

        if (search.inputValue.isBlank() || stringResource(R.string.swipe_up_to_open_the_queue).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTIONS_BAR_SWIPE_UP_TO_OPEN_QUEUE,
                R.string.swipe_up_to_open_the_queue
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_video_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_TOGGLE_VIDEO,
                R.string.action_bar_show_video_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_discover_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_DISCOVER,
                R.string.action_bar_show_discover_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_download_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_DOWNLOAD,
                R.string.action_bar_show_download_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_add_to_playlist_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_ADD_TO_PLAYLIST,
                R.string.action_bar_show_add_to_playlist_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_loop_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_LOOP,
                R.string.action_bar_show_loop_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_shuffle_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_SHUFFLE,
                R.string.action_bar_show_shuffle_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_lyrics_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_SHOW_LYRICS,
                R.string.action_bar_show_lyrics_button
            )
        if (!isLandscape || !showthumbnail) {
            if (!showlyricsthumbnail) {
                if (search.inputValue.isBlank() || stringResource(R.string.expandedplayer).contains(
                        search.inputValue,
                        true
                    )
                )
                    SettingComponents.BooleanEntry(
                        Settings.PLAYER_ACTION_TOGGLE_EXPAND,
                        R.string.expandedplayer
                    )
            }
        }

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_sleep_timer_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_SLEEP_TIMER,
                R.string.action_bar_show_sleep_timer_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.show_equalizer).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_OPEN_EQUALIZER,
                R.string.show_equalizer
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_arrow_button_to_open_queue).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_OPEN_QUEUE_ARROW,
                R.string.action_bar_show_arrow_button_to_open_queue
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_start_radio_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_START_RADIO,
                R.string.action_bar_show_start_radio_button
            )

        if (search.inputValue.isBlank() || stringResource(R.string.action_bar_show_menu_button).contains(
                search.inputValue,
                true
            )
        )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_ACTION_SHOW_MENU,
                R.string.action_bar_show_menu_button
            )

        if (!showlyricsthumbnail) {
            SettingsGroupSpacer()
            SettingsEntryGroupText(title = stringResource(R.string.full_screen_lyrics_components))

            if (showTotalTimeQueue) {
                if (search.inputValue.isBlank() || stringResource(R.string.show_total_time_of_queue).contains(
                        search.inputValue,
                        true
                    )
                )
                    SettingComponents.BooleanEntry(
                        Settings.PLAYER_IS_QUEUE_DURATION_EXPANDED,
                        R.string.show_total_time_of_queue
                    )
            }

            if (search.inputValue.isBlank() || stringResource(R.string.titleartist).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_IS_TITLE_EXPANDED,
                    R.string.titleartist
                )

            if (search.inputValue.isBlank() || stringResource(R.string.timeline).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_IS_TIMELINE_EXPANDED,
                    R.string.timeline
                )

            if (search.inputValue.isBlank() || stringResource(R.string.controls).contains(
                    search.inputValue,
                    true
                )
            )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_IS_CONTROLS_EXPANDED,
                    R.string.controls
                )

            if (statsfornerds && (!(showthumbnail && playerType == PlayerType.Essential))){
                if (search.inputValue.isBlank() || stringResource(R.string.statsfornerds).contains(
                        search.inputValue,
                        true
                    )
                )
                    SettingComponents.BooleanEntry(
                        Settings.PLAYER_IS_STATS_FOR_NERDS_EXPANDED,
                        R.string.statsfornerds
                    )
            }

            var isActionsBarExpanded by Settings.PLAYER_IS_ACTIONS_BAR_EXPANDED
            if (
                showButtonPlayerDownload ||
                showButtonPlayerAddToPlaylist ||
                showButtonPlayerLoop ||
                showButtonPlayerShuffle ||
                showButtonPlayerLyrics ||
                showButtonPlayerSleepTimer ||
                showButtonPlayerSystemEqualizer ||
                showButtonPlayerArrow ||
                showButtonPlayerMenu ||
                expandedplayertoggle ||
                showButtonPlayerDiscover ||
                showButtonPlayerVideo
            ){

                if (search.inputValue.isBlank() || stringResource(R.string.actionbar).contains(
                        search.inputValue,
                        true
                    )
                )
                    SettingComponents.BooleanEntry(
                        Settings.PLAYER_IS_ACTIONS_BAR_EXPANDED,
                        R.string.actionbar
                    )
            }
            if (showNextSongsInPlayer && isActionsBarExpanded) {
                if (search.inputValue.isBlank() || stringResource(R.string.miniqueue).contains(
                        search.inputValue,
                        true
                    )
                )
                    SettingComponents.BooleanEntry(
                        Settings.PLAYER_IS_NEXT_IN_QUEUE_EXPANDED,
                        R.string.miniqueue
                    )
            }

        }

        if( search.inputValue.isBlank() || stringResource( R.string.title_playback_speed ).contains( search.inputValue, true ) )
            SettingComponents.BooleanEntry(
                Settings.AUDIO_SPEED,
                R.string.title_playback_speed,
                R.string.description_playback_speed
            )

        SettingsGroupSpacer()
        SettingsEntryGroupText(title = stringResource(R.string.notification_player))

        if (search.inputValue.isBlank() || stringResource(R.string.notification_player).contains(
                search.inputValue,
                true
            )
        ) {
            SettingComponents.EnumEntry(
                Settings.MEDIA_NOTIFICATION_FIRST_ICON,
                R.string.notificationPlayerFirstIcon,
                action = SettingComponents.Action.RESTART_PLAYER_SERVICE
            ) { restartService = true }
            SettingComponents.EnumEntry(
                Settings.MEDIA_NOTIFICATION_SECOND_ICON,
                R.string.notificationPlayerSecondIcon,
                action = SettingComponents.Action.RESTART_PLAYER_SERVICE
            ) { restartService = true }

            RestartPlayerService(restartService, onRestart = { restartService = false })
        }

        if (isAtLeastAndroid7) {
            SettingsGroupSpacer()
            SettingsEntryGroupText(title = stringResource(R.string.wallpaper))
            SettingComponents.BooleanEntry(
                Settings.ENABLE_WALLPAPER,
                R.string.enable_wallpaper
            )
            AnimatedVisibility(visible = enableWallpaper) {
                Column {
                    SettingComponents.EnumEntry(
                        Settings.WALLPAPER_TYPE,
                        R.string.set_cover_thumbnail_as_wallpaper,
                        Modifier.padding( start = 25.dp ),
                        action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                    ) { restartService = true }
                    RestartPlayerService(restartService, onRestart = { restartService = false })
                }
            }
        }

        SettingsGroupSpacer()
        var resetToDefault by remember { mutableStateOf(false) }
        val context = LocalContext.current
        ButtonBarSettingEntry(
            title = stringResource(R.string.settings_reset),
            text = stringResource(R.string.settings_restore_default_settings),
            icon = R.drawable.refresh,
            iconColor = colorPalette().text,
            onClick = { resetToDefault = true },
        )
        if (resetToDefault) {
            DefaultAppearanceSettings()
            resetToDefault = false
            navController.popBackStack()
            Toaster.done()
        }

        SettingsGroupSpacer(
            modifier = Modifier.height(Dimensions.bottomSpacer)
        )
    }
}
