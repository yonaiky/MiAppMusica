package app.kreate.android.themed.common.screens.settings

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.section
import app.kreate.android.themed.common.screens.settings.player.PlayerActionBar
import app.kreate.android.themed.common.screens.settings.player.PlayerAppearance
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.RestartPlayerService
import it.fast4x.rimusic.utils.isAtLeastAndroid7

@Composable
fun AppearanceSettings() {
    val scrollState = rememberLazyListState()

    val search = remember {
        SettingEntrySearch( scrollState, R.string.player_appearance, R.drawable.color_palette )
    }
    val paddingValues =
        if( UiType.ViMusic.isCurrent() )
            WindowInsets.statusBars.asPaddingValues()
        else
            PaddingValues()
    val (restartService, onRestartServiceChange) = rememberSaveable { mutableStateOf( false ) }

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
            section( R.string.player ) {
                PlayerAppearance( search )
            }
            section( R.string.player_action_bar ) {
                PlayerActionBar( search )
            }
            section( R.string.notification_player ) {
                if ( search.contains( R.string.notification_player ) ) {
                    SettingComponents.EnumEntry(
                        Settings.MEDIA_NOTIFICATION_FIRST_ICON,
                        R.string.notificationPlayerFirstIcon,
                        action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                    ) { onRestartServiceChange( true ) }
                    SettingComponents.EnumEntry(
                        Settings.MEDIA_NOTIFICATION_SECOND_ICON,
                        R.string.notificationPlayerSecondIcon,
                        action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                    ) { onRestartServiceChange( true ) }

                    RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) })
                }
            }
            if( isAtLeastAndroid7 )
                section( R.string.wallpaper ) {
                    SettingComponents.BooleanEntry(
                        Settings.ENABLE_WALLPAPER,
                        R.string.enable_wallpaper
                    )
                    AnimatedVisibility( Settings.ENABLE_WALLPAPER.value ) {
                        Column {
                            SettingComponents.EnumEntry(
                                Settings.WALLPAPER_TYPE,
                                R.string.set_cover_thumbnail_as_wallpaper,
                                Modifier.padding( start = 25.dp ),
                                action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                            ) { onRestartServiceChange( true ) }
                            RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) })
                        }
                    }
                }
        }
    }
}