package me.knighthat.component.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kreate.android.R
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.ui.components.themed.DefaultDialog
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.components.themed.SliderControl
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.blurStrengthKey
import it.fast4x.rimusic.utils.playerBackdropKey
import it.fast4x.rimusic.utils.rememberPreference
import me.knighthat.component.dialog.Dialog

class BlurAdjuster private constructor(
    activeState: MutableState<Boolean>,
    strengthState: MutableState<Float>,
    backdropState: MutableState<Float>
): Dialog {

    companion object {
        @Composable
        operator fun invoke() = BlurAdjuster(
            remember { mutableStateOf( false ) },
            rememberPreference( blurStrengthKey, 25f ),
            rememberPreference( playerBackdropKey, 0f ),
        )
    }

    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.controls_title_blur_effect )

    var strength: Float by strengthState
    var backdrop: Float by backdropState
    override var isActive: Boolean by activeState

    fun onDismiss()  { isActive = false }

    @Composable
    override fun DialogBody() {
        DefaultDialog( ::onDismiss ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { strength = 25f },
                    icon = R.drawable.drop_blur,
                    color = colorPalette().favoritesIcon,
                    modifier = Modifier.size( 24.dp )
                )

                SliderControl(
                    state = strength,
                    onSlide = { strength = it },
                    onSlideComplete = {},
                    toDisplay = { "%.02f".format(it) },
                    range = 0f..100f
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { backdrop = 0f },
                    icon = R.drawable.drop_half_fill,
                    color = colorPalette().favoritesIcon,
                    modifier = Modifier.size( 24.dp )
                )

                SliderControl(
                    state = backdrop,
                    onSlide = { backdrop = it },
                    onSlideComplete = {},
                    toDisplay = { "%.0f".format(it) },
                    range = 0f..100f
                )
            }
        }
    }
}