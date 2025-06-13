package app.kreate.android.themed.common.screens.settings.general

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.section
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.ui.screens.settings.EnumValueSelectorSettingsEntry
import it.fast4x.rimusic.ui.screens.settings.ImportantSettingsDescription
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.languageDestinationName
import me.knighthat.component.dialog.RestartAppDialog
import me.knighthat.updater.Updater

@UnstableApi
@Composable
fun GeneralSettings( navController: NavController ) {
    val scrollState = rememberLazyListState()

    val search = remember {
        SettingEntrySearch( scrollState, R.string.tab_general, R.drawable.app_icon_monochrome )
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

        val sysLocale = LocaleListCompat.getDefault()[0].toString()
        val sysLocaleText = "${stringResource( R.string.system_language )}: $sysLocale"

        LazyColumn( state = scrollState ) {

            section( R.string.update ) {
                if( search.contains( R.string.update ) )
                    Updater.SettingEntry()
            }

            section( R.string.languages, sysLocaleText ) {
                var languageApp by Settings.APP_LANGUAGE

                if( search.contains( R.string.app_language ) )
                    EnumValueSelectorSettingsEntry(
                        title = stringResource(R.string.app_language),
                        selectedValue = languageApp,
                        onValueSelected = {
                            languageApp = it

                            RestartAppDialog.showDialog()
                        },
                        valueText = {
                            languageDestinationName(it)
                        }
                    )
            }

            section( R.string.notification_type, R.string.notification_type_info ) {
                ImportantSettingsDescription( stringResource(R.string.restarting_rimusic_is_required) )

                var notificationType by Settings.NOTIFICATION_TYPE
                if( search.contains( R.string.notification_type ) )
                    EnumValueSelectorSettingsEntry(
                        title = stringResource( R.string.notification_type ),
                        selectedValue = notificationType,
                        onValueSelected = {
                            notificationType = it
                        },
                        valueText = { it.textName }
                    )
            }

            section( R.string.player ) {
                PlayerSettings( search, restartService, onRestartServiceChange )
            }
        }
    }
}