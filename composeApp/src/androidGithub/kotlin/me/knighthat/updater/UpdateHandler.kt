package me.knighthat.updater

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import app.kreate.android.Preferences
import it.fast4x.rimusic.enums.CheckUpdateState

@Composable
fun UpdateHandler() {
    NewUpdateAvailableDialog.Render()
    CheckForUpdateDialog.Render()

    val check4UpdateState by Preferences.CHECK_UPDATE
    LaunchedEffect( check4UpdateState ) {
        when( check4UpdateState ) {
            CheckUpdateState.DOWNLOAD_INSTALL  -> if( !NewUpdateAvailableDialog.isCancelled ) Updater.checkForUpdate()
            CheckUpdateState.ASK      -> CheckForUpdateDialog.isActive = true
            CheckUpdateState.DISABLED -> { /* Does nothing */ }
        }
    }
}