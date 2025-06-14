package app.kreate.android.themed.common.component.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import app.kreate.android.themed.rimusic.component.settings.RiMusicAnimatedHeader
import app.kreate.android.themed.vimusic.component.settings.ViMusicAnimatedHeader
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.components.tab.toolbar.Icon
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.utils.bold
import it.fast4x.rimusic.utils.conditional
import it.fast4x.rimusic.utils.secondary
import it.fast4x.rimusic.utils.semiBold

class SettingEntrySearch(
    private val scrollableState: ScrollableState,
    @StringRes private val titleId: Int,
    @DrawableRes override val iconId: Int
): Icon {

    companion object {
        const val ICON_PADDING = 7
    }

    val inputValue: String
        get() = input.text

    var input: TextFieldValue by mutableStateOf( TextFieldValue() )
    var isVisible: Boolean by mutableStateOf( false )
    var isFocused: Boolean by mutableStateOf( false )

    @Composable
    private fun DecorationBox(
        innerTextField: @Composable () -> Unit,
        onBackClick: () -> Unit
    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            // Placeholder
            BasicText(
                text = stringResource(R.string.search),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = typography().xs
                                    .semiBold
                                    .secondary
                                    .copy(
                                        color = colorPalette().textDisabled
                                    ),
                modifier = Modifier.conditional( inputValue.isNotBlank() ) { alpha( 0f ) }
            )

            // Actual text from user
            innerTextField()
        }
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.padding( start = 30.dp, end = 15.dp )
        ) {
            IconButton(
                onClick = onBackClick,
                icon = R.drawable.backspace_outline,
                color = colorPalette().text.copy( alpha = .8f ), // A little dimmer to prevent eye-candy
                modifier = Modifier.align( Alignment.CenterEnd )
                                   .size( 16.dp )
            )
        }
    }

    fun contains( text: String ) = inputValue.isBlank() || text.contains( inputValue, true )

    @Composable
    fun contains( @StringRes textId: Int ) = contains( stringResource( textId ) )

    @Composable
    fun HeaderIcon( modifier: Modifier ) =
        Icon(
            painter = painterResource( iconId ),
            contentDescription = stringResource( titleId ),
            tint = colorPalette().accent,
            modifier = modifier.size( 22.dp )
                               .clickable( onClick = ::onShortClick )
        )

    @Composable
    fun HeaderText( textAlign: TextAlign, modifier: Modifier = Modifier ) {
        BasicText(
            text = stringResource( titleId ),
            style = TextStyle(
                fontSize = typography().xxl.bold.fontSize,
                fontWeight = typography().xxl.bold.fontWeight,
                color = colorPalette().text,
                textAlign = textAlign
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier.clickable {
                scrollableState.let {
                    if( it is LazyGridState )
                        it.requestScrollToItem( 0, 0 )
                    else if( it is LazyListState )
                        it.requestScrollToItem( 0, 0 )
                }
            }
        )
    }

    @Composable
    fun SearchBar() {
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        // Auto focus on search bar when it's visible
        LaunchedEffect( isVisible, isFocused ) {
            if( !isVisible ) return@LaunchedEffect

            if( isFocused )
                focusRequester.requestFocus()
            else {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        }

        BasicTextField(
            value = input,
            onValueChange = {
                input = it.copy(
                    text = it.text,
                    selection = it.selection
                )
            },
            textStyle = typography().xs.semiBold,
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Search ),
            keyboardActions = KeyboardActions(onSearch = {
                isVisible = inputValue.isNotBlank()
                isFocused = false
                keyboardController?.hide()
            }),
            cursorBrush = SolidColor( colorPalette().text ),
            decorationBox = {
                DecorationBox( it ) {
                    input = TextFieldValue( "" )

                    // Regain focus in case keyboard is hidden
                    isFocused = true
                }
            },
            modifier = Modifier.height( 30.dp )
                               .fillMaxWidth()
                               .focusRequester( focusRequester )
        )
    }

    override fun onShortClick() {
        isVisible = !isVisible
        isFocused = isVisible
    }

    @Composable
    override fun ToolBarButton() {
        // Scroll to top every time search value changes
        LaunchedEffect( input ) {
            if( scrollableState is LazyGridState )
                scrollableState.requestScrollToItem( 0, 0 )
            else if( scrollableState is LazyListState )
                scrollableState.requestScrollToItem( 0, 0 )
        }

        when( UiType.current() ) {
            UiType.RiMusic -> RiMusicAnimatedHeader()
            UiType.ViMusic -> ViMusicAnimatedHeader()
        }
    }
}