package it.fast4x.lrclib

import it.fast4x.lrclib.models.Track
import it.fast4x.lrclib.models.bestMatchingFor
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import it.fast4x.lrclib.utils.runCatchingCancellable
import kotlinx.serialization.json.Json
import kotlin.time.Duration

object LrcLib {
    private val client by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            defaultRequest {
                url("https://lrclib.net")
            }

            expectSuccess = true
        }
    }

    private suspend fun queryLyrics(artist: String, title: String, album: String? = null) =
        client.get("/api/search") {
            parameter("track_name", title)
            parameter("artist_name", artist)
            if (album != null) parameter("album_name", album)
        }.body<List<Track>>().filter { it.syncedLyrics != null }

    suspend fun lyrics(
        artist: String,
        title: String,
        duration: Duration,
        album: String? = null
    ) = runCatchingCancellable {
        val tracks = queryLyrics(artist, title, album)

        tracks.bestMatchingFor(title, duration)?.syncedLyrics?.let(LrcLib::Lyrics)
    }

    suspend fun lyrics(artist: String, title: String) = runCatchingCancellable {
        queryLyrics(artist = artist, title = title, album = null)
    }

    @JvmInline
    value class Lyrics(val text: String) {

        val sentences: List<Pair<Long, String>>
            get() = mutableListOf(0L to "").apply {
                for (line in text.trim().lines()) {
                    try {
                        val position = line.take(10).run {
                            get(8).digitToInt() * 10L +
                                    get(7).digitToInt() * 100 +
                                    get(5).digitToInt() * 1000 +
                                    get(4).digitToInt() * 10000 +
                                    get(2).digitToInt() * 60 * 1000 +
                                    get(1).digitToInt() * 600 * 1000
                        }

                        add(position to line.substring(10))
                    } catch (_: Throwable) {
                    }
                }
            }

    }

}
