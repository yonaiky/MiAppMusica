package app.kreate.android.themed.common.screens.settings.general

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.media3.common.util.UnstableApi
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.AudioQualityFormat
import it.fast4x.rimusic.enums.PipModule
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.screens.settings.EnumValueSelectorSettingsEntry
import it.fast4x.rimusic.ui.screens.settings.SettingsEntry
import it.fast4x.rimusic.ui.screens.settings.SliderSettingsEntry
import it.fast4x.rimusic.ui.screens.settings.SwitchSettingEntry
import it.fast4x.rimusic.utils.RestartPlayerService
import it.fast4x.rimusic.utils.isAtLeastAndroid12
import it.fast4x.rimusic.utils.isAtLeastAndroid6
import it.fast4x.rimusic.utils.rememberEqualizerLauncher
import it.fast4x.rimusic.utils.semiBold

@UnstableApi
@Composable
fun PlayerSettings(
    search: SettingEntrySearch,
    restartService: Boolean,
    onRestartServiceChange: (Boolean) -> Unit
) {
    if( search.contains( R.string.audio_quality_format ) ) {
        var audioQualityFormat by Settings.AUDIO_QUALITY

        EnumValueSelectorSettingsEntry(
            title = stringResource(R.string.audio_quality_format),
            selectedValue = audioQualityFormat,
            onValueSelected = {
                audioQualityFormat = it
                onRestartServiceChange(true)
            },
            valueText = { it.text }
        )

        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.enable_connection_metered ) ) {
        var isConnectionMeteredEnabled by Settings.IS_CONNECTION_METERED

        SwitchSettingEntry(
            title = stringResource(R.string.enable_connection_metered),
            text = stringResource(R.string.info_enable_connection_metered),
            isChecked = isConnectionMeteredEnabled,
            onCheckedChange = {
                isConnectionMeteredEnabled = it
                if (it)
                    Settings.AUDIO_QUALITY.value = AudioQualityFormat.Auto
            }
        )
    }
    if( search.contains( R.string.setting_entry_smart_rewind ) ) {

        BasicText(
            text = stringResource(R.string.setting_entry_smart_rewind),
            style = typography().xs.semiBold.copy(color = colorPalette().text),
            modifier = Modifier.padding(start = 12.dp)
        )
        BasicText(
            text = stringResource(R.string.setting_description_smart_rewind),
            style = typography().xxs.semiBold.copy(color = colorPalette().textDisabled),
            modifier = Modifier.padding(start = 12.dp)
        )
        BasicText(
            text = stringResource(R.string.jump_previous_blank),
            style = typography().xxs.semiBold.copy(color = colorPalette().textDisabled),
            modifier = Modifier.padding(start = 12.dp)
        )

        var jumpPrevious by Settings.JUMP_PREVIOUS
        TextField(
            value = jumpPrevious,
            onValueChange = {
                if (it.isDigitsOnly())
                    jumpPrevious = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = colorPalette().text,
                focusedTextColor = colorPalette().text,
                focusedIndicatorColor = colorPalette().text,
                unfocusedIndicatorColor = colorPalette().text
            ),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
    if( search.contains( R.string.min_listening_time ) ) {
        var exoPlayerMinTimeForEvent by Settings.QUICK_PICKS_MIN_DURATION
        EnumValueSelectorSettingsEntry(
            title = stringResource(R.string.min_listening_time),
            text = stringResource( R.string.is_min_list_time_for_tips_or_quick_pics ),
            selectedValue = exoPlayerMinTimeForEvent,
            onValueSelected = { exoPlayerMinTimeForEvent = it },
            valueText = { it.text }
        )

        var excludeSongWithDurationLimit by Settings.LIMIT_SONGS_WITH_DURATION
        EnumValueSelectorSettingsEntry(
            title = stringResource(R.string.exclude_songs_with_duration_limit),
            text = stringResource( R.string.exclude_songs_with_duration_limit_description ),
            selectedValue = excludeSongWithDurationLimit,
            onValueSelected = { excludeSongWithDurationLimit = it },
            valueText = { it.text }
        )
    }
    if( search.contains( R.string.pause_between_songs ) ) {
        var pauseBetweenSongs by Settings.PAUSE_BETWEEN_SONGS

        EnumValueSelectorSettingsEntry(
            title = stringResource( R.string.pause_between_songs ),
            selectedValue = pauseBetweenSongs,
            onValueSelected = { pauseBetweenSongs = it },
            valueText = { it.text }
        )
    }
    if( search.contains( R.string.player_pause_listen_history ) ) {
        var pauseListenHistory by Settings.PAUSE_HISTORY
        SwitchSettingEntry(
            title = stringResource( R.string.player_pause_listen_history ),
            text = stringResource( R.string.player_pause_listen_history_info ),
            isChecked = pauseListenHistory,
            onCheckedChange = {
                pauseListenHistory = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.player_pause_on_volume_zero ) ) {
        var isPauseOnVolumeZeroEnabled by Settings.PAUSE_WHEN_VOLUME_SET_TO_ZERO

        SwitchSettingEntry(
            title = stringResource(R.string.player_pause_on_volume_zero),
            text = stringResource(R.string.info_pauses_player_when_volume_zero),
            isChecked = isPauseOnVolumeZeroEnabled,
            onCheckedChange = {
                isPauseOnVolumeZeroEnabled = it
            }
        )
    }
    if( search.contains( R.string.effect_fade_audio ) ) {
        var playbackFadeAudioDuration by Settings.AUDIO_FADE_DURATION

        EnumValueSelectorSettingsEntry(
            title = stringResource(R.string.effect_fade_audio),
            text = stringResource(R.string.effect_fade_audio_description),
            selectedValue = playbackFadeAudioDuration,
            onValueSelected = { playbackFadeAudioDuration = it },
            valueText = { it.text }
        )
    }
    if( search.contains( R.string.player_keep_minimized ) ) {
        var keepPlayerMinimized by Settings.PLAYER_KEEP_MINIMIZED

        SwitchSettingEntry(
            title = stringResource(R.string.player_keep_minimized),
            text = stringResource(R.string.when_click_on_a_song_player_start_minimized),
            isChecked = keepPlayerMinimized,
            onCheckedChange = {
                keepPlayerMinimized = it
            }
        )
    }
    if( search.contains( R.string.player_collapsed_disable_swiping_down ) ) {
        var disableClosingPlayerSwipingDown by Settings.MINI_DISABLE_SWIPE_DOWN_TO_DISMISS

        SwitchSettingEntry(
            title = stringResource(R.string.player_collapsed_disable_swiping_down),
            text = stringResource(R.string.avoid_closing_the_player_cleaning_queue_by_swiping_down),
            isChecked = disableClosingPlayerSwipingDown,
            onCheckedChange = {
                disableClosingPlayerSwipingDown = it
            }
        )
    }
    if( search.contains( R.string.player_auto_load_songs_in_queue ) ) {
        var autoLoadSongsInQueue by Settings.QUEUE_AUTO_APPEND

        SwitchSettingEntry(
            title = stringResource(R.string.player_auto_load_songs_in_queue),
            text = stringResource(R.string.player_auto_load_songs_in_queue_description),
            isChecked = autoLoadSongsInQueue,
            onCheckedChange = {
                autoLoadSongsInQueue = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) })
    }
    if( search.contains( R.string.max_songs_in_queue ) ) {
        var maxSongsInQueue by Settings.MAX_NUMBER_OF_SONG_IN_QUEUE

        EnumValueSelectorSettingsEntry(
            title = stringResource(R.string.max_songs_in_queue),
            selectedValue = maxSongsInQueue,
            onValueSelected = { maxSongsInQueue = it },
            valueText = { it.text }
        )
    }
    if( search.contains( R.string.discover ) ) {
        var discoverIsEnabled by Settings.ENABLE_DISCOVER

        SwitchSettingEntry(
            title = stringResource(R.string.discover),
            text = stringResource(R.string.discoverinfo),
            isChecked = discoverIsEnabled,
            onCheckedChange = { discoverIsEnabled = it }
        )
    }
    if( search.contains( R.string.playlistindicator ) ) {
        var playlistIndicator by Settings.SHOW_PLAYLIST_INDICATOR

        SwitchSettingEntry(
            title = stringResource(R.string.playlistindicator),
            text = stringResource(R.string.playlistindicatorinfo),
            isChecked = playlistIndicator,
            onCheckedChange = {
                playlistIndicator = it
            }
        )
    }
    if( search.contains( R.string.now_playing_indicator ) ) {
        var nowPlayingIndicator by Settings.NOW_PLAYING_INDICATOR

        EnumValueSelectorSettingsEntry(
            title = stringResource(R.string.now_playing_indicator),
            selectedValue = nowPlayingIndicator,
            onValueSelected = { nowPlayingIndicator = it },
            valueText = { it.text }
        )
    }
    if( isAtLeastAndroid6 && search.contains( R.string.resume_playback ) ) {
        var resumePlaybackWhenDeviceConnected by Settings.RESUME_PLAYBACK_WHEN_CONNECT_TO_AUDIO_DEVICE

        SwitchSettingEntry(
            title = stringResource(R.string.resume_playback),
            text = stringResource(R.string.when_device_is_connected),
            isChecked = resumePlaybackWhenDeviceConnected,
            onCheckedChange = {
                resumePlaybackWhenDeviceConnected = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) })
    }
    if( search.contains( R.string.persistent_queue ) ) {
        var persistentQueue by Settings.ENABLE_PERSISTENT_QUEUE

        SwitchSettingEntry(
            title = stringResource(R.string.persistent_queue),
            text = stringResource(R.string.save_and_restore_playing_songs),
            isChecked = persistentQueue,
            onCheckedChange = {
                persistentQueue = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) })

        AnimatedVisibility(visible = persistentQueue) {
            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                var resumePlaybackOnStart by Settings.RESUME_PLAYBACK_ON_STARTUP

                SwitchSettingEntry(
                    title =  stringResource(R.string.resume_playback_on_start),
                    text = stringResource(R.string.resume_automatically_when_app_opens),
                    isChecked = resumePlaybackOnStart,
                    onCheckedChange = {
                        resumePlaybackOnStart = it
                        onRestartServiceChange( true )
                    }
                )
                RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
            }
        }
    }
    if( search.contains( R.string.close_app_with_back_button ) ) {
        var closeWithBackButton by Settings.CLOSE_APP_ON_BACK

        SwitchSettingEntry(
            isEnabled = Build.VERSION.SDK_INT >= 33,
            title = stringResource(R.string.close_app_with_back_button),
            text = stringResource(R.string.when_you_use_the_back_button_from_the_home_page),
            isChecked = closeWithBackButton,
            onCheckedChange = {
                closeWithBackButton = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.close_background_player ) ) {
        var closeBackgroundPlayer by Settings.CLOSE_BACKGROUND_JOB_IN_TASK_MANAGER

        SwitchSettingEntry(
            title = stringResource(R.string.close_background_player),
            text = stringResource(R.string.when_app_swipe_out_from_task_manager),
            isChecked = closeBackgroundPlayer,
            onCheckedChange = {
                closeBackgroundPlayer = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.skip_media_on_error ) ) {
        var skipMediaOnError by Settings.PLAYBACK_SKIP_ON_ERROR

        SwitchSettingEntry(
            title = stringResource(R.string.skip_media_on_error),
            text = stringResource(R.string.skip_media_on_error_description),
            isChecked = skipMediaOnError,
            onCheckedChange = {
                skipMediaOnError = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.skip_silence ) ) {
        var skipSilence by Settings.AUDIO_SKIP_SILENCE

        SwitchSettingEntry(
            title = stringResource(R.string.skip_silence),
            text = stringResource(R.string.skip_silent_parts_during_playback),
            isChecked = skipSilence,
            onCheckedChange = {
                skipSilence = it
            }
        )

        AnimatedVisibility(visible = skipSilence) {
            var minimumSilenceDuration by Settings.AUDIO_SKIP_SILENCE_LENGTH
            val initialValue by remember { derivedStateOf { minimumSilenceDuration.toFloat() / 1000L } }
            var newValue by remember(initialValue) { mutableFloatStateOf(initialValue) }

            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                SliderSettingsEntry(
                    title = stringResource(R.string.minimum_silence_length),
                    text = stringResource(R.string.minimum_silence_length_description),
                    state = newValue,
                    onSlide = { newValue = it },
                    onSlideComplete = {
                        minimumSilenceDuration = newValue.toLong() * 1000L
                        onRestartServiceChange( true )
                    },
                    toDisplay = { stringResource(R.string.format_ms, it.toLong()) },
                    range = 1.00f..2000.000f
                )

                RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
            }
        }
    }
    if( search.contains( R.string.loudness_normalization ) ) {
        var volumeNormalization by Settings.AUDIO_VOLUME_NORMALIZATION

        SwitchSettingEntry(
            title = stringResource(R.string.loudness_normalization),
            text = stringResource(R.string.autoadjust_the_volume),
            isChecked = volumeNormalization,
            onCheckedChange = {
                volumeNormalization = it
            }
        )
        AnimatedVisibility(visible = volumeNormalization) {
            var loudnessBaseGain by Settings.AUDIO_VOLUME_NORMALIZATION_TARGET
            val initialValue by remember { derivedStateOf { loudnessBaseGain } }
            var newValue by remember(initialValue) { mutableFloatStateOf(initialValue) }


            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                SliderSettingsEntry(
                    title = stringResource(R.string.settings_loudness_base_gain),
                    text = stringResource(R.string.settings_target_gain_loudness_info),
                    state = newValue,
                    onSlide = { newValue = it },
                    onSlideComplete = {
                        loudnessBaseGain = newValue
                    },
                    toDisplay = { "%.1f dB".format(loudnessBaseGain).replace(",", ".") },
                    range = -20f..20f
                )
            }
        }
    }
    if( search.contains( R.string.settings_audio_bass_boost ) ) {
        var bassboostEnabled by Settings.AUDIO_BASS_BOOSTED

        SwitchSettingEntry(
            title = stringResource(R.string.settings_audio_bass_boost),
            text = "",
            isChecked = bassboostEnabled,
            onCheckedChange = {
                bassboostEnabled = it
            }
        )
        AnimatedVisibility(visible = bassboostEnabled) {
            var bassboostLevel by Settings.AUDIO_BASS_BOOST_LEVEL
            val initialValue by remember { derivedStateOf { bassboostLevel } }
            var newValue by remember(initialValue) { mutableFloatStateOf(initialValue) }


            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                SliderSettingsEntry(
                    title = stringResource(R.string.settings_bass_boost_level),
                    text = "",
                    state = newValue,
                    onSlide = { newValue = it },
                    onSlideComplete = {
                        bassboostLevel = newValue
                    },
                    toDisplay = { "%.1f".format(bassboostLevel).replace(",", ".") },
                    range = 0f..1f
                )
            }
        }
    }
    if( search.contains( R.string.settings_audio_reverb ) ) {
        var audioReverb by Settings.AUDIO_REVERB_PRESET

        EnumValueSelectorSettingsEntry(
            title = stringResource(R.string.settings_audio_reverb),
            text = stringResource(R.string.settings_audio_reverb_info_apply_a_depth_effect_to_the_audio),
            selectedValue = audioReverb,
            onValueSelected = {
                audioReverb = it
                onRestartServiceChange( true )
            },
            valueText = {
                it.textName
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.settings_audio_focus ) ) {
        var audioFocusEnabled by Settings.AUDIO_SMART_PAUSE_DURING_CALLS

        SwitchSettingEntry(
            title = stringResource(R.string.settings_audio_focus),
            text = stringResource(R.string.settings_audio_focus_info),
            isChecked = audioFocusEnabled,
            onCheckedChange = {
                audioFocusEnabled = it
            }
        )
    }
    if( search.contains( R.string.event_volumekeys ) ) {
        var useVolumeKeysToChangeSong by Settings.AUDIO_VOLUME_BUTTONS_CHANGE_SONG

        SwitchSettingEntry(
            title = stringResource(R.string.event_volumekeys),
            text = stringResource(R.string.event_volumekeysinfo),
            isChecked = useVolumeKeysToChangeSong,
            onCheckedChange = {
                useVolumeKeysToChangeSong = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.event_shake ) ) {
        var shakeEventEnabled by Settings.AUDIO_SHAKE_TO_SKIP

        SwitchSettingEntry(
            title = stringResource(R.string.event_shake),
            text = stringResource(R.string.shake_to_change_song),
            isChecked = shakeEventEnabled,
            onCheckedChange = {
                shakeEventEnabled = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.settings_enable_pip ) ) {
        var enablePictureInPicture by Settings.IS_PIP_ENABLED

        SwitchSettingEntry(
            title = stringResource(R.string.settings_enable_pip),
            text = "",
            isChecked = enablePictureInPicture,
            onCheckedChange = {
                enablePictureInPicture = it
                onRestartServiceChange( true )
            }
        )
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
        AnimatedVisibility(visible = enablePictureInPicture) {
            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                var pipModule by Settings.PIP_MODULE

                EnumValueSelectorSettingsEntry(
                    title = stringResource(R.string.settings_pip_module),
                    selectedValue = pipModule,
                    onValueSelected = {
                        pipModule = it
                        onRestartServiceChange( true )
                    },
                    valueText = {
                        when (it) {
                            PipModule.Cover -> stringResource(R.string.pipmodule_cover)
                        }
                    }
                )

                var enablePictureInPictureAuto by Settings.IS_AUTO_PIP_ENABLED

                SwitchSettingEntry(
                    isEnabled = isAtLeastAndroid12,
                    title = stringResource(R.string.settings_enable_pip_auto),
                    text = stringResource(R.string.pip_info_from_android_12_pip_can_be_automatically_enabled),
                    isChecked = enablePictureInPictureAuto,
                    onCheckedChange = {
                        enablePictureInPictureAuto = it
                        onRestartServiceChange( true )
                    }
                )
                RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
            }

        }
    }
    if( search.contains( R.string.settings_enable_autodownload_song ) ) {
        var autoDownloadSong by Settings.AUTO_DOWNLOAD

        SwitchSettingEntry(
            title = stringResource(R.string.settings_enable_autodownload_song),
            text = "",
            isChecked = autoDownloadSong,
            onCheckedChange = {
                autoDownloadSong = it
            }
        )
        AnimatedVisibility(visible = autoDownloadSong) {
            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                var autoDownloadSongWhenLiked by Settings.AUTO_DOWNLOAD_ON_LIKE

                SwitchSettingEntry(
                    title = stringResource(R.string.settings_enable_autodownload_song_when_liked),
                    text = "",
                    isChecked = autoDownloadSongWhenLiked,
                    onCheckedChange = {
                        autoDownloadSongWhenLiked = it
                    }
                )

                var autoDownloadSongWhenAlbumBookmarked by Settings.AUTO_DOWNLOAD_ON_ALBUM_BOOKMARKED

                SwitchSettingEntry(
                    title = stringResource(R.string.settings_enable_autodownload_song_when_album_bookmarked),
                    text = "",
                    isChecked = autoDownloadSongWhenAlbumBookmarked,
                    onCheckedChange = {
                        autoDownloadSongWhenAlbumBookmarked = it
                    }
                )
            }
        }
    }
    if( search.contains( R.string.equalizer ) ) {
        val binder = LocalPlayerServiceBinder.current
        val launchEqualizer by rememberEqualizerLauncher( { binder?.player?.audioSessionId } )

        SettingsEntry(
            title = stringResource(R.string.equalizer),
            text = stringResource(R.string.interact_with_the_system_equalizer),
            onClick = launchEqualizer
        )
    }
}