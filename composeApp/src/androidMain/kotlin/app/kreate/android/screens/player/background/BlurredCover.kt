package app.kreate.android.screens.player.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.utils.BlurTransformation
import it.fast4x.rimusic.utils.isAtLeastAndroid12
import me.knighthat.coil.ImageCacheFactory
import me.knighthat.component.player.BlurAdjuster

@Composable
private fun BlurFilter(
    thumbnailUrl: String,
    showThumbnail: Boolean,
    isShowingLyrics: Boolean,
    isShowingVisualizer: Boolean,
    noBlur: Boolean,
    blurAdjuster: BlurAdjuster,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    val blurRadius by remember {
        derivedStateOf( structuralEqualityPolicy() ) {
            if( showThumbnail || (isShowingLyrics && !isShowingVisualizer) || !noBlur )
                blurAdjuster.strength
            else
                0f
        }
    }
    val blurTransformation by remember {
        derivedStateOf( referentialEqualityPolicy() ) {
            if( !isAtLeastAndroid12 ) {
                val radius: Float =
                    if( showThumbnail || (isShowingLyrics && !isShowingVisualizer) || !noBlur )
                        blurAdjuster.strength
                    else
                        0f
                listOf( BlurTransformation(radius.toInt(), .5f) )
            } else
                emptyList()
        }
    }

    Image(
        painter = ImageCacheFactory.Painter(
            thumbnailUrl = thumbnailUrl,
            transformations = blurTransformation
        ),
        contentDescription = "blurred_background",
        contentScale = contentScale,
        // [Modifier.blur] will be ignore on unsupported devices by default
        modifier = modifier.blur( blurRadius.dp )
    )
}

@Composable
private fun Backdrop( blurAdjuster: BlurAdjuster, modifier: Modifier = Modifier ) {
    // Backdrop is supported regardless of Android version
    val backdropColor by remember {
        derivedStateOf {
            Color.Black.copy(
                // Ensure value can't go outside by accident
                alpha = (blurAdjuster.backdrop / 100f).coerceIn( 0f, 1f )
            )
        }
    }
    Box( modifier.background( backdropColor ) )
}

@Composable
fun BlurredCover(
    thumbnailUrl: String,
    blurAdjuster: BlurAdjuster,
    showThumbnail: Boolean,
    noBlur: Boolean,
    isShowingLyrics: Boolean,
    isShowingVisualizer: Boolean,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    BlurFilter( thumbnailUrl, showThumbnail, isShowingLyrics, isShowingVisualizer, noBlur, blurAdjuster, contentScale, modifier )
    Backdrop( blurAdjuster, modifier )
}