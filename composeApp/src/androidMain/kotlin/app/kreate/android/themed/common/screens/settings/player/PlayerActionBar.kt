package app.kreate.android.themed.common.screens.settings.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.animatedEntry
import app.kreate.android.themed.common.component.settings.entry
import app.kreate.android.themed.common.component.settings.header
import it.fast4x.rimusic.enums.PlayerType

fun LazyListScope.playerActionBarSection( search: SettingEntrySearch, isLandscape: Boolean ) {
    header( R.string.player_action_bar )

    entry( search, R.string.action_bar_transparent_background ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_TRANSPARENT_ACTIONS_BAR,
            R.string.action_bar_transparent_background
        )
    }
    entry( search, R.string.actionspacedevenly ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_BUTTONS_SPACED_EVENLY,
            R.string.actionspacedevenly
        )
    }
    entry( search, R.string.tapqueue ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTIONS_BAR_TAP_TO_OPEN_QUEUE,
            R.string.tapqueue
        )
    }
    entry( search, R.string.swipe_up_to_open_the_queue ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTIONS_BAR_SWIPE_UP_TO_OPEN_QUEUE,
            R.string.swipe_up_to_open_the_queue
        )
    }
    entry( search, R.string.action_bar_show_video_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_TOGGLE_VIDEO,
            R.string.action_bar_show_video_button
        )
    }
    entry( search, R.string.action_bar_show_discover_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_DISCOVER,
            R.string.action_bar_show_discover_button
        )
    }
    entry( search, R.string.action_bar_show_download_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_DOWNLOAD,
            R.string.action_bar_show_download_button
        )
    }
    entry( search, R.string.action_bar_show_add_to_playlist_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_ADD_TO_PLAYLIST,
            R.string.action_bar_show_add_to_playlist_button
        )
    }
    entry( search, R.string.action_bar_show_loop_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_LOOP,
            R.string.action_bar_show_loop_button
        )
    }
    entry( search, R.string.action_bar_show_shuffle_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_SHUFFLE,
            R.string.action_bar_show_shuffle_button
        )
    }
    entry( search, R.string.action_bar_show_lyrics_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_SHOW_LYRICS,
            R.string.action_bar_show_lyrics_button
        )
    }
    entry(
        search = search,
        titleId = R.string.expandedplayer,
        additionalCheck = (!isLandscape || !Preferences.PLAYER_SHOW_THUMBNAIL.value) && !Preferences.LYRICS_SHOW_THUMBNAIL.value
    ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_TOGGLE_EXPAND,
            R.string.expandedplayer
        )
    }
    entry( search, R.string.action_bar_show_sleep_timer_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_SLEEP_TIMER,
            R.string.action_bar_show_sleep_timer_button
        )
    }
    entry( search, R.string.show_equalizer ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_OPEN_EQUALIZER,
            R.string.show_equalizer
        )
    }
    entry( search, R.string.action_bar_show_arrow_button_to_open_queue ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_OPEN_QUEUE_ARROW,
            R.string.action_bar_show_arrow_button_to_open_queue
        )
    }
    entry( search, R.string.action_bar_show_start_radio_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_START_RADIO,
            R.string.action_bar_show_start_radio_button
        )
    }
    entry( search, R.string.action_bar_show_menu_button ) {
        SettingComponents.BooleanEntry(
            Preferences.PLAYER_ACTION_SHOW_MENU,
            R.string.action_bar_show_menu_button
        )
    }
    entry( search, R.string.title_playback_speed ) {
        SettingComponents.BooleanEntry(
            Preferences.AUDIO_SPEED,
            R.string.title_playback_speed,
            R.string.description_playback_speed
        )
    }

    if( !Preferences.LYRICS_SHOW_THUMBNAIL.value ) {
        header( R.string.full_screen_lyrics_components )

        animatedEntry(
            key = "showLyricsThumbnailFalse",
            visible = true
        ) {
            Column {
                if ( Preferences.PLAYER_SHOW_TOTAL_QUEUE_TIME.value && search appearsIn R.string.show_total_time_of_queue )
                    SettingComponents.BooleanEntry(
                        Preferences.PLAYER_IS_QUEUE_DURATION_EXPANDED,
                        R.string.show_total_time_of_queue
                    )

                if ( search appearsIn R.string.titleartist )
                    SettingComponents.BooleanEntry(
                        Preferences.PLAYER_IS_TITLE_EXPANDED,
                        R.string.titleartist
                    )

                if ( search appearsIn R.string.timeline )
                    SettingComponents.BooleanEntry(
                        Preferences.PLAYER_IS_TIMELINE_EXPANDED,
                        R.string.timeline
                    )

                if ( search appearsIn R.string.controls )
                    SettingComponents.BooleanEntry(
                        Preferences.PLAYER_IS_CONTROLS_EXPANDED,
                        R.string.controls
                    )

                if( Preferences.PLAYER_STATS_FOR_NERDS.value
                    && (!(Preferences.PLAYER_SHOW_THUMBNAIL.value && Preferences.PLAYER_TYPE.value == PlayerType.Essential))
                    && search appearsIn R.string.statsfornerds
                )
                    SettingComponents.BooleanEntry(
                        Preferences.PLAYER_IS_STATS_FOR_NERDS_EXPANDED,
                        R.string.statsfornerds
                    )

                if (
                    (Preferences.PLAYER_ACTION_ADD_TO_PLAYLIST.value
                            || Preferences.PLAYER_ACTION_OPEN_QUEUE_ARROW.value
                            || Preferences.PLAYER_ACTION_DOWNLOAD.value
                            || Preferences.PLAYER_ACTION_LOOP.value
                            || Preferences.PLAYER_ACTION_SHOW_LYRICS.value
                            || Preferences.PLAYER_ACTION_TOGGLE_EXPAND.value
                            || Preferences.PLAYER_ACTION_SHUFFLE.value
                            || Preferences.PLAYER_ACTION_SLEEP_TIMER.value
                            || Preferences.PLAYER_ACTION_SHOW_MENU.value
                            || Preferences.PLAYER_ACTION_OPEN_EQUALIZER.value
                            || Preferences.PLAYER_ACTION_DISCOVER.value
                            || Preferences.PLAYER_ACTION_TOGGLE_VIDEO.value)
                    && search appearsIn R.string.actionbar
                )
                    SettingComponents.BooleanEntry(
                        Preferences.PLAYER_IS_ACTIONS_BAR_EXPANDED,
                        R.string.actionbar
                    )

                if( Preferences.PLAYER_SHOW_NEXT_IN_QUEUE.value
                    && Preferences.PLAYER_IS_ACTIONS_BAR_EXPANDED.value
                    && search appearsIn R.string.miniqueue
                )
                    SettingComponents.BooleanEntry(
                        Preferences.PLAYER_IS_NEXT_IN_QUEUE_EXPANDED,
                        R.string.miniqueue
                    )
            }
        }
    }
}