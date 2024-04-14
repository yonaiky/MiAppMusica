package it.fast4x.innertubes.models.body

import it.fast4x.innertubes.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class SearchBody(
    val context: Context,
    val query: String?,
    val params: String?,
)
