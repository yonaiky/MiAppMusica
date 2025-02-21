package me.knighthat.component

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.ui.components.themed.IDialog
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class ExportToFileDialog(
    valueState: MutableState<String>,
    activeState: MutableState<Boolean>,
    private val launcher: ManagedActivityResultLauncher<String, Uri?>
): IDialog {

    abstract val extension: String

    override var value: String by valueState
    override var isActive: Boolean by activeState

    override fun onSet( newValue: String ) {
        /**
         *  Create another reference so any changes applied
         *  to this variable instead of [newValue].
         *  So that [newValue] remains immutable
         */
        var fileName = newValue

        // If user didn't indicate a name, apply date as replacement
        if( newValue.isBlank() ) {
            val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault() )
            fileName = "RMPlaylist_${dateFormat.format( Date() )}"
        }

        // Add extension
        fileName += this.extension

        try {
            launcher.launch( fileName )
        } catch ( _: ActivityNotFoundException) {
            SmartMessage(
                "Couldn't find an application to create documents",
                type = PopupType.Warning,
                context = appContext()
            )
        } finally { onDismiss() }
    }
}