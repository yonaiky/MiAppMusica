package app.kreate.android.themed.common.component.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import app.kreate.android.Settings
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.utils.semiBold
import me.knighthat.component.dialog.Dialog
import me.knighthat.component.dialog.RestartAppDialog
import me.knighthat.enums.TextView

object SettingComponents {

    /**
     * Default indentation, applied to all entries
     */
    private const val DEFAULT_HORIZONTAL_PADDING = 12

    /**
     * Use for subsequent indentation.
     *
     * ```kotlin
     * Modifier.padding( start = SettingComponents.HORIZONTAL_PADDING )
     * ```
     *
     * Avoid using `all` for children's padding, it'll
     * shrink the width, making it more clustered.
     */
    const val HORIZONTAL_PADDING = 12

    /**
     * Space between entries in vertical layout
     */
    const val VERTICAL_SPACING = 12

    @Composable
    fun Description(
        text: String,
        modifier: Modifier = Modifier,
        isImportant: Boolean = false
    ) =
        BasicText(
            text = text,
            maxLines = 2,
            style = typography().xs
                                .semiBold
                                .copy(
                                    if( isImportant )
                                        colorPalette().red
                                    else
                                        colorPalette().textSecondary
                                ),
            modifier = modifier
        )

    @Composable
    fun Description(
        @StringRes textId: Int,
        modifier: Modifier = Modifier,
        isImportant: Boolean = false
    ) = Description( stringResource( textId ), modifier, isImportant )

    @Composable
    fun Text(
        title: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        subtitle: String = "",
        isEnabled: Boolean = true,
        trailingContent: @Composable () -> Unit = {}
    ) =
        Row(
            horizontalArrangement = Arrangement.spacedBy( 16.dp ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding( horizontal = DEFAULT_HORIZONTAL_PADDING.dp )
                               .fillMaxWidth()
                               .clickable(
                                   enabled = isEnabled,
                                   onClick = onClick
                               )
                               .alpha( if ( isEnabled ) 1f else 0.5f )
        ) {
            Column( Modifier.weight( 1f ) ) {
                BasicText(
                    text = title,
                    maxLines = 1,
                    style = typography().xs
                                        .semiBold
                                        .copy( colorPalette().text ),
                    modifier = Modifier.padding( bottom = 4.dp )
                )

                if( subtitle.isNotBlank() )
                    Description( subtitle )
            }

            trailingContent()
        }

    @Composable
    inline fun <reified T: Enum<T>> EnumEntry(
        preference: Settings.Preference.EnumPreference<T>,
        title: String,
        crossinline getName: @Composable (T) -> String,
        modifier: Modifier = Modifier,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
        noinline trailingContent: @Composable () -> Unit = {},
        crossinline onValueChanged: (T) -> Unit = {}
    ) {
        var selected by preference

        /**
         * Putting this inside [Dialog] instance below causes crash (for some reason)
         * The error being [ClassCastException], happens because [enumValues] being
         * used inside an object without `reified T`.
         */
        val enumValues: @Composable Dialog.() -> Unit = {
            enumValues<T>().forEach {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding( vertical = 12.dp, horizontal = 24.dp )
                                       .fillMaxWidth()
                                       .clickable {
                                           selected = it
                                           onValueChanged( it )

                                           if( action == Action.RESTART_APP ) {
                                               hideDialog()
                                               RestartAppDialog.showDialog()
                                           }
                                       }
                ) {
                    val colorPalette = colorPalette()
                    val (inner, outer, width) = remember( selected ) {
                        if( selected == it )
                            Triple(colorPalette.accent, colorPalette.onAccent, 4.dp)
                        else
                            Triple(colorPalette.textDisabled, Color.Transparent, 1.dp)
                    }
                    Canvas(
                        modifier = Modifier.size( 18.dp )
                                           .background( inner, CircleShape )
                    ) {
                        drawCircle(
                            color = outer,
                            radius = width.toPx(),
                            center = size.center
                        )
                    }

                    BasicText(
                        text = getName( it ),
                        style = if( selected == it ) typography().xs.semiBold else typography().xs
                    )
                }
            }

            Spacer( Modifier.height( Dialog.SPACE_BETWEEN_SECTIONS.dp ) )

            if( action == Action.RESTART_APP )
                BasicText(
                    text = stringResource( R.string.restarting_rimusic_is_required ),
                    style = typography().xs.copy(
                        colorPalette().red.copy( .8f )
                    ),
                    modifier = Modifier.fillMaxWidth( .9f )
                )
        }

        val dialog = remember {
            object : Dialog {
                override val dialogTitle: String
                    @Composable
                    get() = title
                override var isActive: Boolean by mutableStateOf(false)

                @Composable
                override fun DialogBody() = enumValues()
            }
        }
        dialog.Render()

        Text(
            title = title,
            onClick = dialog::showDialog,
            modifier = modifier.padding( vertical = HORIZONTAL_PADDING.dp ),
            // Default to show current value's name if no subtitle is provided
            subtitle = subtitle.ifBlank { getName( preference.value ) },
            isEnabled = isEnabled,
            trailingContent = trailingContent
        )
    }

    @Composable
    inline fun <reified T> EnumEntry(
        preference: Settings.Preference.EnumPreference<T>,
        title: String,
        modifier: Modifier = Modifier,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
        noinline trailingContent: @Composable () -> Unit = {},
        crossinline onValueChanged: (T) -> Unit = {}
    ) where T: Enum<T>, T: TextView =
        EnumEntry( preference, title, { it.text }, modifier, subtitle, isEnabled, action, trailingContent, onValueChanged )

    @Composable
    inline fun <reified T: Enum<T>> EnumEntry(
        preference: Settings.Preference.EnumPreference<T>,
        @StringRes titleId: Int,
        crossinline getName: @Composable (T) -> String,
        modifier: Modifier = Modifier,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
        noinline trailingContent: @Composable () -> Unit = {},
        crossinline onValueChanged: (T) -> Unit = {}
    ) =
        EnumEntry( preference, stringResource( titleId ), getName, modifier, subtitle, isEnabled, action, trailingContent, onValueChanged )

    @Composable
    inline fun <reified T> EnumEntry(
        preference: Settings.Preference.EnumPreference<T>,
        @StringRes titleId: Int,
        modifier: Modifier = Modifier,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
        noinline trailingContent: @Composable () -> Unit = {},
        crossinline onValueChanged: (T) -> Unit = {}
    ) where T: Enum<T>, T: TextView =
        EnumEntry( preference, stringResource( titleId ), modifier, subtitle, isEnabled, action, trailingContent, onValueChanged )

    /**
     * A set of actions to enact once the setting is set.
     */
    enum class Action {

        NONE,
        RESTART_APP,
        RESTART_PLAYER_SERVICE;
    }
}