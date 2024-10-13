package it.fast4x.rimusic.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.chevron_down
import rimusic.composeapp.generated.resources.chevron_up

@Composable
fun ExpandIcon(
    onAction: () -> Unit,
    actionClose: Boolean = false,
    modifier: Modifier = Modifier
) =
    Box(
        modifier = modifier
            //.fillMaxWidth()
            //.height(40.dp)
            .size(40.dp)
    ) {
        Image(
            painter = painterResource( if (actionClose) Res.drawable.chevron_down else Res.drawable.chevron_up),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(30.dp)
                .clickable{
                    onAction()
                }
        )
    }