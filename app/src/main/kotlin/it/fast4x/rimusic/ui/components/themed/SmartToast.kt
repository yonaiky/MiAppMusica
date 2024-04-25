package it.fast4x.rimusic.ui.components.themed

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.coder.vincent.smart_toast.SmartToast
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType

fun SmartToast(
    message: String,
    type: PopupType? = PopupType.Info,
    backgroundColor: Color? = Color.DarkGray,
    durationLong: Boolean? = false
) {
  var smartToast =  SmartToast.emotion()
      backgroundColor?.let {
          smartToast.config()
              .backgroundColor(backgroundColor.hashCode())
              .commit()
      }
    when(type) {
        PopupType.Info -> if (durationLong == true) smartToast.infoLong(message) else smartToast.info(message)
        PopupType.Error -> if (durationLong == true) smartToast.errorLong(message) else smartToast.error(message)
        PopupType.Warning -> if (durationLong == true) smartToast.warningLong(message) else smartToast.warning(message)
        else -> if (durationLong == true) smartToast.completeLong(message) else smartToast.complete(message)
    }


}