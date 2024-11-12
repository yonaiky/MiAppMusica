package it.fast4x.rimusic.models

import kotlinx.serialization.Serializable


@Serializable
class GhostSong (
    val id: String,
    val title: String,
    val artistsText: String? = null,
    val durationText: String?,
    val thumbnailUrl: String?,
    val likedAt: Long? = null,
    val totalPlayTimeMs: Long = 0
)
