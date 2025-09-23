package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.colorPalette

@Composable
fun GoogleIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color? = null
) {
    val colorPalette = colorPalette()
    
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint ?: colorPalette.text,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun GoogleFilledIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    backgroundColor: Color? = null,
    tint: Color? = null
) {
    val colorPalette = colorPalette()
    
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor ?: colorPalette.background2)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint ?: colorPalette.text,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun GoogleRoundedIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    backgroundColor: Color? = null,
    tint: Color? = null,
    cornerRadius: Int = 8
) {
    val colorPalette = colorPalette()
    
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(backgroundColor ?: colorPalette.background2)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint ?: colorPalette.text,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun GoogleAccentIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val colorPalette = colorPalette()
    
    GoogleFilledIconButton(
        imageVector = imageVector,
        onClick = onClick,
        modifier = modifier,
        contentDescription = contentDescription,
        backgroundColor = colorPalette.accent,
        tint = colorPalette.onAccent
    )
}
