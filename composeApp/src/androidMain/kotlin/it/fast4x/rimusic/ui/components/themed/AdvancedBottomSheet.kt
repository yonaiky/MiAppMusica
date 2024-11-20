package it.fast4x.rimusic.ui.components.themed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

///////////////////////////////
// THIS IS WORK IN PROGRESS
///////////////////////////////
private const val SLIDE_DURATION_MS = 800;
private const val FADE_DURATION_MS = 500;

@Composable
fun BottomSheetDemo() {
    var showBottomSheet by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Button(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 120.dp)
        ) {
            Text("Open Bottom Sheet")
        }
        if (showBottomSheet) {
            // Show a grey overlay on the screen when bottom sheet is opened
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(onClick = {
                        showBottomSheet = false
                    }) // Close sheet when user taps anywhere outside
            )
            //BottomSheetContent()
            BottomSheetSlideIn(showBottomSheet)
            //BottomSheetSlideWithBounce(showBottomSheet)
            //BottomSheetFadeIn(showBottomSheet)
            //BottomSheetFadeAndSlide(showBottomSheet)
        }
    }
}

@Composable
fun BottomSheetSlideIn(isVisible: Boolean) {
    // Wrap the BottomSheetContent() inside AnimatedVisibility()
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(SLIDE_DURATION_MS)
        ),
        exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(SLIDE_DURATION_MS))
    ) {
        BottomSheetContent()
    }
}

@Composable
fun BottomSheetSlideWithBounce(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it }, animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(SLIDE_DURATION_MS)
        )
    ) {
        BottomSheetContent()
    }
}

@Composable
fun BottomSheetFadeIn(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(FADE_DURATION_MS)),
        exit = fadeOut(animationSpec = tween(FADE_DURATION_MS))
    ) {
        BottomSheetContent()
    }
}

@Composable
fun BottomSheetFadeAndSlide(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(SLIDE_DURATION_MS)
        ) + fadeIn(animationSpec = tween(FADE_DURATION_MS)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(SLIDE_DURATION_MS)
        ) + fadeOut(animationSpec = tween(FADE_DURATION_MS))
    ) {
        BottomSheetContent()
    }
}

@Composable
fun BottomSheetContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .height(300.dp)
            .padding(16.dp)
            .pointerInput(Unit) {}
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Horizontal tab on top of the Bottom Sheet
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(6.dp)
                    .width(60.dp)
                    .background(Color.Gray, RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Your sheet content goes here
            Text(
                "Bottom Sheet Content",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 80.dp)
            )
        }
    }
}