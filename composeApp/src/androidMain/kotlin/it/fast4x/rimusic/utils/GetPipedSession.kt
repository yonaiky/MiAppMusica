package it.fast4x.rimusic.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import io.ktor.http.Url
import it.fast4x.rimusic.MainApplication
import it.fast4x.rimusic.models.PipedSession
import timber.log.Timber

/*
@Composable
fun getPipedSession(): PipedSession {
    val pipedUsername by rememberEncryptedPreference(pipedUsernameKey, "")
    val pipedInstanceName by rememberEncryptedPreference(pipedInstanceNameKey, "")
    val pipedApiBaseUrl by rememberEncryptedPreference(pipedApiBaseUrlKey, "")
    val pipedApiToken by rememberEncryptedPreference(pipedApiTokenKey, "")
    return PipedSession(
        instanceName = pipedInstanceName,
        apiBaseUrl = Url(pipedApiBaseUrl),
        token = pipedApiToken,
        username = pipedUsername
    )
}
*/

@Composable
fun getPipedSession(): PipedSession {
    val context = LocalContext.current
    val pipedSession = PipedSession(
        instanceName = "",
        apiBaseUrl = Url(""),
        token = "",
        username = ""
    )
    if (context.preferences.getBoolean(isPipedEnabledKey, false)) {
        runCatching {
            with(context.encryptedPreferences) {
                pipedSession.username = getString(pipedUsernameKey, "").toString()
                pipedSession.instanceName = getString(pipedInstanceNameKey, "").toString()
                pipedSession.apiBaseUrl = Url(getString(pipedApiBaseUrlKey, "").toString())
                pipedSession.token = getString(pipedApiTokenKey, "").toString()
            }
        }.onFailure {
            Timber.e("GetPipedSession get encryptedPreferences error ${it.stackTraceToString()}")
        }
    }

    return pipedSession

    /*
    return PipedSession(
        instanceName = MainApplication.pipedInstanceName ?: "",
        apiBaseUrl = Url(MainApplication.pipedApiBaseUrl ?: ""),
        token = MainApplication.pipedApiToken ?: "",
        username = MainApplication.pipedUsername ?: ""
    )
     */
}