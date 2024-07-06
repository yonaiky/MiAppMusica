package it.fast4x.rimusic.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import io.ktor.http.Url
import it.fast4x.rimusic.MainApplication
import it.fast4x.rimusic.models.PipedSession

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
    return PipedSession(
        instanceName = MainApplication.pipedInstanceName ?: "",
        apiBaseUrl = Url(MainApplication.pipedApiBaseUrl ?: ""),
        token = MainApplication.pipedApiToken ?: "",
        username = MainApplication.pipedUsername ?: ""
    )
}