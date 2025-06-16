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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import app.kreate.android.Preferences
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.components.themed.Switch
import it.fast4x.rimusic.utils.semiBold
import me.knighthat.component.dialog.Dialog
import me.knighthat.component.dialog.RestartAppDialog
import me.knighthat.component.dialog.TextInputDialog
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

    /**
     * Space between header and above component
     */
    const val HEADER_SPACING = 24

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
            modifier = modifier.padding( horizontal = DEFAULT_HORIZONTAL_PADDING.dp )
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
            modifier = modifier.padding( all = DEFAULT_HORIZONTAL_PADDING.dp )
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
                    BasicText(
                        text = subtitle,
                        maxLines = 2,
                        style = typography().xs
                                            .semiBold
                                            .copy( colorPalette().textSecondary )
                    )
            }

            trailingContent()
        }

    @Composable
    inline fun <reified T: Enum<T>> EnumEntry(
        preference: Preferences.Enum<T>,
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
            Column(
                Modifier.wrapContentHeight()
                        .verticalScroll( rememberScrollState() )
            ) {
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
            modifier = modifier,
            // Default to show current value's name if no subtitle is provided
            subtitle = subtitle.ifBlank { getName( preference.value ) },
            isEnabled = isEnabled,
            trailingContent = trailingContent
        )
    }

    @Composable
    inline fun <reified T> EnumEntry(
        preference: Preferences.Enum<T>,
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
        preference: Preferences.Enum<T>,
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
        preference: Preferences.Enum<T>,
        @StringRes titleId: Int,
        modifier: Modifier = Modifier,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
        noinline trailingContent: @Composable () -> Unit = {},
        crossinline onValueChanged: (T) -> Unit = {}
    ) where T: Enum<T>, T: TextView =
        EnumEntry( preference, stringResource( titleId ), modifier, subtitle, isEnabled, action, trailingContent, onValueChanged )

    @Composable
    fun BooleanEntry(
        preference: Preferences.Boolean,
        title: String,
        modifier: Modifier = Modifier,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
        onValueChanged: (Boolean) -> Unit = {}
    ) =
        Text(
            title = title,
            onClick = {
                onValueChanged( preference.flip() )

                if ( action == Action.RESTART_APP )
                    RestartAppDialog.showDialog()
            },
            modifier = modifier,
            subtitle = subtitle,
            isEnabled = isEnabled,
            trailingContent = { Switch( preference.value ) }
        )

    @Composable
    fun BooleanEntry(
        preference: Preferences.Boolean,
        @StringRes titleId: Int,
        modifier: Modifier = Modifier,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
        onValueChanged: (Boolean) -> Unit = {}
    ) = BooleanEntry(
        preference, stringResource( titleId ), modifier, subtitle, isEnabled, action, onValueChanged
    )

    @Composable
    fun BooleanEntry(
        preference: Preferences.Boolean,
        @StringRes titleId: Int,
        @StringRes subtitleId: Int,
        modifier: Modifier = Modifier,
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
        onValueChanged: (Boolean) -> Unit = {}
    ) = BooleanEntry(
        preference, stringResource( titleId ), modifier, stringResource( subtitleId ), isEnabled, action, onValueChanged
    )

    @Composable
    private fun <T> InputDialogEntry(
        preference: Preferences<T>,
        title: String,
        constraint: String,
        onValueChanged: (String) -> Unit,
        modifier: Modifier = Modifier,
        keyboardOption: KeyboardOptions = KeyboardOptions.Default,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
    ) {
        val dialog = remember {
            object : TextInputDialog(constraint) {
                override val keyboardOption: KeyboardOptions = keyboardOption
                override val dialogTitle: String
                    @Composable
                    get() = title

                override var value: TextFieldValue by mutableStateOf( TextFieldValue(preference.value.toString()) )
                override var isActive: Boolean by mutableStateOf( false )

                override fun onSet( newValue: String ) {
                    super.onSet(newValue)

                    onValueChanged( newValue )

                    hideDialog()
                }

                override fun hideDialog() {
                    super.hideDialog()
                    // Some processing might have happened after the value is set,
                    // setting this back to match preference's value is best idea
                    value = TextFieldValue(preference.value.toString())
                }
            }
        }
        dialog.Render()

        Text(
            title = title,
            onClick = dialog::showDialog,
            modifier = modifier,
            subtitle = subtitle.ifBlank { preference.value.toString() },
            isEnabled = isEnabled
        )
    }

    @Composable
    fun InputDialogEntry(
        preference: Preferences.String,
        title: String,
        constraint: String,
        modifier: Modifier = Modifier,
        keyboardOption: KeyboardOptions = KeyboardOptions.Default,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
    ) =
        InputDialogEntry(
            preference, title, constraint, { preference.value = it }, modifier, keyboardOption, subtitle, isEnabled, action
        )

    @Composable
    fun InputDialogEntry(
        preference: Preferences.String,
        @StringRes titleId: Int,
        constraint: String,
        modifier: Modifier = Modifier,
        keyboardOption: KeyboardOptions = KeyboardOptions.Default,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
    ) =
        InputDialogEntry(
            preference, stringResource( titleId ), constraint, { preference.value = it }, modifier, keyboardOption, subtitle, isEnabled, action
        )

    @Composable
    fun <T: Number> InputDialogEntry(
        preference: Preferences<T>,
        title: String,
        constraint: String,
        modifier: Modifier = Modifier,
        keyboardOption: KeyboardOptions = KeyboardOptions.Default,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
    ) {
        fun valueProcessor( newValue: String ) {
            when( preference ) {
                is Preferences.Int -> preference.value = newValue.toInt()
                is Preferences.Long -> preference.value = newValue.toLong()
                is Preferences.Float -> preference.value = newValue.toFloat()
                else -> throw UnsupportedOperationException(
                    "${preference::class} is not supported in <T: Number> InputDialogEntry"
                )
            }
        }

        InputDialogEntry(
            preference, title, constraint, ::valueProcessor, modifier, keyboardOption, subtitle, isEnabled, action
        )
    }

    @Composable
    fun <T: Number> InputDialogEntry(
        preference: Preferences<T>,
        @StringRes titleId: Int,
        constraint: String,
        modifier: Modifier = Modifier,
        keyboardOption: KeyboardOptions = KeyboardOptions.Default,
        subtitle: String = "",
        isEnabled: Boolean = true,
        action: Action = Action.NONE,
    ) =
        InputDialogEntry(
            preference, stringResource( titleId ), constraint, modifier, keyboardOption, subtitle, isEnabled, action
        )

    /**
     * A set of actions to enact once the setting is set.
     */
    enum class Action {

        NONE,
        RESTART_APP,
        RESTART_PLAYER_SERVICE;
    }
}