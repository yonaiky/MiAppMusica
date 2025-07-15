package app.kreate.android.themed.common.screens.settings.general

import androidx.compose.foundation.lazy.LazyListScope
import app.kreate.android.R
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.section
import me.knighthat.updater.Updater


fun LazyListScope.updateSection( search: SettingEntrySearch ) = section(R.string.update) {
    if ( !(search appearsIn R.string.update) ) return@section


    Updater.SettingEntry()
}
