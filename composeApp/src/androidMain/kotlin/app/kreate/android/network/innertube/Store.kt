package app.kreate.android.network.innertube

import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.knighthat.utils.Toaster
import org.jetbrains.annotations.Blocking

object Store {

    private const val DEFAULT_VISITOR_DATA = "CgtMN0FkbDFaWERfdyi8t4u7BjIKCgJWThIEGgAgWQ%3D%3D"
    private const val DEFAULT_COOKIE = "PREF=hl=en&tz=UTC; SOCS=CAI"

    private val REGEXES_VISITOR_DATA = listOf(
        Regex("\\{\"key\":\"visitor_data\",\"value\":\"(Cgt.*?%3D%3D)\"\\}"),
        Regex(",\"VISITOR_DATA\":\"(Cgt.*?%3D%3D)\",")
    )
    private lateinit var ghostResponseHeaders: Headers
    private lateinit var ghostResponseBody: String
    private lateinit var visitorData: String
    private lateinit var cookie: String

    @Blocking
    private suspend fun fetchIfNeeded() {
        if( ::ghostResponseBody.isInitialized && ::ghostResponseHeaders.isInitialized )
            return

        runCatching {
            Innertube.client.get("https://www.youtube.com/watch?v=dQw4w9WgXcQ&bpctr=9999999999&has_verified=1") {
                headers {
                    append( HttpHeaders.Connection, "Close" )
                    append( HttpHeaders.Host, "https://www.youtube.com" )
                    append( HttpHeaders.Cookie, DEFAULT_COOKIE )
                    append( HttpHeaders.UserAgent, Context.USER_AGENT_WEB )
                    append( "Sec-Fetch-Mode", "navigate" )
                }
            }
        }.fold(
            onSuccess = {
                // Cache for later use
                ghostResponseHeaders = it.headers
                ghostResponseBody = it.bodyAsText()
            },
            onFailure = {
                Toaster.e("Failed to get visitorData")
                it.printStackTrace()
            }
        )
    }

    @Blocking
    fun getVisitorData(): String {
        if( ::visitorData.isInitialized )
            return visitorData

        runBlocking( Dispatchers.IO ) { fetchIfNeeded() }

        if( ::ghostResponseBody.isInitialized )
            REGEXES_VISITOR_DATA.firstNotNullOfOrNull { regex ->
                                    regex.find( ghostResponseBody )
                                         ?.groupValues
                                         ?.getOrNull( 1 )
                                }
                                ?.let { visitorData = it }
        else
            visitorData = DEFAULT_VISITOR_DATA

        return visitorData
    }

    @Blocking
    fun getCookie(): String {
        if( ::cookie.isInitialized )
            return cookie

        runBlocking( Dispatchers.IO ) { fetchIfNeeded() }

        if( ::ghostResponseHeaders.isInitialized )
            ghostResponseHeaders.getAll(HttpHeaders.SetCookie)
                                .orEmpty()
                                .joinToString("; ") {
                                    it.split(";").first()
                                }
                                .let { cookie = "$DEFAULT_COOKIE; $it" }
        else
            cookie = DEFAULT_COOKIE

        return cookie
    }
}