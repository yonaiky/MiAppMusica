package app.kreate.android.service.innertube

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import app.kreate.android.BuildConfig
import app.kreate.android.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.sha1
import io.ktor.util.toMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import me.knighthat.innertube.Constants
import me.knighthat.innertube.Innertube
import me.knighthat.innertube.request.Request
import me.knighthat.innertube.request.body.Context
import me.knighthat.innertube.response.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.Blocking
import timber.log.Timber


class InnertubeProvider: Innertube.Provider {

    private val COOKIE_MAP by derivedStateOf {
        val cookies by Preferences.YOUTUBE_COOKIES
        if( cookies.isBlank() ) return@derivedStateOf emptyMap()

        runCatching {
            cookies.split( ';' )
                    .associate {
                        val (k, v) = it.split('=', limit = 2)
                        k.trim() to v.trim()
                    }
        }.onFailure {
            it.printStackTrace()
            Timber.tag( "InnertubeProvider" ).e( "Cookie parser failed!" )
        }.getOrElse { emptyMap() }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val JSON: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false

        // Exclude ("type": "me.knighthat.innertube.*")
        // since there's no intention to deserialize json
        // string back to the class
        classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    private val CLIENT = HttpClient(OkHttp) {
        expectSuccess = true

        // TODO: Add json (de)serialization once expanded to global use
        install( ContentNegotiation )

        install( ContentEncoding ) {
            gzip(1f)
            deflate(0.9F)
        }

        if( BuildConfig.DEBUG )
            engine {
                addInterceptor(
                    HttpLoggingInterceptor().setLevel( HttpLoggingInterceptor.Level.HEADERS )
                )
                addInterceptor(
                    HttpLoggingInterceptor().setLevel( HttpLoggingInterceptor.Level.BODY )
                )
            }

        defaultRequest {
            accept( ContentType.Application.Json )
            contentType( ContentType.Application.Json )

            headers {
                append( "X-Goog-Api-Format-Version", "1" )
                append( "X-Origin", Constants.YOUTUBE_MUSIC_URL )
                append( "Referer", Constants.YOUTUBE_MUSIC_URL )
            }
        }
    }

    override val cookies: String by Preferences.YOUTUBE_COOKIES
    override val dataSyncId: String by Preferences.YOUTUBE_SYNC_ID
    override val visitorData: String by Preferences.YOUTUBE_VISITOR_DATA

    @Blocking
    override fun execute( request: Request ): Response = runBlocking( Dispatchers.IO ) {
        val result = CLIENT.request( request.url ) {
            method = HttpMethod.parse( request.httpMethod )
            // Disable pretty print - potentially save data
            url {
                parameters.append( "prettyPrint", "false" )
            }
            // Only setBody when it's not null
            JSON.encodeToString( request.dataToSend ).also( this::setBody )
            // Add headers
            request.headers
                   .forEach( headers::appendAll )

            headers {
                val context = request.dataToSend?.context ?: Context.WEB_REMIX_DEFAULT

                append( "X-YouTube-Client-Name", context.client.xClientName.toString() )
                append( "X-YouTube-Client-Version", context.client.clientVersion )

                // Series of checks, if 1 fails, then don't send login information
                if (
                    !request.useLogin
                    || cookies.isBlank()
                    || "SAPISID" !in COOKIE_MAP
                ) return@headers

                append( "cookie", cookies )

                val currentTime = System.currentTimeMillis() / 1000
                val sapisidHash: String
                "%d %s %s".format( currentTime, COOKIE_MAP["SAPISID"], Constants.YOUTUBE_MUSIC_URL )
                          .toByteArray()
                          .let( ::sha1 )
                          .joinToString("") { "%02x".format(it) }
                          .also { sapisidHash = it }
                append("Authorization", "SAPISIDHASH ${currentTime}_$sapisidHash")
            }
        }

        Response(
            result.status.value, "", result.headers.toMap(), result.bodyAsText()
        )
    }
}