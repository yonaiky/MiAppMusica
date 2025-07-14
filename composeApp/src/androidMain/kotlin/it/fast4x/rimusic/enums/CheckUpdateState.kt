package it.fast4x.rimusic.enums

import androidx.annotation.StringRes
import app.kreate.android.R
import me.knighthat.enums.TextView

enum class CheckUpdateState(
    @field:StringRes override val textId: Int
): TextView {

    DOWNLOAD_INSTALL( R.string.enabled ),
    DISABLED( R.string.vt_disabled ),
    ASK( R.string.ask );
}