package app.kreate.android.themed.common.screens.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingHeader
import it.fast4x.rimusic.enums.ColorPaletteMode
import it.fast4x.rimusic.enums.ColorPaletteName
import it.fast4x.rimusic.ui.components.themed.ConfirmationDialog
import it.fast4x.rimusic.ui.screens.settings.ButtonBarSettingEntry
import it.fast4x.rimusic.ui.screens.settings.ColorSettingEntry
import it.fast4x.rimusic.ui.styling.DefaultDarkColorPalette
import it.fast4x.rimusic.ui.styling.DefaultLightColorPalette

@Composable
fun ThemeSettings(modifier: Modifier = Modifier) {
    var colorPaletteName by Settings.COLOR_PALETTE
    var colorPaletteMode by Settings.THEME_MODE
    var customColor by Settings.CUSTOM_COLOR_HASH_CODE
    var customThemeLight_Background0 by Settings.CUSTOM_LIGHT_THEME_BACKGROUND_0_HASH_CODE
    var customThemeLight_Background1 by Settings.CUSTOM_LIGHT_THEME_BACKGROUND_1_HASH_CODE
    var customThemeLight_Background2 by Settings.CUSTOM_LIGHT_THEME_BACKGROUND_2_HASH_CODE
    var customThemeLight_Background3 by Settings.CUSTOM_LIGHT_THEME_BACKGROUND_3_HASH_CODE
    var customThemeLight_Background4 by Settings.CUSTOM_LIGHT_THEME_BACKGROUND_4_HASH_CODE
    var customThemeLight_Text by Settings.CUSTOM_LIGHT_TEXT_HASH_CODE
    var customThemeLight_TextSecondary by Settings.CUSTOM_LIGHT_TEXT_SECONDARY_HASH_CODE
    var customThemeLight_TextDisabled by Settings.CUSTOM_LIGHT_TEXT_DISABLED_HASH_CODE
    var customThemeLight_IconButtonPlayer by Settings.CUSTOM_LIGHT_PLAY_BUTTON_HASH_CODE
    var customThemeLight_Accent by Settings.CUSTOM_LIGHT_ACCENT_HASH_CODE

    var customThemeDark_Background0 by Settings.CUSTOM_DARK_THEME_BACKGROUND_0_HASH_CODE
    var customThemeDark_Background1 by Settings.CUSTOM_DARK_THEME_BACKGROUND_1_HASH_CODE
    var customThemeDark_Background2 by Settings.CUSTOM_DARK_THEME_BACKGROUND_2_HASH_CODE
    var customThemeDark_Background3 by Settings.CUSTOM_DARK_THEME_BACKGROUND_3_HASH_CODE
    var customThemeDark_Background4 by Settings.CUSTOM_DARK_THEME_BACKGROUND_4_HASH_CODE
    var customThemeDark_Text by Settings.CUSTOM_DARK_TEXT_HASH_CODE
    var customThemeDark_TextSecondary by Settings.CUSTOM_DARK_TEXT_SECONDARY_HASH_CODE
    var customThemeDark_TextDisabled by Settings.CUSTOM_DARK_TEXT_DISABLED_HASH_CODE
    var customThemeDark_IconButtonPlayer by Settings.CUSTOM_DARK_PLAY_BUTTON_HASH_CODE
    var customThemeDark_Accent by Settings.CUSTOM_DARK_ACCENT_HASH_CODE

    var resetCustomLightThemeDialog by rememberSaveable { mutableStateOf(false) }
    var resetCustomDarkThemeDialog by rememberSaveable { mutableStateOf(false) }

    if (resetCustomLightThemeDialog) {
        ConfirmationDialog(
            text = stringResource(R.string.do_you_really_want_to_reset_the_custom_light_theme_colors),
            onDismiss = { resetCustomLightThemeDialog = false },
            onConfirm = {
                resetCustomLightThemeDialog = false
                customThemeLight_Background0 = DefaultLightColorPalette.background0.hashCode()
                customThemeLight_Background1 = DefaultLightColorPalette.background1.hashCode()
                customThemeLight_Background2 = DefaultLightColorPalette.background2.hashCode()
                customThemeLight_Background3 = DefaultLightColorPalette.background3.hashCode()
                customThemeLight_Background4 = DefaultLightColorPalette.background4.hashCode()
                customThemeLight_Text = DefaultLightColorPalette.text.hashCode()
                customThemeLight_TextSecondary = DefaultLightColorPalette.textSecondary.hashCode()
                customThemeLight_TextDisabled = DefaultLightColorPalette.textDisabled.hashCode()
                customThemeLight_IconButtonPlayer = DefaultLightColorPalette.iconButtonPlayer.hashCode()
                customThemeLight_Accent = DefaultLightColorPalette.accent.hashCode()
            }
        )
    }

    if (resetCustomDarkThemeDialog) {
        ConfirmationDialog(
            text = stringResource(R.string.do_you_really_want_to_reset_the_custom_dark_theme_colors),
            onDismiss = { resetCustomDarkThemeDialog = false },
            onConfirm = {
                resetCustomDarkThemeDialog = false
                customThemeDark_Background0 = DefaultDarkColorPalette.background0.hashCode()
                customThemeDark_Background1 = DefaultDarkColorPalette.background1.hashCode()
                customThemeDark_Background2 = DefaultDarkColorPalette.background2.hashCode()
                customThemeDark_Background3 = DefaultDarkColorPalette.background3.hashCode()
                customThemeDark_Background4 = DefaultDarkColorPalette.background4.hashCode()
                customThemeDark_Text = DefaultDarkColorPalette.text.hashCode()
                customThemeDark_TextSecondary = DefaultDarkColorPalette.textSecondary.hashCode()
                customThemeDark_TextDisabled = DefaultDarkColorPalette.textDisabled.hashCode()
                customThemeDark_IconButtonPlayer = DefaultDarkColorPalette.iconButtonPlayer.hashCode()
                customThemeDark_Accent = DefaultDarkColorPalette.accent.hashCode()
            }
        )
    }

    SettingComponents.EnumEntry(
        Settings.COLOR_PALETTE,
        R.string.theme
    ) {
        if( colorPaletteName == ColorPaletteName.ModernBlack )
            colorPaletteMode = ColorPaletteMode.System
    }

    AnimatedVisibility(visible = colorPaletteName == ColorPaletteName.CustomColor) {
        Column(
            Modifier.padding( start = 25.dp )
        ) {
            ColorSettingEntry(
                title = stringResource(R.string.customcolor),
                text = "",
                color = Color(customColor),
                onColorSelected = {
                    customColor = it.hashCode()
                }
            )
            SettingComponents.Description( R.string.restarting_rimusic_is_required )
        }
    }
    AnimatedVisibility(visible = colorPaletteName == ColorPaletteName.Customized) {
        Column {
            SettingHeader( R.string.title_customized_light_theme_colors )

            ButtonBarSettingEntry(
                title = stringResource(R.string.title_reset_customized_light_colors),
                text = stringResource(R.string.info_click_to_reset_default_light_colors),
                icon = R.drawable.trash,
                onClick = { resetCustomLightThemeDialog = true }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_1),
                text = "",
                color = Color(customThemeLight_Background0),
                onColorSelected = {
                    customThemeLight_Background0 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_2),
                text = "",
                color = Color(customThemeLight_Background1),
                onColorSelected = {
                    customThemeLight_Background1 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_3),
                text = "",
                color = Color(customThemeLight_Background2),
                onColorSelected = {
                    customThemeLight_Background2 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_4),
                text = "",
                color = Color(customThemeLight_Background3),
                onColorSelected = {
                    customThemeLight_Background3 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_5),
                text = "",
                color = Color(customThemeLight_Background4),
                onColorSelected = {
                    customThemeLight_Background4 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_text),
                text = "",
                color = Color(customThemeLight_Text),
                onColorSelected = {
                    customThemeLight_Text= it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_text_secondary),
                text = "",
                color = Color(customThemeLight_TextSecondary),
                onColorSelected = {
                    customThemeLight_TextSecondary = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_text_disabled),
                text = "",
                color = Color(customThemeLight_TextDisabled),
                onColorSelected = {
                    customThemeLight_TextDisabled = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_icon_button_player),
                text = "",
                color = Color(customThemeLight_IconButtonPlayer),
                onColorSelected = {
                    customThemeLight_IconButtonPlayer = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_accent),
                text = "",
                color = Color(customThemeLight_Accent),
                onColorSelected = {
                    customThemeLight_Accent = it.hashCode()
                }
            )

            SettingHeader( R.string.title_customized_dark_theme_colors )

            ButtonBarSettingEntry(
                title = stringResource(R.string.title_reset_customized_dark_colors),
                text = stringResource(R.string.click_to_reset_default_dark_colors),
                icon = R.drawable.trash,
                onClick = { resetCustomDarkThemeDialog = true }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_1),
                text = "",
                color = Color(customThemeDark_Background0),
                onColorSelected = {
                    customThemeDark_Background0 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_2),
                text = "",
                color = Color(customThemeDark_Background1),
                onColorSelected = {
                    customThemeDark_Background1 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_3),
                text = "",
                color = Color(customThemeDark_Background2),
                onColorSelected = {
                    customThemeDark_Background2 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_4),
                text = "",
                color = Color(customThemeDark_Background3),
                onColorSelected = {
                    customThemeDark_Background3 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_background_5),
                text = "",
                color = Color(customThemeDark_Background4),
                onColorSelected = {
                    customThemeDark_Background4 = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_text),
                text = "",
                color = Color(customThemeDark_Text),
                onColorSelected = {
                    customThemeDark_Text= it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_text_secondary),
                text = "",
                color = Color(customThemeDark_TextSecondary),
                onColorSelected = {
                    customThemeDark_TextSecondary = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_text_disabled),
                text = "",
                color = Color(customThemeDark_TextDisabled),
                onColorSelected = {
                    customThemeDark_TextDisabled = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_icon_button_player),
                text = "",
                color = Color(customThemeDark_IconButtonPlayer),
                onColorSelected = {
                    customThemeDark_IconButtonPlayer = it.hashCode()
                }
            )
            ColorSettingEntry(
                title = stringResource(R.string.color_accent),
                text = "",
                color = Color(customThemeDark_Accent),
                onColorSelected = {
                    customThemeDark_Accent = it.hashCode()
                }
            )
        }
    }
}