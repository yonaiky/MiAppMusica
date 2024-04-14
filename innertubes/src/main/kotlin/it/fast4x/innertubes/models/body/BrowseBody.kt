package it.fast4x.innertubes.models.body

import it.fast4x.innertubes.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class BrowseBody(
    val context: Context,
    val browseId: String?,
    val params: String?,
)
