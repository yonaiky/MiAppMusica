package app.kreate.android.service.newpipe

import app.kreate.android.service.NetworkService
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import me.knighthat.innertube.UserAgents
import org.jetbrains.annotations.Blocking
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException

class NewPipeDownloader: Downloader() {

    @Blocking
    override fun execute( request: Request ): Response = runBlocking {
        val result = NetworkService.client.request( request.url() ) {
            accept( ContentType.Application.Json )
            contentType( ContentType.Application.Json )
            method = HttpMethod.parse( request.httpMethod() )

            // Only setBody when it's not null
            request.dataToSend()?.also( this::setBody )
            // Add headers
            headers {
                this.append( "User-Agent", UserAgents.CHROME_WINDOWS )
                request.headers().forEach( this::appendAll )
            }
        }

        if( result.status.value == 429 )
            throw ReCaptchaException("reCaptcha Challenge requested", request.url())

        val responseHeaders = result.headers.entries().associate { (k, v) -> k to v }

        Response(
            result.status.value,
            "",
            responseHeaders,
            result.bodyAsText(),
            request.url()
        )
    }
}