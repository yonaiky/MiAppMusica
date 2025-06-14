package app.kreate.android.themed.common.screens.settings.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.AnimatedGradient
import it.fast4x.rimusic.enums.CarouselSize
import it.fast4x.rimusic.enums.ColorPaletteMode
import it.fast4x.rimusic.enums.ColorPaletteName
import it.fast4x.rimusic.enums.PlayerBackgroundColors
import it.fast4x.rimusic.enums.PlayerControlsType
import it.fast4x.rimusic.enums.PlayerInfoType
import it.fast4x.rimusic.enums.PlayerPlayButtonType
import it.fast4x.rimusic.enums.PlayerThumbnailSize
import it.fast4x.rimusic.enums.PlayerTimelineSize
import it.fast4x.rimusic.enums.PlayerTimelineType
import it.fast4x.rimusic.enums.PlayerType
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.enums.ThumbnailType
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.components.themed.AppearancePresetDialog
import it.fast4x.rimusic.utils.isLandscape
import it.fast4x.rimusic.utils.semiBold

@Composable
fun PlayerAppearance( search: SettingEntrySearch ) {
    var showthumbnail by Settings.PLAYER_SHOW_THUMBNAIL
    var transparentbar by Settings.TRANSPARENT_TIMELINE
    var showlyricsthumbnail by Settings.LYRICS_SHOW_THUMBNAIL
    var expandedplayer by Settings.PLAYER_EXPANDED
    var playerPlayButtonType by Settings.PLAYER_PLAY_BUTTON_TYPE
    var bottomgradient by Settings.PLAYER_BOTTOM_GRADIENT
    var visualizerEnabled by Settings.PLAYER_VISUALIZER
    var playerTimelineType by Settings.PLAYER_TIMELINE_TYPE
    var playerThumbnailSize by Settings.PLAYER_PORTRAIT_THUMBNAIL_SIZE
    var playerTimelineSize by Settings.PLAYER_TIMELINE_SIZE
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
    var showButtonPlayerDiscover by Settings.PLAYER_ACTION_DISCOVER
    var showButtonPlayerVideo by Settings.PLAYER_ACTION_TOGGLE_VIDEO
    var showTotalTimeQueue by Settings.PLAYER_SHOW_TOTAL_QUEUE_TIME
    var showNextSongsInPlayer by Settings.PLAYER_SHOW_NEXT_IN_QUEUE
    var showRemainingSongTime by Settings.PLAYER_SHOW_SONGS_REMAINING_TIME
    var thumbnailRoundness by Settings.THUMBNAIL_BORDER_RADIUS
    var playerBackgroundColors by Settings.PLAYER_BACKGROUND
    var showTopActionsBar by Settings.PLAYER_SHOW_TOP_ACTIONS_BAR
    var playerControlsType by Settings.PLAYER_CONTROLS_TYPE
    var playerInfoType by Settings.PLAYER_INFO_TYPE
    var transparentBackgroundActionBarPlayer by Settings.PLAYER_TRANSPARENT_ACTIONS_BAR
    var actionspacedevenly by Settings.PLAYER_ACTION_BUTTONS_SPACED_EVENLY
    var thumbnailType by Settings.THUMBNAIL_TYPE
    var playerType by Settings.PLAYER_TYPE
    var noblur by Settings.PLAYER_BACKGROUND_BLUR
    var fadingedge by Settings.PLAYER_BACKGROUND_FADING_EDGE
    var carouselSize by Settings.CAROUSEL_SIZE
    var playerInfoShowIcons by Settings.PLAYER_SONG_INFO_ICON
    var showCoverThumbnailAnimation by Settings.PLAYER_THUMBNAIL_ANIMATION
    var topPadding by Settings.PLAYER_TOP_PADDING
    var animatedGradient by Settings.ANIMATED_GRADIENT
    var blurStrength by Settings.PLAYER_BACKGROUND_BLUR_STRENGTH
    var thumbnailFadeEx  by Settings.PLAYER_THUMBNAIL_FADE_EX
    var thumbnailFade  by Settings.PLAYER_THUMBNAIL_FADE
    var thumbnailSpacing  by Settings.PLAYER_THUMBNAIL_SPACING
    var colorPaletteName by Settings.COLOR_PALETTE
    var colorPaletteMode by Settings.THEME_MODE

    var appearanceChooser by remember{ mutableStateOf(false)}
    if (appearanceChooser) {
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

        if ( search.contains( R.string.show_player_top_actions_bar ) )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_SHOW_TOP_ACTIONS_BAR,
                titleId = R.string.show_player_top_actions_bar
            )

        if( !showTopActionsBar && search.contains( R.string.blankspace ) )
            SettingComponents.BooleanEntry(
                Settings.PLAYER_TOP_PADDING,
                R.string.blankspace
            )
    }
    if ( search.contains( R.string.playertype ) )
        SettingComponents.EnumEntry(
            Settings.PLAYER_TYPE,
            R.string.playertype
        )

    if ( search.contains( R.string.queuetype ) )
        SettingComponents.EnumEntry(
            Settings.QUEUE_TYPE,
            R.string.queuetype
        )

    if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor
        && search.contains( R.string.show_thumbnail )
    )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_SHOW_THUMBNAIL,
            R.string.show_thumbnail
        )
    AnimatedVisibility(visible = !showthumbnail && playerType == PlayerType.Modern && !isLandscape) {
        if ( search.contains( R.string.swipe_Animation_No_Thumbnail ) )
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
            if ( playerType == PlayerType.Modern && search.contains( R.string.fadingedge ) )
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

            if (playerType == PlayerType.Modern && !isLandscape && (expandedplayertoggle || expandedplayer)) {
                if ( search.contains( R.string.carousel ) )
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

                if ( search.contains( R.string.carouselsize ) )
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
                if ( search.contains( R.string.thumbnailpause ) )
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
                if ( search.contains( R.string.show_lyrics_thumbnail ) )
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
                if ( visualizerEnabled && search.contains( R.string.showvisthumbnail ) )
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

            if ( search.contains( R.string.show_cover_thumbnail_animation ) ) {
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
                if ( search.contains( R.string.player_thumbnail_size ) )
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
                if ( search.contains( R.string.player_thumbnail_size ) )
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
            if ( search.contains( R.string.thumbnailtype ) )
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

            if ( search.contains( R.string.thumbnail_roundness ) )
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

    if ( !showthumbnail && search.contains( R.string.noblur ) )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_BACKGROUND_BLUR,
            R.string.noblur
        )

    if (!(showthumbnail && playerType == PlayerType.Essential)
        && search.contains( R.string.statsfornerdsplayer )
    )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_STATS_FOR_NERDS,
            R.string.statsfornerdsplayer
        )

    if ( search.contains( R.string.timelinesize ) )
        SettingComponents.EnumEntry(
            Settings.PLAYER_TIMELINE_SIZE,
            R.string.timelinesize
        )

    if ( search.contains( R.string.pinfo_type ) ) {
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
    if ( search.contains( R.string.miniplayertype ) )
        SettingComponents.EnumEntry(
            Settings.MINI_PLAYER_TYPE,
            R.string.miniplayertype
        )
    if ( search.contains( R.string.player_swap_controls_with_timeline ) )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_IS_CONTROL_AND_TIMELINE_SWAPPED,
            R.string.player_swap_controls_with_timeline
        )
    if ( search.contains( R.string.timeline ) )
        SettingComponents.EnumEntry(
            Settings.PLAYER_TIMELINE_TYPE,
            R.string.timeline
        )
    if ( search.contains( R.string.transparentbar ) )
        SettingComponents.BooleanEntry(
            Settings.TRANSPARENT_TIMELINE,
            R.string.transparentbar
        )
    if ( search.contains( R.string.pcontrols_type ) )
        SettingComponents.EnumEntry(
            Settings.PLAYER_CONTROLS_TYPE,
            R.string.pcontrols_type
        )
    if ( search.contains( R.string.play_button ) )
        SettingComponents.EnumEntry(
            Settings.PLAYER_PLAY_BUTTON_TYPE,
            R.string.play_button
        )
    if ( search.contains( R.string.buttonzoomout ) )
        SettingComponents.BooleanEntry(
            Settings.ZOOM_OUT_ANIMATION,
            R.string.buttonzoomout
        )
    if ( search.contains( R.string.play_button ) )
        SettingComponents.EnumEntry(
            Settings.LIKE_ICON,
            R.string.play_button
        )
    if ( search.contains( R.string.background_colors ) )
        SettingComponents.EnumEntry(
            Settings.PLAYER_BACKGROUND,
            R.string.background_colors
        )

    AnimatedVisibility(visible = playerBackgroundColors == PlayerBackgroundColors.AnimatedGradient) {
        if ( search.contains( R.string.gradienttype ) )
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
       Column {
           if( search.contains( R.string.rotating_cover_title ) )
               SettingComponents.BooleanEntry(
                   Settings.PLAYER_ROTATING_ALBUM_COVER,
                   R.string.rotating_cover_title,
                   Modifier.padding( start = 25.dp )
               )

           if ( search.contains( R.string.bottomgradient ) )
               SettingComponents.BooleanEntry(
                   Settings.PLAYER_BOTTOM_GRADIENT,
                   R.string.bottomgradient,
                   Modifier.padding( start = 25.dp )
               )

           if ( playerType == PlayerType.Modern && search.contains( R.string.albumCoverRotation ) )
               SettingComponents.BooleanEntry(
                   Settings.PLAYER_THUMBNAIL_ROTATION,
                   R.string.albumCoverRotation,
                   Modifier.padding( start = 25.dp )
               )
       }
    }
    AnimatedVisibility( playerBackgroundColors == PlayerBackgroundColors.CoverColorGradient ) {
        if( playerBackgroundColors == PlayerBackgroundColors.ThemeColorGradient && search.contains( R.string.blackgradient ) )
            SettingComponents.BooleanEntry(
                Settings.BLACK_GRADIENT,
                R.string.blackgradient
            )
    }

    if ( search.contains( R.string.textoutline ) )
        SettingComponents.BooleanEntry(
            Settings.TEXT_OUTLINE,
            R.string.textoutline
        )

    if ( search.contains( R.string.show_total_time_of_queue ) )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_SHOW_TOTAL_QUEUE_TIME,
            R.string.show_total_time_of_queue
        )

    if ( search.contains( R.string.show_remaining_song_time ) )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_SHOW_SONGS_REMAINING_TIME,
            R.string.show_remaining_song_time
        )

    if ( search.contains( R.string.show_next_songs_in_player ) )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_SHOW_NEXT_IN_QUEUE,
            R.string.show_next_songs_in_player
        )
    AnimatedVisibility( visible = showNextSongsInPlayer) {
        Column {
            if( search.contains( R.string.showtwosongs ) )
                SettingComponents.EnumEntry(
                    Settings.MAX_NUMBER_OF_NEXT_IN_QUEUE,
                    R.string.songs_number_to_show,
                    Modifier.padding( start = 25.dp )
                )

            if ( search.contains( R.string.showalbumcover ) )
                SettingComponents.BooleanEntry(
                    Settings.PLAYER_SHOW_NEXT_IN_QUEUE_THUMBNAIL,
                    R.string.showalbumcover
                )
        }
    }

    if ( search.contains( R.string.disable_scrolling_text ) )
        SettingComponents.BooleanEntry(
            Settings.SCROLLING_TEXT_DISABLED,
            R.string.disable_scrolling_text,
            R.string.scrolling_text_is_used_for_long_texts
        )

    val swipeDirection = if (playerType == PlayerType.Modern && !isLandscape)
        R.string.disable_horizontal_swipe
    else
        R.string.disable_vertical_swipe
    if ( search.contains( swipeDirection ) ) {
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

    if ( search.contains( R.string.player_rotating_buttons ) )
        SettingComponents.BooleanEntry(
            Settings.ROTATION_EFFECT,
            R.string.player_rotating_buttons,
            R.string.player_enable_rotation_buttons
        )

    if ( search.contains( R.string.toggle_lyrics ) )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_TAP_THUMBNAIL_FOR_LYRICS,
            R.string.toggle_lyrics,
            R.string.by_tapping_on_the_thumbnail
        )

    if ( search.contains( R.string.click_lyrics_text ) )
        SettingComponents.BooleanEntry(
            Settings.LYRICS_JUMP_ON_TAP,
            R.string.click_lyrics_text
        )
    if ( showlyricsthumbnail && search.contains( R.string.show_background_in_lyrics ) )
        SettingComponents.BooleanEntry(
            Settings.LYRICS_SHOW_ACCENT_BACKGROUND,
            R.string.show_background_in_lyrics
        )

    if ( search.contains( R.string.player_enable_lyrics_popup_message ) )
        SettingComponents.BooleanEntry(
            Settings.PLAYER_ACTION_LYRICS_POPUP_MESSAGE,
            R.string.player_enable_lyrics_popup_message
        )

    if ( search.contains( R.string.background_progress_bar ) )
        SettingComponents.EnumEntry(
            Settings.MINI_PLAYER_PROGRESS_BAR,
            R.string.background_progress_bar
        )

    if ( search.contains( R.string.visualizer ) ) {
        SettingComponents.BooleanEntry(
            Settings.PLAYER_VISUALIZER,
            R.string.visualizer
        )

        SettingComponents.Description( R.string.visualizer_require_mic_permission, isImportant = true )
    }
}