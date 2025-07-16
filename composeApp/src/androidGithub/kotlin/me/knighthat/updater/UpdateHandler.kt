package me.knighthat.updater

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import app.kreate.android.Preferences
import it.fast4x.rimusic.enums.CheckUpdateState

@Composable
fun UpdateHandler() {
    DownloadAndInstallDialog.Render()
    NewUpdatePrompt.Render()

    val check4UpdateState by Preferences.CHECK_UPDATE
    LaunchedEffect( check4UpdateState ) {
        if( check4UpdateState != CheckUpdateState.DISABLED )
            Updater.checkForUpdate()
    }
}