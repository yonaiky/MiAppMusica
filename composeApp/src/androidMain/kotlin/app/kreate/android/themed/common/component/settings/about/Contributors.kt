package app.kreate.android.themed.common.component.settings.about

import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.knighthat.utils.Toaster

class Contributors(context: Context) {

    companion object {
        @Composable
        fun Show( list: List<InfoCard> ) =
            LazyColumn(
                Modifier.fillMaxWidth()
                        .heightIn( 150.dp, 300.dp )
                        .padding( top = 15.dp )
            ) {
                items(
                    items = list,
                    key = System::identityHashCode
                ) { card ->
                    card.Draw( InfoCard.defaultValues, LocalUriHandler.current )
                }
            }
    }

    private val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    val developers: List<Developer> =
        parseRawJson( context, R.raw.contributors ) {
            it.displayName ?: it.username
        }
    val translators: List<Translator> =
        parseRawJson( context, R.raw.translators ) {
            it.displayName ?: it.username
        }

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> parseRawJson(
        context: Context,
        @RawRes rawId: Int,
        crossinline sortCategory: (T) -> String
    ): List<T> =
        runCatching {
            context.resources
                   .openRawResource( rawId )
                   .use { inStream ->
                       json.decodeFromStream<List<T>>( inStream )
                           .sortedBy( sortCategory )
                   }
        }.onFailure { err ->
            err.printStackTrace()
            err.message?.also( Toaster::e )
        }.getOrDefault( emptyList() )
}