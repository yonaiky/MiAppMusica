package it.fast4x.innertubes.models.body

import it.fast4x.innertubes.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class PlayerBody(
    val context: Context,
    val videoId: String,
    val playlistId: String?,
)
