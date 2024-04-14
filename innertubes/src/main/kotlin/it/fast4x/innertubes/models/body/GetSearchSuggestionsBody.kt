package it.fast4x.innertubes.models.body

import it.fast4x.innertubes.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)
