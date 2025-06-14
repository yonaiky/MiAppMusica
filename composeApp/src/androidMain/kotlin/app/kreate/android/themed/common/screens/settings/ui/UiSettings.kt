package app.kreate.android.themed.common.screens.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.section
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.BackgroundProgress
import it.fast4x.rimusic.enums.CarouselSize
import it.fast4x.rimusic.enums.ColorPaletteMode
import it.fast4x.rimusic.enums.ColorPaletteName
import it.fast4x.rimusic.enums.IconLikeType
import it.fast4x.rimusic.enums.MiniPlayerType
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PlayerBackgroundColors
import it.fast4x.rimusic.enums.PlayerControlsType
import it.fast4x.rimusic.enums.PlayerPlayButtonType
import it.fast4x.rimusic.enums.PlayerThumbnailSize
import it.fast4x.rimusic.enums.PlayerTimelineSize
import it.fast4x.rimusic.enums.PlayerTimelineType
import it.fast4x.rimusic.enums.PlayerType
import it.fast4x.rimusic.enums.QueueType
import it.fast4x.rimusic.enums.ThumbnailType
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.ui.styling.Dimensions

@Composable
fun UiSettings() {
    val scrollState = rememberLazyListState()

    val search = remember {
        SettingEntrySearch( scrollState, R.string.user_interface, R.drawable.ui )
    }
    val paddingValues =
        if( UiType.ViMusic.isCurrent() )
            WindowInsets.statusBars.asPaddingValues()
        else
            PaddingValues()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background( colorPalette().background0 )
                           .padding( paddingValues )
                           .fillMaxHeight()
                           .fillMaxWidth(
                               if ( NavigationBarPosition.Right.isCurrent() )
                                   Dimensions.contentWidthRightBar
                               else
                                   1f
                           )
    ) {
        search.ToolBarButton()

        LazyColumn( state = scrollState ) {

            section( R.string.user_interface ) {
                var disablePlayerHorizontalSwipe by Settings.PLAYER_THUMBNAIL_HORIZONTAL_SWIPE_DISABLED
                var playerTimelineType by Settings.PLAYER_TIMELINE_TYPE
                var visualizerEnabled by Settings.PLAYER_VISUALIZER
                var playerThumbnailSize by Settings.PLAYER_PORTRAIT_THUMBNAIL_SIZE
                var thumbnailTapEnabled by Settings.PLAYER_TAP_THUMBNAIL_FOR_LYRICS
                var showSearchTab by Settings.SHOW_SEARCH_IN_NAVIGATION_BAR
                var showStatsInNavbar by Settings.SHOW_STATS_IN_NAVIGATION_BAR
                var navigationBarPosition by Settings.NAVIGATION_BAR_POSITION
                var showTopActionsBar by Settings.PLAYER_SHOW_TOP_ACTIONS_BAR
                var playerType by Settings.PLAYER_TYPE
                var queueType by Settings.QUEUE_TYPE
                var fadingedge by Settings.PLAYER_BACKGROUND_FADING_EDGE
                var carousel by Settings.PLAYER_THUMBNAILS_CAROUSEL
                var carouselSize by Settings.CAROUSEL_SIZE
                var thumbnailType by Settings.THUMBNAIL_TYPE
                var playerTimelineSize by Settings.PLAYER_TIMELINE_SIZE
                var playerInfoShowIcons by Settings.PLAYER_SONG_INFO_ICON
                var miniPlayerType by Settings.MINI_PLAYER_TYPE
                var playerSwapControlsWithTimeline by Settings.PLAYER_IS_CONTROL_AND_TIMELINE_SWAPPED
                var transparentBackgroundActionBarPlayer by Settings.PLAYER_TRANSPARENT_ACTIONS_BAR
                var playerControlsType by Settings.PLAYER_CONTROLS_TYPE
                var playerPlayButtonType by Settings.PLAYER_PLAY_BUTTON_TYPE
                var buttonzoomout by Settings.ZOOM_OUT_ANIMATION
                var iconLikeType by Settings.LIKE_ICON
                var playerBackgroundColors by Settings.PLAYER_BACKGROUND
                var blackgradient by Settings.BLACK_GRADIENT
                var showTotalTimeQueue by Settings.PLAYER_SHOW_TOTAL_QUEUE_TIME
                var showRemainingSongTime by Settings.PLAYER_SHOW_SONGS_REMAINING_TIME
                var showNextSongsInPlayer by Settings.PLAYER_SHOW_NEXT_IN_QUEUE
                var disableScrollingText by Settings.SCROLLING_TEXT_DISABLED
                var effectRotationEnabled by Settings.ROTATION_EFFECT
                var clickLyricsText by Settings.LYRICS_JUMP_ON_TAP
                var playerEnableLyricsPopupMessage by Settings.PLAYER_ACTION_LYRICS_POPUP_MESSAGE
                var backgroundProgress by Settings.MINI_PLAYER_PROGRESS_BAR
                var actionspacedevenly by Settings.PLAYER_ACTION_BUTTONS_SPACED_EVENLY
                var tapqueue by Settings.PLAYER_ACTIONS_BAR_TAP_TO_OPEN_QUEUE
                var swipeUpQueue by Settings.PLAYER_ACTIONS_BAR_SWIPE_UP_TO_OPEN_QUEUE
                var showButtonPlayerDiscover by Settings.PLAYER_ACTION_DISCOVER
                var showButtonPlayerDownload by Settings.PLAYER_ACTION_DOWNLOAD
                var showButtonPlayerAddToPlaylist by Settings.PLAYER_ACTION_ADD_TO_PLAYLIST
                var showButtonPlayerLoop by Settings.PLAYER_ACTION_LOOP
                var showButtonPlayerShuffle by Settings.PLAYER_ACTION_SHUFFLE
                var showButtonPlayerLyrics by Settings.PLAYER_ACTION_SHOW_LYRICS
                var expandedplayertoggle by Settings.PLAYER_ACTION_TOGGLE_EXPAND
                var showButtonPlayerSleepTimer by Settings.PLAYER_ACTION_SLEEP_TIMER
                var showButtonPlayerSystemEqualizer by Settings.PLAYER_ACTION_OPEN_EQUALIZER
                var showButtonPlayerArrow by Settings.PLAYER_ACTION_OPEN_QUEUE_ARROW
                var showButtonPlayerMenu by Settings.PLAYER_ACTION_SHOW_MENU
                var showthumbnail by Settings.PLAYER_SHOW_THUMBNAIL
                var keepPlayerMinimized by Settings.PLAYER_KEEP_MINIMIZED

                if( search.contains( R.string.interface_in_use ) )
                    SettingComponents.EnumEntry(
                        Settings.MAIN_THEME,
                        R.string.interface_in_use,
                        action = SettingComponents.Action.RESTART_APP
                    ) {
                        if ( it == UiType.ViMusic ) {
                            disablePlayerHorizontalSwipe = true
                            playerTimelineType = PlayerTimelineType.FakeAudioBar
                            visualizerEnabled = false
                            playerThumbnailSize = PlayerThumbnailSize.Medium
                            thumbnailTapEnabled = true
                            showSearchTab = true
                            showStatsInNavbar = true
                            navigationBarPosition = NavigationBarPosition.Left
                            showTopActionsBar = false
                            playerType = PlayerType.Modern
                            queueType = QueueType.Modern
                            fadingedge = false
                            carousel = true
                            carouselSize = CarouselSize.Medium
                            thumbnailType = ThumbnailType.Essential
                            playerTimelineSize = PlayerTimelineSize.Medium
                            playerInfoShowIcons = true
                            miniPlayerType = MiniPlayerType.Modern
                            playerSwapControlsWithTimeline = false
                            transparentBackgroundActionBarPlayer = false
                            playerControlsType = PlayerControlsType.Essential
                            playerPlayButtonType = PlayerPlayButtonType.Disabled
                            buttonzoomout = true
                            iconLikeType = IconLikeType.Essential
                            playerBackgroundColors = PlayerBackgroundColors.CoverColorGradient
                            blackgradient = true
                            showTotalTimeQueue = false
                            showRemainingSongTime = false
                            showNextSongsInPlayer = false
                            disableScrollingText = false
                            effectRotationEnabled = true
                            clickLyricsText = true
                            playerEnableLyricsPopupMessage = true
                            backgroundProgress = BackgroundProgress.MiniPlayer
                            transparentBackgroundActionBarPlayer = true
                            actionspacedevenly = false
                            tapqueue = false
                            swipeUpQueue = true
                            showButtonPlayerDiscover = false
                            showButtonPlayerDownload = false
                            showButtonPlayerAddToPlaylist = false
                            showButtonPlayerLoop = false
                            showButtonPlayerShuffle = false
                            showButtonPlayerLyrics = false
                            expandedplayertoggle = false
                            showButtonPlayerSleepTimer = false
                            showButtonPlayerSystemEqualizer = false
                            showButtonPlayerArrow = false
                            showButtonPlayerShuffle = false
                            showButtonPlayerMenu = true
                            showthumbnail = true
                            keepPlayerMinimized = false
                        } else {
                            disablePlayerHorizontalSwipe = false
                            navigationBarPosition = NavigationBarPosition.Bottom
                        }
                    }

                if( search.contains( R.string.theme ) ) {
                    ThemeSettings()
                }
                if( search.contains( R.string.theme_mode ) )
                    SettingComponents.EnumEntry(
                        preference = Settings.COLOR_PALETTE,
                        titleId = R.string.theme,
                        onValueChanged = {
                            when (it) {
                                ColorPaletteName.PureBlack,
                                ColorPaletteName.ModernBlack -> Settings.THEME_MODE.value = ColorPaletteMode.System
                                else -> { /* Does nothing */ }
                            }
                        }
                    )
                if( search.contains( R.string.navigation_bar_position ) )
                    SettingComponents.EnumEntry(
                        Settings.NAVIGATION_BAR_POSITION,
                        R.string.navigation_bar_position
                    )
                if( search.contains( R.string.navigation_bar_type ) )
                    SettingComponents.EnumEntry(
                        Settings.NAVIGATION_BAR_TYPE,
                        R.string.navigation_bar_type
                    )
                if( search.contains( R.string.player_position ) )
                    SettingComponents.EnumEntry(
                        Settings.MINI_PLAYER_POSITION,
                        R.string.player_position
                    )
                if( search.contains( R.string.menu_style ) )
                    SettingComponents.EnumEntry(
                        Settings.MENU_STYLE,
                        R.string.menu_style
                    )
                if( search.contains( R.string.default_page ) )
                    SettingComponents.EnumEntry(
                        Settings.STARTUP_SCREEN,
                        R.string.default_page
                    )
                if( search.contains( R.string.transition_effect ) )
                    SettingComponents.EnumEntry(
                        Settings.TRANSITION_EFFECT,
                        R.string.transition_effect
                    )
                if( UiType.ViMusic.isCurrent() && search.contains( R.string.vimusic_show_search_button_in_navigation_bar ) )
                    SettingComponents.BooleanEntry(
                        Settings.SHOW_SEARCH_IN_NAVIGATION_BAR,
                        R.string.vimusic_show_search_button_in_navigation_bar,
                        R.string.vismusic_only_in_left_right_navigation_bar
                    )
                if( UiType.ViMusic.isCurrent() && search.contains( R.string.show_statistics_in_navigation_bar ) )
                    SettingComponents.BooleanEntry(
                        Settings.SHOW_STATS_IN_NAVIGATION_BAR,
                        R.string.show_statistics_in_navigation_bar
                    )
                if( search.contains( R.string.show_floating_icon ) )
                    SettingComponents.BooleanEntry(
                        Settings.SHOW_FLOATING_ICON,
                        R.string.show_floating_icon
                    )
                if( search.contains( R.string.settings_use_font_type ) )
                    SettingComponents.EnumEntry(
                        Settings.FONT,
                        R.string.settings_use_font_type
                    )
                if( search.contains( R.string.use_system_font ) )
                    SettingComponents.BooleanEntry(
                        Settings.USE_SYSTEM_FONT,
                        R.string.use_system_font,
                        R.string.use_font_by_the_system
                    )
                if( search.contains( R.string.apply_font_padding ) ) {
                    SettingComponents.BooleanEntry(
                        Settings.APPLY_FONT_PADDING,
                        R.string.apply_font_padding,
                        R.string.add_spacing_around_texts
                    )
                }
                if( search.contains( R.string.swipe_to_action ) ) {
                    SwipeActionSettings()
                }
            }
            section( R.string.songs ) {
                val showFavorites = stringResource( R.string.setting_entry_show_chip, stringResource( R.string.favorites ) )
                if( search.contains( showFavorites ) )
                    SettingComponents.BooleanEntry(
                        Settings.HOME_SONGS_SHOW_FAVORITES_CHIP,
                        showFavorites
                    )

                val showCached = stringResource( R.string.setting_entry_show_chip, stringResource( R.string.cached ) )
                if( search.contains( showCached ) )
                    SettingComponents.BooleanEntry(
                        Settings.HOME_SONGS_SHOW_CACHED_CHIP,
                        showCached
                    )

                val showDownloaded = stringResource( R.string.setting_entry_show_chip, stringResource( R.string.downloaded ) )
                if ( search.contains( showDownloaded ) )
                    SettingComponents.BooleanEntry(
                        Settings.HOME_SONGS_SHOW_DOWNLOADED_CHIP,
                        showDownloaded
                    )

                val topNumber = stringResource( R.string.my_playlist_top, Settings.MAX_NUMBER_OF_TOP_PLAYED.value )
                val showMostPlayed = stringResource( R.string.setting_entry_show_chip, topNumber )
                if ( search.contains( showMostPlayed ) )
                    SettingComponents.BooleanEntry(
                        Settings.HOME_SONGS_SHOW_MOST_PLAYED_CHIP,
                        showMostPlayed
                    )

                val showOnDevice = stringResource( R.string.setting_entry_show_chip, stringResource( R.string.on_device ) )
                if ( search.contains( showOnDevice ) )
                    SettingComponents.BooleanEntry(
                        Settings.HOME_SONGS_SHOW_ON_DEVICE_CHIP,
                        showOnDevice
                    )
            }
            section( R.string.playlists ) {
                val showPiped = stringResource( R.string.setting_entry_show_chip, stringResource( R.string.piped_playlists ) )
                if ( search.contains( showPiped ) )
                    SettingComponents.BooleanEntry(
                        Settings.SHOW_PIPED_PLAYLISTS,
                        showPiped
                    )

                val showPinned = stringResource( R.string.setting_entry_show_chip, stringResource( R.string.pinned_playlists ) )
                if ( search.contains( showPinned ) )
                    SettingComponents.BooleanEntry(
                        Settings.SHOW_PINNED_PLAYLISTS,
                        showPinned
                    )

                val showMonthly = stringResource( R.string.setting_entry_show_chip, stringResource( R.string.monthly_playlists ) )
                if ( search.contains( showMonthly ) )
                    SettingComponents.BooleanEntry(
                        Settings.SHOW_MONTHLY_PLAYLISTS,
                        showMonthly
                    )
            }
            section( R.string.smart_recommendations ) {
                if( search.contains( R.string.statistics_max_number_of_items ) )
                    SettingComponents.EnumEntry(
                        Settings.MAX_NUMBER_OF_SMART_RECOMMENDATIONS,
                        R.string.statistics_max_number_of_items
                    )
            }
            section( R.string.statistics ) {
                if( search.contains( R.string.statistics_max_number_of_items ) )
                    SettingComponents.EnumEntry(
                        Settings.MAX_NUMBER_OF_STATISTIC_ITEMS,
                        R.string.statistics_max_number_of_items
                    )
                if( search.contains( R.string.listening_time ) )
                    SettingComponents.BooleanEntry(
                        Settings.SHOW_LISTENING_STATS,
                        R.string.listening_time,
                        R.string.shows_the_number_of_songs_heard_and_their_listening_time
                    )
            }
            section( R.string.playlist_top ) {
                if( search.contains( R.string.statistics_max_number_of_items ) )
                    SettingComponents.EnumEntry(
                        Settings.MAX_NUMBER_OF_TOP_PLAYED,
                        R.string.statistics_max_number_of_items
                    )
            }
        }
    }
}