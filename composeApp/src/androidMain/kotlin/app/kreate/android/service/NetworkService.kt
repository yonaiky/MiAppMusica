package app.kreate.android.service

import app.kreate.android.BuildConfig
import app.kreate.android.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

object NetworkService {

    @OptIn(ExperimentalSerializationApi::class)
    val JSON: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false

        // Exclude ("type": "me.knighthat.innertube.*")
        // since there's no intention to deserialize json
        // string back to the class
        classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    var client = initClient()
        private set

    private fun getProxy(): Proxy {
        if( !Preferences.IS_PROXY_ENABLED.value ) return Proxy.NO_PROXY

        val proxy = Proxy(
            Preferences.PROXY_SCHEME.value,
            InetSocketAddress(Preferences.PROXY_HOST.value, Preferences.PROXY_PORT.value)
        )

        return runCatching {
            OkHttpClient.Builder()
                        .proxy( proxy )
                        .callTimeout( 3, TimeUnit.SECONDS )
                        .build()
                        .newCall(
                            Request.Builder()
                                .get()
                                .url( "http://example.com" )
                                .build()
                        )
                        .execute()

            proxy
        }.getOrDefault( Proxy.NO_PROXY )
    }

    private fun initClient(): HttpClient =
        HttpClient(OkHttp) {
            expectSuccess = true

            // TODO: Add json (de)serialization once expanded to global use
            install( ContentNegotiation ) {
                json( JSON )
            }

            install( ContentEncoding ) {
                gzip( 1f )
                deflate( 0.9F )
            }

            engine {
                this.proxy = getProxy()

                if( BuildConfig.DEBUG ) {
                    addInterceptor(
                        HttpLoggingInterceptor().setLevel( HttpLoggingInterceptor.Level.HEADERS )
                    )
                    addInterceptor(
                        HttpLoggingInterceptor().setLevel( HttpLoggingInterceptor.Level.BODY )
                    )
                }
            }
        }
}