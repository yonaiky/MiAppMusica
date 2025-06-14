package it.fast4x.rimusic.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingComponents
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.ui.components.themed.ConfirmationDialog
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.styling.Dimensions
import kotlinx.coroutines.Dispatchers
import me.knighthat.utils.Toaster

@ExperimentalAnimationApi
@UnstableApi
@Composable
fun  QuickPicsSettings() {
    var clearEvents by remember { mutableStateOf(false) }
    if (clearEvents) {
        ConfirmationDialog(
            text = stringResource(R.string.do_you_really_want_to_delete_all_playback_events),
            onDismiss = { clearEvents = false },
            onConfirm = {
                Database.asyncTransaction {
                    eventTable.deleteAll()
                    Toaster.done()
                }
            }
        )
    }

    //var isEnabledDiscoveryLangCode by rememberPreference(isEnabledDiscoveryLangCodeKey,   true)

    //var showActionsBar by rememberPreference(showActionsBarKey, true)
    Column(
        modifier = Modifier
            .background(colorPalette().background0)
            //.fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth(
                if( NavigationBarPosition.Right.isCurrent() )
                    Dimensions.contentWidthRightBar
                else
                    1f
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
            title = if (!isYouTubeLoggedIn()) stringResource(R.string.quick_picks) else stringResource(R.string.home),
            iconId = if (!isYouTubeLoggedIn()) R.drawable.sparkles else R.drawable.ytmusic,
            enabled = false,
            showIcon = true,
            modifier = Modifier,
            onClick = {}
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_PAGE,
            R.string.enable_quick_picks_page
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_TIPS,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.tips ) )
        )

        AnimatedVisibility(
            visible = Settings.QUICK_PICKS_SHOW_TIPS.value,
            enter = fadeIn(tween(100)),
            exit = fadeOut(tween(100)),
        ) {
            SettingComponents.EnumEntry( Settings.QUICK_PICKS_TYPE, R.string.tips )
        }

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_CHARTS,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.charts ) )
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_RELATED_ALBUMS,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.related_albums ) )
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_RELATED_ARTISTS,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.similar_artists ) )
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_NEW_ALBUMS_ARTISTS,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.new_albums_of_your_artists ) )
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_NEW_ALBUMS,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.new_albums ) )
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_MIGHT_LIKE_PLAYLISTS,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.playlists_you_might_like ) )
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_MOODS_AND_GENRES,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.moods_and_genres ) )
        )

        SettingComponents.BooleanEntry(
            Settings.QUICK_PICKS_SHOW_MONTHLY_PLAYLISTS,
            stringResource( R.string.entry_setting_quick_picks_show_section, stringResource( R.string.monthly_playlists ) )
        )

        val eventsCount by remember {
            Database.eventTable
                    .countAll()
        }.collectAsState( 0L, Dispatchers.IO )

        SettingsEntry(
            title = stringResource(R.string.reset_quick_picks),
            text = if (eventsCount > 0) {
                stringResource(R.string.delete_playback_events, eventsCount)
            } else {
                stringResource(R.string.quick_picks_are_cleared)
            },
            isEnabled = eventsCount > 0,
            onClick = { clearEvents = true }
        )
        SettingsGroupSpacer(
            modifier = Modifier.height(Dimensions.bottomSpacer)
        )
    }
}
