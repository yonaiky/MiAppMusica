package me.knighthat.component

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import it.fast4x.rimusic.R
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.ui.components.themed.SmartMessage

abstract class ImportSongsFromFile(
    private val launcher: ManagedActivityResultLauncher<Array<String>, Uri?>
) {
    abstract val supportedMimes: Array<String>

    fun onShortClick() {
        try {
            launcher.launch( supportedMimes )
        } catch ( _: ActivityNotFoundException ) {
            SmartMessage(
                appContext().resources.getString( R.string.info_not_find_app_open_doc ),
                type = PopupType.Warning, context = appContext()
            )
        }
    }
}