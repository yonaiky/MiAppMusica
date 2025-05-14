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

object Store {

    const val DEFAULT = "CgtMN0FkbDFaWERfdyi8t4u7BjIKCgJWThIEGgAgWQ%3D%3D"

    private val REGEXES_VISITOR_DATA = listOf(
        Regex("\\{\"key\":\"visitor_data\",\"value\":\"(Cgt.*?%3D%3D)\"\\}"),
        Regex(",\"VISITOR_DATA\":\"(Cgt.*?%3D%3D)\",")
    )
    private lateinit var ghostResponseHeaders: Headers
    private lateinit var ghostResponseBody: String
    private lateinit var visitorData: String
    private lateinit var cookie: String

    private suspend fun fetchIfNeeded() {
        if( ::ghostResponseBody.isInitialized && ::ghostResponseHeaders.isInitialized )
            return

        val response =
            Innertube.client.get("https://www.youtube.com/watch?v=dQw4w9WgXcQ&bpctr=9999999999&has_verified=1") {
                headers {
                    append( HttpHeaders.Connection, "Close" )
                    append( HttpHeaders.Host, "https://www.youtube.com" )
                    append( HttpHeaders.Cookie, "PREF=hl=en&tz=UTC; SOCS=CAI" )
                    append( HttpHeaders.UserAgent, Context.USER_AGENT_WEB )
                    append( "Sec-Fetch-Mode", "navigate" )
                }
            }

        // Cache for later use
        ghostResponseHeaders = response.headers
        ghostResponseBody = response.bodyAsText()
    }

    fun getVisitorData(): String {
        if( ::visitorData.isInitialized )
            return visitorData

        runBlocking( Dispatchers.IO ) { fetchIfNeeded() }

        val matchedGroup = REGEXES_VISITOR_DATA.firstNotNullOfOrNull { regex ->
            regex.find( ghostResponseBody )?.groupValues?.getOrNull( 1 )
        }
        visitorData = matchedGroup ?: DEFAULT

        return visitorData
    }

    fun getCookie(): String {
        if( ::cookie.isInitialized )
            return cookie

        runBlocking( Dispatchers.IO ) { fetchIfNeeded() }

        val headerCookie: String = ghostResponseHeaders.getAll(HttpHeaders.SetCookie)
                                                       .orEmpty()
                                                       .joinToString("; ") {
                                                           it.split(";").first()
                                                       }
        cookie = "PREF=hl=en&tz=UTC; SOCS=CAI; $headerCookie"

        return cookie
    }
}