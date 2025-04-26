package it.fast4x.rimusic.ui.widgets

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.core.graphics.createBitmap
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import it.fast4x.rimusic.cleanPrefix

class PlayerVerticalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PlayerVerticalWidget()
}

class PlayerVerticalWidget: GlanceAppWidget() {
    companion object {
        val songTitleKey = stringPreferencesKey("songTitleKey")
        val songArtistKey = stringPreferencesKey("songArtistKey")
        val isPlayingKey = booleanPreferencesKey("isPlayingKey")
        var widgetBitmap = createBitmap(1, 1)
        lateinit var widgetPlayer: ExoPlayer
    }

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            GlanceTheme {
                WidgetActiveVerticalContent(context)
            }
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun updateInfo(
        context: Context,
        songTitle: String,
        songArtist: String,
        isPlaying: Boolean,
        bitmap: Bitmap,
        player: ExoPlayer
    ) {

        val glanceId =
            GlanceAppWidgetManager(context).getGlanceIds(PlayerVerticalWidget::class.java).firstOrNull()
                ?: return

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { preferences ->
            preferences.toMutablePreferences().apply {
                this[songTitleKey] = cleanPrefix(songTitle)
                this[songArtistKey] = songArtist
                this[isPlayingKey] = isPlaying
            }
        }

        widgetBitmap = bitmap
        widgetPlayer = player
        PlayerVerticalWidget().update(context, glanceId)
    }

}
