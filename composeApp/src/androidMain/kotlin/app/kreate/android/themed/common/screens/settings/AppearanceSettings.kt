package app.kreate.android.themed.common.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.animatedEntry
import app.kreate.android.themed.common.component.settings.entry
import app.kreate.android.themed.common.component.settings.header
import app.kreate.android.themed.common.screens.settings.player.playerActionBarSection
import app.kreate.android.themed.common.screens.settings.player.playerAppearanceSection
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.isAtLeastAndroid7
import it.fast4x.rimusic.utils.isLandscape

@Composable
fun AppearanceSettings( paddingValues: PaddingValues ) {
    val scrollState = rememberLazyListState()
    val isLandscapeMode = isLandscape

    val search = remember {
        SettingEntrySearch( scrollState, R.string.player_appearance, R.drawable.color_palette )
    }

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

        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(bottom = Dimensions.bottomSpacer)
        ) {
            playerAppearanceSection( search, isLandscapeMode )
            playerActionBarSection( search, isLandscapeMode )

            header( R.string.notification_player )
            entry( search, R.string.notificationPlayerFirstIcon ) {
                SettingComponents.EnumEntry(
                    Preferences.MEDIA_NOTIFICATION_FIRST_ICON,
                    R.string.notificationPlayerFirstIcon,
                    action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                )
            }
            entry( search, R.string.notificationPlayerSecondIcon ) {
                SettingComponents.EnumEntry(
                    Preferences.MEDIA_NOTIFICATION_SECOND_ICON,
                    R.string.notificationPlayerSecondIcon,
                    action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                )
            }

            if( isAtLeastAndroid7 ) {
                header( R.string.wallpaper )
                entry( search, R.string.enable_wallpaper ) {
                    SettingComponents.BooleanEntry(
                        Preferences.ENABLE_WALLPAPER,
                        R.string.enable_wallpaper
                    )
                }
                animatedEntry(
                    key = "wallpaperChildren",
                    visible = Preferences.ENABLE_WALLPAPER.value,
                    modifier = Modifier.padding( start = 25.dp )
                ) {
                    if( search appearsIn R.string.set_cover_thumbnail_as_wallpaper )
                        SettingComponents.EnumEntry(
                            Preferences.WALLPAPER_TYPE,
                            R.string.set_cover_thumbnail_as_wallpaper,
                            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                        )
                }
            }
        }
    }
}