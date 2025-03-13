package it.fast4x.rimusic.enums

import androidx.annotation.StringRes
import app.kreate.android.R
import me.knighthat.enums.TextView

enum class TransitionEffect(
    @field:StringRes override val textId: Int
): TextView {

    SlideVertical( R.string.te_slide_horizontal ),

    SlideHorizontal( R.string.te_slide_vertical ),

    Scale( R.string.te_scale ),

    Fade( R.string.te_fade ),

    Expand( R.string.te_expand ),

    None( R.string.none );
}