package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.ui.components.MusicBars
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.onOverlay

@Composable
fun NowPlayingShow (
    mediaId: String
) {
    val binder = LocalPlayerServiceBinder.current
    val player = binder?.player

    val (colorPalette, typography, thumbnailShape) = LocalAppearance.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = Color.Black.copy(alpha = 0.25f),
                shape = thumbnailShape
            )
            .size(Dimensions.thumbnails.song)
    ) {

        if (player?.currentMediaItem?.mediaId == mediaId) {
            MusicBars(
                color = colorPalette.onOverlay,
                modifier = Modifier
                    .height(40.dp)
            )
            /*
            Image(
                painter = painterResource(R.drawable.musical_notes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorPalette.onOverlay),
                modifier = Modifier
                    .size(40.dp)
            )
             */
        }
    }

}