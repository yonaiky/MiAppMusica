package it.fast4x.rimusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.enums.PageType
import it.fast4x.rimusic.items.SongItem
import player.frame.FramePlayer
import vlcj.VlcjFrameController

@Composable
fun PlayerEssential(
    frameController: VlcjFrameController,
    url: String?,
    onExpandAction: () -> Unit
){

    Column (verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        HorizontalDivider(
            color = Color.DarkGray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth().alpha(0.6f)
        )
        Row(
            Modifier.background(Color.Black).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExpandIcon(
                onAction = onExpandAction
            )

            Row(
                //Modifier.border(BorderStroke(1.dp, Color.Red)),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                SongItem(
                    thumbnailContent = {},
                    authors = "Author",
                    duration = "00:00",
                    title = "Title",
                    isDownloaded = false,
                    onDownloadClick = {},
                    thumbnailSizeDp = 80.dp,
                    modifier = Modifier.fillMaxWidth(0.2f)
                )
            }
            FramePlayer(
                Modifier.fillMaxWidth(0.8f), //.border(BorderStroke(1.dp, Color.Yellow)),
                url ?: "",
                frameController.size.collectAsState(null).value?.run {
                    IntSize(first, second)
                } ?: IntSize.Zero,
                frameController.bytes.collectAsState(null).value,
                frameController,
                true,
                false
            )
        }
    }

}