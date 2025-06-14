package it.fast4x.rimusic.ui.screens.settings

import androidx.compose.runtime.getValue
import app.kreate.android.Settings
import it.fast4x.innertube.utils.parseCookieString

fun isYouTubeLoginEnabled(): Boolean = Settings.YOUTUBE_LOGIN.value

fun isYouTubeSyncEnabled(): Boolean {
    val isYouTubeSyncEnabled by Settings.YOUTUBE_PLAYLISTS_SYNC
    return isYouTubeSyncEnabled && isYouTubeLoggedIn() && isYouTubeLoginEnabled()
}

fun isYouTubeLoggedIn(): Boolean {
    val cookie by Settings.YOUTUBE_COOKIES
    val isLoggedIn = cookie?.let { parseCookieString(it) }?.contains("SAPISID") == true
    return isLoggedIn
}





