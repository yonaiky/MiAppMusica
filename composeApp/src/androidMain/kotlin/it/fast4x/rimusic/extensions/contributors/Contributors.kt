package it.fast4x.rimusic.extensions.contributors

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import it.fast4x.rimusic.extensions.contributors.models.Developer
import it.fast4x.rimusic.extensions.contributors.models.Translator
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import timber.log.Timber

private val JSON: Json by lazy {
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}
private lateinit var developersList: List<Developer>
private lateinit var translatorsList: List<Translator>

@Composable
fun countDevelopers(): Int {
    val context = LocalContext.current

    if( !::developersList.isInitialized )
        initDevelopers( context )

    return if ( developersList.isEmpty() ) 0
    else
        developersList.size
}



@OptIn(ExperimentalSerializationApi::class)
private fun initDevelopers(context: Context) {
    try {
        context.resources
               .openRawResource( R.raw.contributors )
               .use { inStream ->
                   JSON.decodeFromStream<List<Developer>>( inStream )
                       .sortedBy { it.displayName ?: it.username }
               }
               .also { developersList = it }
    } catch ( e: Exception ) {
        Timber.e( e.stackTraceToString() )
        println("Contributors initDevelopers Exception: ${e.message}")
        developersList = emptyList()
    }
}

@Composable
fun ShowDevelopers() {
    val context = LocalContext.current

    if( !::developersList.isInitialized )
        initDevelopers( context )

    if ( developersList.isEmpty() ) return

    Box( Modifier.fillMaxWidth().height( 600.dp ) ) {
        LazyColumn( Modifier.fillMaxWidth().padding( top = 15.dp ) ) {
            items( developersList ) { it.Draw() }
        }
    }
}


@OptIn(ExperimentalSerializationApi::class)
private fun initTranslators(context: Context) {
    try {
        context.resources
               .openRawResource( R.raw.translators )
               .use { inStream ->
                   JSON.decodeFromStream<List<Translator>>( inStream )
                       .sortedBy { it.displayName ?: it.username }
               }
               .also { translatorsList = it }
    } catch ( e: Exception ) {
        Timber.e( e.stackTraceToString() )
        println("Contributors initTranslators Exception: ${e.message}")
        translatorsList = emptyList()
    }
}

@Composable
fun countTranslators(): Int {
    val context = LocalContext.current

    if( !::translatorsList.isInitialized )
        initTranslators( context )

    return if ( translatorsList.isEmpty() ) 0
    else
        translatorsList.size
}

@Composable
fun ShowTranslators() {
    val context = LocalContext.current

    if( !::translatorsList.isInitialized )
        initTranslators( context )

    if ( translatorsList.isEmpty() ) return

    Box( Modifier.fillMaxWidth().height( 600.dp ) ) {
        LazyColumn( Modifier.fillMaxWidth().padding( top = 15.dp ) ) {
            items( translatorsList ) { it.Draw() }
        }
    }
}