package it.vfsfitvnm.vimusic.ui.components.themed

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import it.vfsfitvnm.innertube.models.NavigationEndpoint
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.service.PlayerService
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.ui.styling.favoritesIcon
import it.vfsfitvnm.vimusic.utils.medium
import it.vfsfitvnm.vimusic.utils.seamlessPlay

@OptIn(UnstableApi::class)
@Composable
fun MediaItemGridMenu (
    binder: PlayerService.Binder,
    mediaItem: MediaItem,
    onDismiss: () -> Unit,
) {
    val (colorPalette, typography) = LocalAppearance.current
    GridMenu(
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 8.dp,
            end = 8.dp,
            bottom = 8.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
        )
    ) {
        GridMenuItem(
            icon = R.drawable.radio,
            title = R.string.start_radio,
            colorIcon = colorPalette.text,
            colorText = colorPalette.text,
            enabled = false
        ) {
            println("mediaItem click")
        }
        GridMenuItem(
            icon = R.drawable.radio,
            title = R.string.start_radio,
            colorIcon = colorPalette.text,
            colorText = colorPalette.text,
            enabled = true
        ) {
            println("mediaItem click")
        }

    }


}