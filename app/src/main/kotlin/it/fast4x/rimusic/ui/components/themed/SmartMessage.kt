package it.fast4x.rimusic.ui.components.themed

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi
import com.coder.vincent.smart_snackbar.SNACK_BAR_ICON_POSITION_LEFT
import com.coder.vincent.smart_snackbar.SmartSnackBar
import com.coder.vincent.smart_snackbar.bean.SnackBarStyle
import it.fast4x.rimusic.MainActivity
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.MessageType
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.utils.getEnum
import it.fast4x.rimusic.utils.messageTypeKey
import it.fast4x.rimusic.utils.preferences
import it.fast4x.rimusic.utils.toast
import it.fast4x.rimusic.utils.toastLong

@OptIn(UnstableApi::class)
fun SmartMessage(
    message: String,
    type: PopupType? = PopupType.Info,
    backgroundColor: Color? = Color.DarkGray,
    durationLong: Boolean? = false,
    context: Context,
) {
    if (context.preferences.getEnum(messageTypeKey, MessageType.Modern) == MessageType.Modern) {
        val smartMessage = SmartSnackBar.bottom(context as MainActivity)

        smartMessage.config()
            //.backgroundColor(backgroundColor.hashCode())
            .themeStyle(SnackBarStyle.AUTO)
            .icon(
                when (type) {
                    PopupType.Info, PopupType.Success  -> R.drawable.information
                    PopupType.Error, PopupType.Warning -> R.drawable.alert
                    else -> R.drawable.information
                }
            )
            .iconSizeDp(24f)
            .iconPosition(SNACK_BAR_ICON_POSITION_LEFT)
            .apply()
        if (durationLong == true) smartMessage.showLong(message) else smartMessage.show(message)

    } else
        if (durationLong == true) context.toastLong(message) else context.toast(message)
}

