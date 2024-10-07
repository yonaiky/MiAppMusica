package it.fast4x.rimusic.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import it.fast4x.innertube.Innertube
import it.fast4x.rimusic.cleanPrefix
import it.fast4x.rimusic.enums.ThumbnailRoundness
import it.fast4x.rimusic.thumbnail
import it.fast4x.rimusic.utils.LoadImage

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
            thumbnailContent()
        }

        ItemInfoContainer {
            trailingContent?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(
                        text = title ?: "",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = typography.titleSmall.fontSize,
                            //fontWeight = typography.titleMedium.fontWeight
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
                    //fontWeight = typography.titleSmall.fontWeight
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
                        //fontWeight = typography.titleSmall.fontWeight
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
                            //fontWeight = typography.titleSmall.fontWeight
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

            song.thumbnail?.url.let {
                if (it != null) {
                    LoadImage(it)
                }
            }
            //onThumbnailContent?.invoke(this)
        },
        trailingContent = {},
        onDownloadClick = onDownloadClick
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumItem(
    thumbnailUrl: String?,
    title: String?,
    authors: String?,
    year: String?,
    yearCentered: Boolean? = true,
    thumbnailSizePx: Int?,
    thumbnailSizeDp: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false,
    showAuthors: Boolean? = false
) {

    ItemContainer(
        alternative = alternative,
        thumbnailSizeDp = thumbnailSizeDp,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        AsyncImage(
            model = thumbnailUrl.let { cleanPrefix(it ?: "") }, //thumbnailUrl?.thumbnail(thumbnailSizePx)?.let { it1 -> cleanPrefix(it1) },) } //thumbnailUrl?.thumbnail(thumbnailSizePx)?.let { it1 -> cleanPrefix(it1) },
            contentDescription = null,
            //contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(ThumbnailRoundness.Medium.shape())
                .requiredSize(thumbnailSizeDp)
        )

        ItemInfoContainer {
            BasicText(
                text = cleanPrefix(title ?: ""),
                style = TextStyle(
                    color = Color.White,
                    fontSize = typography.titleSmall.fontSize,
                    //fontWeight = typography.titleSmall.fontWeight
                ),
                maxLines = 1, //if (alternative) 1 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .basicMarquee(iterations = Int.MAX_VALUE)
            )

            if (!alternative || showAuthors == true) {
                authors?.let {
                    BasicText(
                        text = cleanPrefix(authors),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = typography.titleSmall.fontSize,
                            //fontWeight = typography.titleSmall.fontWeight
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .basicMarquee(iterations = Int.MAX_VALUE)
                            .align(
                                if (yearCentered == true) Alignment.CenterHorizontally else Alignment.Start)
                    )
                }
            }

            BasicText(
                text = year ?: "",
                style = TextStyle(
                    color = Color.White,
                    fontSize = typography.titleSmall.fontSize,
                    //fontWeight = typography.titleSmall.fontWeight
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(
                        if (yearCentered == true) Alignment.CenterHorizontally else Alignment.Start)
            )
        }
    }
}

@Composable
fun AlbumItem(
    album: Innertube.AlbumItem,
    yearCentered: Boolean? = true,
    thumbnailSizeDp: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false,
    showAuthors: Boolean? = false
) {
    AlbumItem(
        thumbnailUrl = album.thumbnail?.url,
        title = album.info?.name,
        authors = album.authors?.joinToString(", ") { it.name.toString() },
        year = album.year,
        yearCentered = yearCentered,
        thumbnailSizePx = null,
        thumbnailSizeDp = thumbnailSizeDp,
        modifier = modifier,
        alternative = alternative,
        showAuthors = showAuthors
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistItem(
    thumbnailUrl: String?,
    name: String?,
    subscribersCount: String?,
    //thumbnailSizePx: Int,
    thumbnailSizeDp: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false,
    showName: Boolean = true
) {
    ItemContainer(
        alternative = alternative,
        thumbnailSizeDp = thumbnailSizeDp,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .clip(ThumbnailRoundness.Medium.shape())
                .requiredSize(thumbnailSizeDp)
        )

        if (showName)
            ItemInfoContainer(
                horizontalAlignment = if (alternative) Alignment.CenterHorizontally else Alignment.Start,
            ) {
                BasicText(
                    text = name ?: "",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = typography.titleSmall.fontSize,
                        //fontWeight = typography.titleSmall.fontWeight
                    ),
                    maxLines = 1, //if (alternative) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .basicMarquee(iterations = Int.MAX_VALUE)
                )

                subscribersCount?.let {
                    BasicText(
                        text = subscribersCount,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = typography.titleSmall.fontSize,
                            //fontWeight = typography.titleSmall.fontWeight
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .basicMarquee(iterations = Int.MAX_VALUE)
                    )
                }
            }
    }
}

@Composable
fun ArtistItem(
    artist: Innertube.ArtistItem,
    //thumbnailSizePx: Int,
    thumbnailSizeDp: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false,
) {
    ArtistItem(
        thumbnailUrl = artist.thumbnail?.url,
        name = artist.info?.name,
        subscribersCount = artist.subscribersCountText,
        thumbnailSizeDp = thumbnailSizeDp,
        modifier = modifier,
        alternative = alternative
    )
}