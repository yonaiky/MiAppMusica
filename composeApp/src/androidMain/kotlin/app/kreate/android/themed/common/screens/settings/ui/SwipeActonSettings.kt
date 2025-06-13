package app.kreate.android.themed.common.screens.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import app.kreate.android.Settings
import it.fast4x.rimusic.enums.AlbumSwipeAction
import it.fast4x.rimusic.enums.PlaylistSwipeAction
import it.fast4x.rimusic.enums.QueueSwipeAction
import it.fast4x.rimusic.ui.screens.settings.EnumValueSelectorSettingsEntry
import it.fast4x.rimusic.ui.screens.settings.SwitchSettingEntry

@Composable
fun SwipeActionSettings() {
    var isSwipeToActionEnabled by Settings.ENABLE_SWIPE_ACTION
    var queueSwipeLeftAction by Settings.QUEUE_SWIPE_LEFT_ACTION
    var queueSwipeRightAction by Settings.QUEUE_SWIPE_RIGHT_ACTION
    var playlistSwipeLeftAction by Settings.PLAYLIST_SWIPE_LEFT_ACTION
    var playlistSwipeRightAction by Settings.PLAYLIST_SWIPE_RIGHT_ACTION
    var albumSwipeLeftAction by Settings.ALBUM_SWIPE_LEFT_ACTION
    var albumSwipeRightAction by Settings.ALBUM_SWIPE_RIGHT_ACTION

    SwitchSettingEntry(
        title = stringResource(R.string.swipe_to_action),
        text = stringResource(R.string.activate_the_action_menu_by_swiping_the_song_left_or_right),
        isChecked = isSwipeToActionEnabled,
        onCheckedChange = { isSwipeToActionEnabled = it }
    )

    AnimatedVisibility(visible = isSwipeToActionEnabled) {
        Column(
            modifier = Modifier.padding(start = 25.dp)
        ) {
            EnumValueSelectorSettingsEntry<QueueSwipeAction>(
                title = stringResource(R.string.queue_and_local_playlists_left_swipe),
                selectedValue = queueSwipeLeftAction,
                onValueSelected = {
                    queueSwipeLeftAction = it
                },
                valueText = { it.text },
            )
            EnumValueSelectorSettingsEntry<QueueSwipeAction>(
                title = stringResource(R.string.queue_and_local_playlists_right_swipe),
                selectedValue = queueSwipeRightAction,
                onValueSelected = {
                    queueSwipeRightAction = it
                },
                valueText = { it.text },
            )
            EnumValueSelectorSettingsEntry<PlaylistSwipeAction>(
                title = stringResource(R.string.playlist_left_swipe),
                selectedValue = playlistSwipeLeftAction,
                onValueSelected = {
                    playlistSwipeLeftAction = it
                },
                valueText = { it.text },
            )
            EnumValueSelectorSettingsEntry<PlaylistSwipeAction>(
                title = stringResource(R.string.playlist_right_swipe),
                selectedValue = playlistSwipeRightAction,
                onValueSelected = {
                    playlistSwipeRightAction = it
                },
                valueText = { it.text },
            )
            EnumValueSelectorSettingsEntry<AlbumSwipeAction>(
                title = stringResource(R.string.album_left_swipe),
                selectedValue = albumSwipeLeftAction,
                onValueSelected = {
                    albumSwipeLeftAction = it
                },
                valueText = { it.text },
            )
            EnumValueSelectorSettingsEntry<AlbumSwipeAction>(
                title = stringResource(R.string.album_right_swipe),
                selectedValue = albumSwipeRightAction,
                onValueSelected = {
                    albumSwipeRightAction = it
                },
                valueText = { it.text },
            )
        }
    }
}