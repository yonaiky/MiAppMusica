package app.kreate.android.themed.common.screens.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import app.kreate.android.Preferences
import app.kreate.android.themed.common.component.settings.SettingComponents

@Composable
fun SwipeActionSettings() {
    SettingComponents.BooleanEntry(
        Preferences.ENABLE_SWIPE_ACTION,
        R.string.swipe_to_action,
        R.string.activate_the_action_menu_by_swiping_the_song_left_or_right
    )

    AnimatedVisibility( Preferences.ENABLE_SWIPE_ACTION.value ) {
        Column(
            modifier = Modifier.padding(start = 25.dp)
        ) {
            SettingComponents.EnumEntry(
                Preferences.QUEUE_SWIPE_LEFT_ACTION,
                R.string.queue_and_local_playlists_left_swipe
            )
            SettingComponents.EnumEntry(
                Preferences.QUEUE_SWIPE_RIGHT_ACTION,
                R.string.queue_and_local_playlists_right_swipe
            )
            SettingComponents.EnumEntry(
                Preferences.PLAYLIST_SWIPE_LEFT_ACTION,
                R.string.playlist_left_swipe
            )
            SettingComponents.EnumEntry(
                Preferences.PLAYLIST_SWIPE_RIGHT_ACTION,
                R.string.playlist_right_swipe
            )
            SettingComponents.EnumEntry(
                Preferences.ALBUM_SWIPE_LEFT_ACTION,
                R.string.album_left_swipe
            )
            SettingComponents.EnumEntry(
                Preferences.ALBUM_SWIPE_RIGHT_ACTION,
                R.string.album_right_swipe
            )
        }
    }
}