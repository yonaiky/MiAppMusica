package it.fast4x.rimusic.ui.components.themed

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.coder.vincent.smart_snackbar.SNACK_BAR_ICON_POSITION_LEFT
import com.coder.vincent.smart_snackbar.SmartSnackBar
import it.fast4x.rimusic.MainActivity
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType

@OptIn(UnstableApi::class)
fun SmartMessage(
    message: String,
    type: PopupType? = PopupType.Info,
    backgroundColor: Color? = Color.DarkGray,
    durationLong: Boolean? = false,
    context: Context,
) {
    var smartMessage = SmartSnackBar.bottom(context as MainActivity)

    backgroundColor?.let {
        smartMessage.config()
            .backgroundColor(backgroundColor.hashCode())
            //.backgroundColorResource(com.coder.vincent.smart_toast.R.color.colorPrimaryDark)
            .icon(when (type) {
                PopupType.Info -> R.drawable.information_circle
                PopupType.Error -> R.drawable.alert_circle
                PopupType.Warning -> R.drawable.alert_circle_not_filled
                PopupType.Success -> R.drawable.checked
                null -> R.drawable.information_circle
            }
            )
            .iconSizeDp(24f)
            .iconPosition(SNACK_BAR_ICON_POSITION_LEFT)
            .apply()
    }
    if (durationLong == true) smartMessage.showLong(message) else smartMessage.show(message)
}

