package me.knighthat.component

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import it.fast4x.rimusic.R
import me.knighthat.component.dialog.InputDialogConstraints
import me.knighthat.component.dialog.TextInputDialog
import me.knighthat.utils.Toaster
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class ExportToFileDialog(
    valueState: MutableState<TextFieldValue>,
    activeState: MutableState<Boolean>,
    private val launcher: ManagedActivityResultLauncher<String, Uri?>
): TextInputDialog(InputDialogConstraints.ALL, false) {

    abstract val extension: String
    override val keyboardOption: KeyboardOptions = KeyboardOptions.Default

    override var value: TextFieldValue by valueState
    override var isActive: Boolean by activeState

    override fun onSet( newValue: String ) {
        super.onSet( newValue )
        if( errorMessage.isNotEmpty() ) return

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
            Toaster.e( R.string.info_not_find_app_open_doc )
        } finally { hideDialog() }
    }
}