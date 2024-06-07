package it.fast4x.rimusic.utils

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCancellationException
import io.ktor.http.Url
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val executor = Executors.newCachedThreadPool()
private val coroutineScope = CoroutineScope(
    executor.asCoroutineDispatcher() + SupervisorJob() + CoroutineName("androidx-credentials-util")
)

private suspend inline fun <T> wrapper(
    crossinline block: (cont: CancellableContinuation<T>) -> Unit
): T = withContext(coroutineScope.coroutineContext) {
    runCatching {
        suspendCancellableCoroutine { cont ->
            runCatching {
                block(cont)
            }.exceptionOrNull()?.let {
                if (it is CancellationException) cont.cancel() else cont.resumeWithException(it)
            }
        }
    }.let {
        it.exceptionOrNull()?.printStackTrace()
        it.getOrThrow()
    }
}

private inline fun <
        reified Response : Any,
        reified Exception : Throwable,
        reified CancellationException : Exception
        > callback(
    cont: CancellableContinuation<Response>
) = object : CredentialManagerCallback<Response, Exception> {
    override fun onError(e: Exception) {
        if (e is CancellationException) cont.cancel(e) else cont.resumeWithException(e)
    }

    override fun onResult(result: Response) = cont.resume(result)
}

suspend fun CredentialManager.upsert(
    context: Context,
    username: String,
    password: String
) = wrapper { cont ->
    createCredentialAsync(
        context = context,
        request = CreatePasswordRequest(
            id = username,
            password = password
        ),
        cancellationSignal = null, //cont.asCancellationSignal,
        executor = executor,
        callback = callback<_, _, CreateCredentialCancellationException>(cont)
    )
}

suspend fun CredentialManager.get(context: Context) = wrapper { cont ->
    getCredentialAsync(
        context = context,
        request = GetCredentialRequest(listOf(GetPasswordOption())),
        cancellationSignal = null, //cont.asCancellationSignal,
        executor = executor,
        callback = callback<_, _, GetCredentialCancellationException>(cont)
    )
}.let { runCatching { it.credential as? PasswordCredential }.getOrNull() }


data class PipedSession(
    val instanceName: String,
    val apiBaseUrl: Url,
    val token: String,
    val username: String
) {
    fun serialize() = "$instanceName|$apiBaseUrl|$token|$username"
}