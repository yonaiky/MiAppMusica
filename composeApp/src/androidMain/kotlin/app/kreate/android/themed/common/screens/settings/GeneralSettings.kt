package app.kreate.android.themed.common.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.media3.common.util.UnstableApi
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.section
import app.kreate.android.themed.common.screens.settings.general.PlayerSettings
import app.kreate.android.themed.common.screens.settings.general.updateSection
import app.kreate.android.utils.innertube.getAppLanguageCode
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.ui.styling.Dimensions
import me.knighthat.utils.Toaster
import java.util.Locale

@UnstableApi
@Composable
fun GeneralSettings( paddingValues: PaddingValues ) {
    val scrollState = rememberLazyListState()

    val search = remember {
        SettingEntrySearch( scrollState, R.string.tab_general, R.drawable.app_icon_monochrome )
    }
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

        val sysLocaleText = stringResource( R.string.currently_selected, Preferences.APP_LANGUAGE.value.text )

        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(bottom = Dimensions.bottomSpacer)
        ) {
            updateSection( search )

            section( R.string.languages, sysLocaleText ) {
                if( search appearsIn R.string.app_language )
                    SettingComponents.EnumEntry(
                        Preferences.APP_LANGUAGE,
                        titleId = R.string.app_language,
                        subtitleId = R.string.setting_description_app_language,
                        onValueChanged = {
                            try {
                                // Apply it first before really selecting it
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags( it.code )
                                )

                                Preferences.APP_LANGUAGE.value = it
                            } catch (err: Exception) {
                                err.printStackTrace()
                                err.message?.also( Toaster::e )
                            }
                        }
                    )

                if( search appearsIn R.string.setting_entry_app_region ) {
                    SettingComponents.ListEntry(
                        preference = Preferences.APP_REGION,
                        title = stringResource( R.string.setting_entry_app_region ),
                        getName = {
                            val locale = Locale(getAppLanguageCode(), it)
                            locale.getDisplayCountry( locale )
                        },
                        getList = { Locale.getISOCountries().toList() },
                        subtitle = stringResource( R.string.setting_description_app_region )
                    )
                }
            }

            section( R.string.notification_type, R.string.notification_type_info ) {
                if( search appearsIn R.string.notification_type )
                    SettingComponents.EnumEntry(
                        Preferences.NOTIFICATION_TYPE,
                        R.string.notification_type,
                        action = SettingComponents.Action.RESTART_APP
                    )
            }

            section( R.string.player ) {
                PlayerSettings( search, restartService, onRestartServiceChange )
            }
        }
    }
}