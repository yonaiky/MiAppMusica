package app.kreate.android.themed.common.component.settings.about

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import it.fast4x.rimusic.ui.styling.favoritesIcon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Developer(
    private val id: Int,
    private val contributions: Int?,
    @SerialName("login") override val username: String,
    @SerialName("name") override val displayName: String?,
    @SerialName("html_url") override val profileUrl: String,
    @SerialName("avatar_url") override val avatarUrl: String
): InfoCard() {

    override val handle: String = profileUrl.split( "/" ).last()

    @Composable
    override fun TrailingContent( color: Color, fontSize: TextUnit ) {
        Text(
            text = contributions.toString(),
            style = TextStyle(
                color = color,
                fontSize = fontSize,
            ),
            textAlign = TextAlign.End
        )

        Spacer( Modifier.width(5.dp ) )

        Icon(
            painter = painterResource( R.drawable.git_pull_request_outline ),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size( fontSize.value.dp )
        )
    }

    override fun isOwner(): Boolean = id == 1484476
}
