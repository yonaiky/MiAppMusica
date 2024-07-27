package it.fast4x.rimusic.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.components.themed.SmartToast

@Composable
fun TextCopyToClipboard(textCopied:String) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    runCatching {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", textCopied))
    }.onFailure {
        //SmartToast("Failed to copy text to clipbaoard ${it.stackTraceToString()}", type = PopupType.Error)
        SmartMessage("Failed to copy text to clipbaoard ${it.stackTraceToString()}", type = PopupType.Error, context = context)
    }
    // Only show a toast for Android 12 and lower.
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
        //SmartToast(context.resources.getString(R.string.value_copied), type = PopupType.Info)
        SmartMessage(context.resources.getString(R.string.value_copied), type = PopupType.Info, context = context)
}

@Composable
fun textCopyFromClipboard(): String {
    val context = LocalContext.current
    var textCopied by remember { mutableStateOf("") }
    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    runCatching {
        textCopied = clipboardManager.primaryClip?.getItemAt(0)?.coerceToText(context).toString()
    }.onFailure {
        //SmartToast("Failed to copy text from clipbaoard ${it.stackTraceToString()}", type = PopupType.Error)
        SmartMessage("Failed to copy text from clipbaoard ${it.stackTraceToString()}", type = PopupType.Error, context = context)
    }
    // Only show a toast for Android 12 and lower.
    //if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
    //    SmartToast(context.resources.getString(R.string.value_copied), type = PopupType.Info)

    return textCopied
}