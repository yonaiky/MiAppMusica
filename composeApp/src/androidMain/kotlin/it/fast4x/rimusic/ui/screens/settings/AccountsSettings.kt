package it.fast4x.rimusic.ui.screens.settings

import app.kreate.android.Preferences
import app.kreate.android.service.innertube.InnertubeProvider

fun isYouTubeSyncEnabled(): Boolean {
    return isYouTubeLoggedIn() && Preferences.YOUTUBE_PLAYLISTS_SYNC.value
}

fun isYouTubeLoggedIn(): Boolean =
    Preferences.YOUTUBE_LOGIN.value && InnertubeProvider.COOKIE_MAP.containsKey( "SAPISID" )





