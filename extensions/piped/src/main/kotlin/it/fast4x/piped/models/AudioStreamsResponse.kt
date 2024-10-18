package it.fast4x.piped.models

import kotlinx.serialization.Serializable

@Serializable
data class AudioStream(
    val url: String,
    val bitrate: Long
)

@Serializable
data class PipedResponse(
    val audioStreams: List<AudioStream>
)