package it.fast4x.innertube.models.bodies

import it.fast4x.innertube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class PlayerBody(
    val context: Context = Context.DefaultWeb,
    val videoId: String,
    val playlistId: String? = null,
    val contentCheckOk: Boolean = true,
    val racyCheckOk: Boolean = true,
    val playbackContext: PlaybackContext? = null,
    val cpn: String? = "wzf9Y0nqz6AUe2Vr",
) {
    @Serializable
    data class PlaybackContext(
        val contentPlaybackContext: ContentPlaybackContext,
    ) {
        @Serializable
        data class ContentPlaybackContext(
            //val html5Preference: String = "HTML5_PREF_WANTS",
            val signatureTimestamp: Int //= 20073,
        )
    }
}
