package app.kreate.android.enums

import androidx.annotation.StringRes
import app.kreate.android.R
import me.knighthat.enums.TextView

enum class PlatformIndicatorType(
    @field:StringRes override val textId: Int
): TextView {

    ICON( R.string.word_icon ),
    DISABLED( R.string.vt_disabled );
}