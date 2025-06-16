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
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.AudioQualityFormat
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.screens.settings.SettingsEntry
import it.fast4x.rimusic.ui.screens.settings.SliderSettingsEntry
import it.fast4x.rimusic.utils.RestartPlayerService
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
        SettingComponents.EnumEntry(
            Preferences.AUDIO_QUALITY,
            R.string.audio_quality_format
        ) { onRestartServiceChange( true ) }

        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.enable_connection_metered ) )
        SettingComponents.BooleanEntry(
            Preferences.IS_CONNECTION_METERED,
            R.string.enable_connection_metered,
            R.string.info_enable_connection_metered
        ) {
            if ( it )
                Preferences.AUDIO_QUALITY.value = AudioQualityFormat.Auto
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

        var jumpPrevious by Preferences.JUMP_PREVIOUS
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
        SettingComponents.EnumEntry(
            Preferences.QUICK_PICKS_MIN_DURATION,
            R.string.min_listening_time
        )
        SettingComponents.Description( R.string.is_min_list_time_for_tips_or_quick_pics )

        SettingComponents.EnumEntry(
            Preferences.LIMIT_SONGS_WITH_DURATION,
            R.string.exclude_songs_with_duration_limit
        )
        SettingComponents.Description( R.string.exclude_songs_with_duration_limit_description )
    }
    if( search.contains( R.string.pause_between_songs ) )
        SettingComponents.EnumEntry(
            Preferences.PAUSE_BETWEEN_SONGS,
            R.string.pause_between_songs
        )

    if( search.contains( R.string.player_pause_listen_history ) ) {

        SettingComponents.BooleanEntry(
            Preferences.PAUSE_HISTORY,
            R.string.player_pause_listen_history,
            R.string.player_pause_listen_history_info,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.player_pause_on_volume_zero ) )
        SettingComponents.BooleanEntry(
            Preferences.PAUSE_WHEN_VOLUME_SET_TO_ZERO,
            R.string.player_pause_on_volume_zero,
            R.string.info_pauses_player_when_volume_zero
        )
    if( search.contains( R.string.effect_fade_audio ) ) {
        SettingComponents.EnumEntry(
            Preferences.AUDIO_FADE_DURATION,
            R.string.effect_fade_audio
        )
        SettingComponents.Description( R.string.effect_fade_audio_description )
    }
    if( search.contains( R.string.player_keep_minimized ) )
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_KEEP_MINIMIZED,
            R.string.player_keep_minimized,
            R.string.when_click_on_a_song_player_start_minimized
        )
    if( search.contains( R.string.player_collapsed_disable_swiping_down ) )
        SettingComponents.BooleanEntry(
            Preferences.MINI_DISABLE_SWIPE_DOWN_TO_DISMISS,
            R.string.player_collapsed_disable_swiping_down,
            R.string.avoid_closing_the_player_cleaning_queue_by_swiping_down
        )
    if( search.contains( R.string.player_auto_load_songs_in_queue ) ) {
        SettingComponents.BooleanEntry(
            Preferences.QUEUE_AUTO_APPEND,
            R.string.player_auto_load_songs_in_queue,
            R.string.player_auto_load_songs_in_queue_description
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) })
    }
    if( search.contains( R.string.max_songs_in_queue ) )
        SettingComponents.EnumEntry(
            Preferences.MAX_NUMBER_OF_SONG_IN_QUEUE,
            R.string.max_songs_in_queue
        )
    if( search.contains( R.string.discover ) )
        SettingComponents.BooleanEntry(
            Preferences.ENABLE_DISCOVER,
            R.string.discover,
            R.string.discoverinfo
        )
    if( search.contains( R.string.playlistindicator ) )
        SettingComponents.BooleanEntry(
            Preferences.SHOW_PLAYLIST_INDICATOR,
            R.string.playlistindicator,
            R.string.playlistindicatorinfo
        )
    if( search.contains( R.string.now_playing_indicator ) )
        SettingComponents.EnumEntry(
            Preferences.NOW_PLAYING_INDICATOR,
            R.string.now_playing_indicator
        )
    if( isAtLeastAndroid6 && search.contains( R.string.resume_playback ) ) {
        SettingComponents.BooleanEntry(
            Preferences.RESUME_PLAYBACK_WHEN_CONNECT_TO_AUDIO_DEVICE,
            R.string.resume_playback,
            R.string.when_device_is_connected,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) })
    }
    if( search.contains( R.string.persistent_queue ) ) {
        SettingComponents.BooleanEntry(
            Preferences.ENABLE_PERSISTENT_QUEUE,
            R.string.persistent_queue,
            R.string.save_and_restore_playing_songs,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) })

        AnimatedVisibility( Preferences.ENABLE_PERSISTENT_QUEUE.value ) {
            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                SettingComponents.BooleanEntry(
                    Preferences.RESUME_PLAYBACK_ON_STARTUP,
                    R.string.resume_playback_on_start,
                    R.string.resume_automatically_when_app_opens,
                    action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                ) {
                    onRestartServiceChange( true )
                }
                RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
            }
        }
    }
    if( search.contains( R.string.close_app_with_back_button ) ) {
        SettingComponents.BooleanEntry(
            Preferences.CLOSE_APP_ON_BACK,
            R.string.close_app_with_back_button,
            R.string.when_you_use_the_back_button_from_the_home_page,
            isEnabled = Build.VERSION.SDK_INT >= 33,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.close_background_player ) ) {
        SettingComponents.BooleanEntry(
            Preferences.CLOSE_BACKGROUND_JOB_IN_TASK_MANAGER,
            R.string.close_background_player,
            R.string.when_app_swipe_out_from_task_manager,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.skip_media_on_error ) ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYBACK_SKIP_ON_ERROR,
            R.string.skip_media_on_error,
            R.string.skip_media_on_error_description,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.skip_silence ) ) {
        SettingComponents.BooleanEntry(
            Preferences.AUDIO_SKIP_SILENCE,
            R.string.skip_silence,
            R.string.skip_silent_parts_during_playback
        )

        AnimatedVisibility( Preferences.AUDIO_SKIP_SILENCE.value ) {
            var minimumSilenceDuration by Preferences.AUDIO_SKIP_SILENCE_LENGTH
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
        SettingComponents.BooleanEntry(
            Preferences.AUDIO_VOLUME_NORMALIZATION,
            R.string.loudness_normalization,
            R.string.autoadjust_the_volume
        )
        AnimatedVisibility( Preferences.AUDIO_VOLUME_NORMALIZATION.value ) {
            var loudnessBaseGain by Preferences.AUDIO_VOLUME_NORMALIZATION_TARGET
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
        SettingComponents.BooleanEntry(
            Preferences.AUDIO_BASS_BOOSTED,
            R.string.settings_audio_bass_boost
        )
        AnimatedVisibility( Preferences.AUDIO_BASS_BOOSTED.value ) {
            var bassboostLevel by Preferences.AUDIO_BASS_BOOST_LEVEL
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
        SettingComponents.EnumEntry(
            Preferences.AUDIO_REVERB_PRESET,
            R.string.settings_audio_reverb
        ) { onRestartServiceChange( true ) }
        SettingComponents.Description( R.string.settings_audio_reverb_info_apply_a_depth_effect_to_the_audio )

        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.settings_audio_focus ) ) {
        SettingComponents.BooleanEntry(
            Preferences.AUDIO_SMART_PAUSE_DURING_CALLS,
            R.string.settings_audio_focus,
            R.string.settings_audio_focus_info
        )
    }
    if( search.contains( R.string.event_volumekeys ) ) {
        SettingComponents.BooleanEntry(
            Preferences.AUDIO_VOLUME_BUTTONS_CHANGE_SONG,
            R.string.event_volumekeys,
            R.string.event_volumekeysinfo
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.event_shake ) ) {
        SettingComponents.BooleanEntry(
            Preferences.AUDIO_SHAKE_TO_SKIP,
            R.string.event_shake,
            R.string.shake_to_change_song,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
    }
    if( search.contains( R.string.settings_enable_pip ) ) {
        SettingComponents.BooleanEntry(
            Preferences.IS_PIP_ENABLED,
            R.string.settings_enable_pip,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) {
            onRestartServiceChange( true )
        }
        RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
        AnimatedVisibility( Preferences.IS_PIP_ENABLED.value ) {
            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                SettingComponents.EnumEntry(
                    Preferences.PIP_MODULE,
                    R.string.settings_pip_module
                ) { onRestartServiceChange( true ) }

                SettingComponents.BooleanEntry(
                    Preferences.IS_AUTO_PIP_ENABLED,
                    R.string.settings_enable_pip_auto,
                    R.string.pip_info_from_android_12_pip_can_be_automatically_enabled,
                    action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                ) {
                    onRestartServiceChange( true )
                }
                RestartPlayerService(restartService, onRestart = { onRestartServiceChange( false ) } )
            }

        }
    }
    if( search.contains( R.string.settings_enable_autodownload_song ) ) {
        SettingComponents.BooleanEntry(
            Preferences.AUTO_DOWNLOAD,
            R.string.settings_enable_autodownload_song
        )
        AnimatedVisibility( Preferences.AUTO_DOWNLOAD.value ) {
            Column(
                modifier = Modifier.padding(start = 25.dp)
            ) {
                SettingComponents.BooleanEntry(
                    Preferences.AUTO_DOWNLOAD_ON_LIKE,
                    R.string.settings_enable_autodownload_song_when_liked
                )

                SettingComponents.BooleanEntry(
                    Preferences.AUTO_DOWNLOAD_ON_ALBUM_BOOKMARKED,
                    R.string.settings_enable_autodownload_song_when_album_bookmarked
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