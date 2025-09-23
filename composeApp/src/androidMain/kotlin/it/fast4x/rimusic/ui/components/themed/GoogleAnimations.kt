package it.fast4x.rimusic.ui.components.themed

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.colorPalette

// Google Material Design 3 Animation Durations
object GoogleAnimationDurations {
    const val Short1 = 50
    const val Short2 = 100
    const val Short3 = 150
    const val Short4 = 200
    const val Medium1 = 250
    const val Medium2 = 300
    const val Medium3 = 350
    const val Medium4 = 400
    const val Long1 = 450
    const val Long2 = 500
    const val Long3 = 550
    const val Long4 = 600
    const val ExtraLong1 = 700
    const val ExtraLong2 = 800
    const val ExtraLong3 = 900
    const val ExtraLong4 = 1000
}

// Google Material Design 3 Easing Functions
object GoogleEasing {
    val Standard = FastOutSlowInEasing
    val Decelerate = FastOutSlowInEasing
    val Accelerate = LinearEasing
    val Sharp = LinearEasing
}

// Google Material Design 3 Animation Specs
object GoogleAnimationSpecs {
    val Short = tween<Float>(
        durationMillis = GoogleAnimationDurations.Short4,
        easing = GoogleEasing.Standard
    )
    
    val Medium = tween<Float>(
        durationMillis = GoogleAnimationDurations.Medium4,
        easing = GoogleEasing.Standard
    )
    
    val Long = tween<Float>(
        durationMillis = GoogleAnimationDurations.Long4,
        easing = GoogleEasing.Standard
    )
    
    val ExtraLong = tween<Float>(
        durationMillis = GoogleAnimationDurations.ExtraLong4,
        easing = GoogleEasing.Standard
    )
}

@Composable
fun GoogleShimmerEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorPalette = colorPalette()
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = GoogleEasing.Standard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorPalette.background2)
            .alpha(alpha)
    ) {
        content()
    }
}

@Composable
fun GoogleRippleEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorPalette = colorPalette()
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorPalette.background2)
    ) {
        content()
    }
}

@Composable
fun GoogleFadeIn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fade_in")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = GoogleAnimationSpecs.Medium,
        label = "fade_alpha"
    )
    
    Box(
        modifier = modifier.alpha(alpha)
    ) {
        content()
    }
}

@Composable
fun GoogleSlideIn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "slide_in")
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 50f,
        targetValue = 0f,
        animationSpec = GoogleAnimationSpecs.Medium,
        label = "slide_offset"
    )
    
    Box(
        modifier = modifier
    ) {
        content()
    }
}
