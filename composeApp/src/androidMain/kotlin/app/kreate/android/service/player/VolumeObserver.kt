package app.kreate.android.service.player

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings

class VolumeObserver(
    private val context: Context,
    private val onVolumeChanged: (Int) -> Unit
): ContentObserver(Handler(Looper.getMainLooper())) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun register() =
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            this
        )

    fun unregister() = context.contentResolver.unregisterContentObserver( this )

    override fun onChange( selfChange: Boolean ) =
        onVolumeChanged(
            audioManager.getStreamVolume( AudioManager.STREAM_MUSIC )
        )
}