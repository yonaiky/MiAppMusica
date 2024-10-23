package it.fast4x.invidious.models

import kotlinx.serialization.Serializable

@Serializable
data class InvidiousResponse (
    val adaptiveFormats: List<AdaptiveFormat>? = null
)

@Serializable
data class AdaptiveFormat (
    val init: String? = null,
    val index: String? = null,
    val bitrate: Long? = null,
    val url: String? = null,
    val itag: String? = null,
    val type: String? = null,
    val clen: String? = null,
    val lmt: String? = null,
    val projectionType: String? = null,
    val container: String? = null,
    val encoding: String? = null,
    val audioQuality: String? = null,
    val audioSampleRate: Long? = null,
    val audioChannels: Long? = null
)