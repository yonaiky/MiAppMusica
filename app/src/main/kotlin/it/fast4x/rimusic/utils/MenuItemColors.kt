package it.fast4x.rimusic.utils

import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.favoritesIcon

@Composable
fun menuItemColors(): MenuItemColors {
    val (colorPalette, _) = LocalAppearance.current
    return MenuItemColors(
        leadingIconColor =  colorPalette.favoritesIcon,
        trailingIconColor =  colorPalette.favoritesIcon,
        textColor = colorPalette.text,
        disabledTextColor = colorPalette.textDisabled,
        disabledLeadingIconColor = colorPalette.textDisabled,
        disabledTrailingIconColor = colorPalette.textDisabled,
    )

}