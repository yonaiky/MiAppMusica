package it.fast4x.rimusic.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import it.fast4x.compose.reordering.animateItemPlacement
import it.fast4x.rimusic.LocalPlayerSheetState
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.ui.styling.overlay
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.thumbnailRoundnessKey
import kotlinx.coroutines.launch

@Composable
fun SnackbarDemo() {
    Column {
        val (snackbarVisibleState, setSnackBarState) = remember { mutableStateOf(false) }

        Button(onClick = { setSnackBarState(!snackbarVisibleState) }) {
            if (snackbarVisibleState) {
                Text("Hide Snackbar")
            } else {
                Text("Show Snackbar")
            }
        }
        if (snackbarVisibleState) {
            Snackbar(

                action = {
                    Button(onClick = {}) {
                        Text("MyAction")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) { Text(text = "This is a snackbar!") }
        }

    }
}


@Composable
fun Popup(
    message: String,
    type: PopupType
) {
    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val (colorPalette, typography, roundness) = LocalAppearance.current
    var thumbnailRoundness by rememberPreference(
        thumbnailRoundnessKey,
        ThumbnailRoundness.Heavy
    )

    val density = LocalDensity.current
    val windowsInsets = WindowInsets.systemBars
    val bottomDp = with(density) { windowsInsets.getBottom(density).toDp() }
    val navigationBarPosition by rememberPreference(navigationBarPositionKey, NavigationBarPosition.Left)
    val additionalBottomPadding = if (navigationBarPosition == NavigationBarPosition.Bottom)
        Dimensions.additionalVerticalSpaceForFloatingAction else 0.dp
    val playerSheetState = LocalPlayerSheetState.current
    val bottomPadding = if (playerSheetState.isCollapsed) bottomDp + Dimensions.collapsedPlayer + additionalBottomPadding else bottomDp + additionalBottomPadding

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.padding(10.dp)
    ) {

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                snackState.showSnackbar(message)
            }
        }

        SnackbarHost(
            modifier=Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding)
                .clip(thumbnailRoundness.shape())
                .align(Alignment.BottomStart)
                .animateContentSize(),
            hostState = snackState
        ) { snackbarData: SnackbarData ->
            CustomSnackBar(
                when(type) {
                    PopupType.Alert -> R.drawable.alert_circle
                    PopupType.Help -> R.drawable.help_circle
                    PopupType.Info -> R.drawable.information_circle
                },
                snackbarData.visuals.message,
                isRtl = true,
                containerColor = colorPalette.favoritesIcon
            )
        }
    }

}

@Composable
fun CustomSnackBar(
    @DrawableRes drawableRes: Int,
    message: String,
    isRtl: Boolean = true,
    containerColor: Color = Color.Black
) {
    val (colorPalette, typography) = LocalAppearance.current
    Snackbar(containerColor = containerColor) {
        CompositionLocalProvider(
            LocalLayoutDirection provides
                    if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    painterResource(id = drawableRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = message,
                    fontFamily = typography.s.fontFamily,
                    fontWeight = typography.s.fontWeight,
                    fontSize = typography.s.fontSize,
                    fontStyle = typography.s.fontStyle
                )
            }
        }
    }
}