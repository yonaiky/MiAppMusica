package app.kreate.android.themed.common.component.settings.about

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.styling.ColorPalette
import it.fast4x.rimusic.ui.styling.Typography
import kotlinx.serialization.Serializable

@Serializable
data class Translator(
    private val languages: String,
    override val username: String,
    override val displayName: String?,
    override val profileUrl: String,
    override val avatarUrl: String
): InfoCard() {

    override val handle: String = profileUrl.split( "/" ).last()

    override fun isOwner(): Boolean = username == "knighthat"

    @Composable
    override fun TrailingContent( color: Color, fontSize: TextUnit ) {
        Text(
            text = languages,
            style = TextStyle(
                color = color,
                fontSize = fontSize,
            ),
            textAlign = TextAlign.End
        )

        Spacer( Modifier.width(5.dp ) )

        Icon(
            painter = painterResource( R.drawable.translate ),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size( fontSize.value.dp )
        )
    }
}