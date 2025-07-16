package app.kreate.android.themed.common.screens.settings.general

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.kreate.android.BuildConfig
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.section
import it.fast4x.rimusic.enums.CheckUpdateState
import it.fast4x.rimusic.ui.components.themed.SecondaryTextButton
import me.knighthat.updater.Updater


fun LazyListScope.updateSection( search: SettingEntrySearch ) = section(R.string.update) {
    if ( !(search appearsIn R.string.update) ) return@section

    var checkUpdateState by Preferences.CHECK_UPDATE

    SettingComponents.EnumEntry(
        preference = Preferences.CHECK_UPDATE,
        titleId = R.string.setting_entry_update_checker,
        subtitle = stringResource( checkUpdateState.subtitleId, BuildConfig.APP_NAME ),
        trailingContent = {
            AnimatedVisibility(
                visible = checkUpdateState == CheckUpdateState.ASK,
                // Slide in from right + fade in effect.
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(initialAlpha = 0f),
                // Slide out from left + fade out effect.
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(targetAlpha = 0f)
            ) {
                SecondaryTextButton(
                    text = stringResource( R.string.info_check_update_now ),
                    onClick = { Updater.checkForUpdate( true ) }
                )
            }
        }
    )
}
