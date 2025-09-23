package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.colorPalette
import it.fast4x.rimusic.ui.styling.typography

@Composable
fun GooglePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colorPalette = colorPalette()
    val typography = typography()
    
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorPalette.accent,
            contentColor = colorPalette.onAccent,
            disabledContainerColor = colorPalette.textDisabled,
            disabledContentColor = colorPalette.textSecondary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = typography.s.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun GoogleSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colorPalette = colorPalette()
    val typography = typography()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (enabled) colorPalette.background2 else colorPalette.textDisabled
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = typography.s.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = if (enabled) colorPalette.text else colorPalette.textSecondary
            )
        )
    }
}

@Composable
fun GoogleTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colorPalette = colorPalette()
    val typography = typography()
    
    Box(
        modifier = modifier
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = typography.s.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = if (enabled) colorPalette.accent else colorPalette.textDisabled
            )
        )
    }
}

@Composable
fun GoogleFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorPalette = colorPalette()
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorPalette.accent)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
