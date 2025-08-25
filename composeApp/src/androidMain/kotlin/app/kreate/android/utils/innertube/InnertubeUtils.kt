package app.kreate.android.utils.innertube

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import app.kreate.android.Preferences
import it.fast4x.rimusic.utils.isAtLeastAndroid6

object InnertubeUtils {

    val isLoggedIn: Boolean by derivedStateOf {
        isAtLeastAndroid6 && Preferences.YOUTUBE_LOGIN.value && Preferences.YOUTUBE_SYNC_ID.value.isNotBlank()
    }
}