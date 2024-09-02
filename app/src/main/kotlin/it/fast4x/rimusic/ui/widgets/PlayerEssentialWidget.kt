package it.fast4x.rimusic.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalState
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.service.PlayerService
import it.fast4x.rimusic.ui.widgets.PlayerEssentialWidget.Companion.isPlayingKey
import it.fast4x.rimusic.ui.widgets.PlayerEssentialWidget.Companion.songNameKey
import it.fast4x.rimusic.ui.widgets.PlayerEssentialWidget.Companion.widgetBitmap
import it.fast4x.rimusic.ui.widgets.PlayerEssentialWidget.Companion.widgetPlayer

class PlayerEssentialReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PlayerEssentialWidget()
}

class PlayerEssentialWidget: GlanceAppWidget() {
    companion object {
        // key for Preferences
        val songNameKey = stringPreferencesKey("songNameKey")
        val isPlayingKey = booleanPreferencesKey("isPlayingKey")
        var widgetBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        lateinit var widgetPlayer: ExoPlayer
    }

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            GlanceTheme {
                WidgetActiveContent()
            }
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun updateInfo(
        context: Context,
        songName: String,
        isPlaying: Boolean,
        bitmap: Bitmap,
        player: ExoPlayer
    ) {

        val glanceId =
            GlanceAppWidgetManager(context).getGlanceIds(PlayerEssentialWidget::class.java).firstOrNull()
                ?: return

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { preferences ->
            preferences.toMutablePreferences().apply {
                this[songNameKey] = songName
                this[isPlayingKey] = isPlaying
            }
        }

        widgetBitmap = bitmap
        widgetPlayer = player
        PlayerEssentialWidget().update(context, glanceId)
    }

}



@OptIn(UnstableApi::class)
@Composable
fun WidgetContent()  {
    val playerIsActive = !widgetPlayer.currentMediaItem?.mediaMetadata?.title.isNullOrEmpty()
    println("WidgetContent: playerIsActive: $playerIsActive")
    when (playerIsActive) {
        true -> WidgetActiveContent()
        false -> {
            Text(text = "RiMusic Player is not active", modifier = GlanceModifier.padding(12.dp))
        }
    }




}

@Composable
fun WidgetActiveContent() {
    val preferences = currentState<Preferences>()
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(GlanceTheme.colors.background),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Song: ${preferences[songNameKey]}", modifier = GlanceModifier.padding(12.dp))
        Text(text = "isPlaying: ${preferences[isPlayingKey]}", modifier = GlanceModifier.padding(12.dp))
        /*
        Text(
            text = "Song: ${widgetPlayer.currentMediaItem?.mediaMetadata?.title}",
            modifier = GlanceModifier.padding(12.dp)
        )
        Text(
            text = "isPlaying: ${widgetPlayer.isPlaying}",
            modifier = GlanceModifier.padding(12.dp)
        )
        */
        Text(
            text = "Click for previous song",
            modifier = GlanceModifier.padding(12.dp)
                .clickable {
                    widgetPlayer.seekToPrevious()
                }
        )
        Text(
            text = "Click for play/pause",
            modifier = GlanceModifier.padding(12.dp)
                .clickable {
                    if (widgetPlayer.isPlaying) {
                        widgetPlayer.pause()
                    } else {
                        widgetPlayer.play()
                    }
                }
        )
        Text(
            text = "Click for next song",
            modifier = GlanceModifier.padding(12.dp)
                .clickable {
                    widgetPlayer.seekToNext()
                }
        )


        Image(
            provider = ImageProvider(widgetBitmap),
            contentDescription = "My image",
        )

    }

}

