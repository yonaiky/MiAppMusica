package app.kreate.android.service

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.runBlocking
import java.io.InputStream

@UnstableApi
class KtorHttpDatasource(
    private val client: HttpClient
): DataSource {
    private var uri: String? = null
    private var inputStream: InputStream? = null
    private var bytesRemaining: Long = 0

    override fun addTransferListener(transferListener: TransferListener) {
        // Optional: notify about transfers
    }

    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri.toString()

        val response = runBlocking {
            client.get( uri!! )
        }

        if (response.status.value !in 200..299 && response.status.value != 206) {
            throw java.io.IOException("Unexpected response code: ${response.status}")
        }

        inputStream = runBlocking {
            response.bodyAsChannel().toInputStream()
        }
        bytesRemaining = response.contentLength() ?: C.LENGTH_UNSET.toLong()
        return bytesRemaining
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val stream = inputStream ?: return C.RESULT_END_OF_INPUT
        val read = stream.read(buffer, offset, length)
        if (read == -1) return C.RESULT_END_OF_INPUT
        if (bytesRemaining != C.LENGTH_UNSET.toLong()) bytesRemaining -= read
        return read
    }

    override fun getUri(): Uri? = uri?.toUri()

    override fun close() {
        inputStream?.close()
        inputStream = null
        uri = null
    }

    class Factory(
        private val client: HttpClient
    ): DataSource.Factory {
        override fun createDataSource(): DataSource = KtorHttpDatasource(client)
    }
}