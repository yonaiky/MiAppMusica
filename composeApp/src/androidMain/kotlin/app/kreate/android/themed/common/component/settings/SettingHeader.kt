package app.kreate.android.themed.common.component.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.utils.secondary
import it.fast4x.rimusic.utils.semiBold

@Composable
fun SettingHeader(
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    subtitle: String
) {
    val underlineColor = colorPalette().textDisabled.copy( .6f )
    Column(
        modifier.drawBehind {
                    drawLine(
                        color = underlineColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )
                }
                .padding( bottom = 10.dp )
    ) {
        BasicText(
            text = stringResource( titleId ).uppercase(),
            style = typography().m
                                .semiBold
                                .copy( colorPalette().accent ),
        )

        if ( subtitle.isNotBlank() )
            BasicText(
                text = subtitle,
                style = typography().xxs.secondary,
                modifier = Modifier.padding( start = 3.dp )
            )
    }
}
