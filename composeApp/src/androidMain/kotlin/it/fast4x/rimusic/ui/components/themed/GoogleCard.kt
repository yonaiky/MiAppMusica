package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.colorPalette

@Composable
fun GoogleCard(
    modifier: Modifier = Modifier,
    elevation: Int = 2,
    cornerRadius: Int = 12,
    content: @Composable () -> Unit
) {
    val appearance = LocalAppearance.current
    val colorPalette = colorPalette()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorPalette.background1
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun GoogleElevatedCard(
    modifier: Modifier = Modifier,
    elevation: Int = 8,
    cornerRadius: Int = 16,
    content: @Composable () -> Unit
) {
    val appearance = LocalAppearance.current
    val colorPalette = colorPalette()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorPalette.background1
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun GoogleSurfaceCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 8,
    content: @Composable () -> Unit
) {
    val appearance = LocalAppearance.current
    val colorPalette = colorPalette()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(colorPalette.background2)
            .padding(12.dp)
    ) {
        content()
    }
}
