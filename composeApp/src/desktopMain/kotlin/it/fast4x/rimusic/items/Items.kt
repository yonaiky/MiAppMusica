package it.fast4x.rimusic.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.fast4x.innertube.Innertube
import it.fast4x.rimusic.enums.ThumbnailRoundness
import org.jetbrains.compose.resources.painterResource
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.app_icon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    thumbnailContent: @Composable BoxScope.() -> Unit?,
    title: String?,
    authors: String?,
    duration: String?,
    thumbnailSizeDp: Dp,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    isDownloaded: Boolean,
    onDownloadClick: () -> Unit
) {

    ItemContainer(
        alternative = false,
        thumbnailSizeDp = thumbnailSizeDp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(thumbnailSizeDp) //.border(1.dp, Color.Red)
        ) {
            if (thumbnailContent.invoke(this) == null)
            thumbnailContent() else Image(
                painter = painterResource(Res.drawable.app_icon),
                colorFilter = ColorFilter.tint(Color.Green.copy(alpha = 0.6f)),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        ItemInfoContainer {
            trailingContent?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(
                        text = title ?: "",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = typography.titleMedium.fontSize,
                            fontWeight = typography.titleMedium.fontWeight
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .basicMarquee(iterations = Int.MAX_VALUE)
                    )

                    it()
                }
            } ?: BasicText(
                text = title ?: "",
                style = TextStyle(
                    color = Color.White,
                    fontSize = typography.titleSmall.fontSize,
                    fontWeight = typography.titleSmall.fontWeight
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .basicMarquee(iterations = Int.MAX_VALUE)
            )


            Row(verticalAlignment = Alignment.CenterVertically) {

                /*
                IconButton(
                    onClick = onDownloadClick,
                    icon = if (isDownloaded) R.drawable.downloaded else R.drawable.download,
                    color = if (isDownloaded) colorPalette.text else colorPalette.textDisabled,
                    modifier = Modifier
                        .size(16.dp)
                )
                 */

                Spacer(modifier = Modifier.padding(horizontal = 2.dp))

                BasicText(
                    text = authors ?: "",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = typography.titleSmall.fontSize,
                        fontWeight = typography.titleSmall.fontWeight
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
                        .weight(1f)
                        .basicMarquee(iterations = Int.MAX_VALUE)
                )

                duration?.let {
                    BasicText(
                        text = duration,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = typography.titleSmall.fontSize,
                            fontWeight = typography.titleSmall.fontWeight
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: Innertube.SongItem,
    isDownloaded: Boolean = false,
    onDownloadClick: () -> Unit = {},
    thumbnailSizeDp: Dp = 50.dp,
    modifier: Modifier
) {
    SongItem(
        title = song.info?.name,
        authors = song.authors?.joinToString(", ") { it.name.toString() },
        duration = song.durationText,
        thumbnailSizeDp = thumbnailSizeDp,
        modifier = modifier,
        isDownloaded = isDownloaded,
        thumbnailContent = {
            /*
            AsyncImage(
                model = song.thumbnail?.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(ThumbnailRoundness.Medium.shape())
                    .fillMaxSize()
            )
             */

            //onThumbnailContent?.invoke(this)
        },
        trailingContent = {},
        onDownloadClick = onDownloadClick
    )
}