package it.fast4x.rimusic.ui.screens.history

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.models.DateAgo
import it.fast4x.rimusic.models.EventWithSong
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.components.themed.Title
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.TimeZone
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun HistoryList() {
    val (colorPalette, typography) = LocalAppearance.current
    val windowInsets = LocalPlayerAwareWindowInsets.current

    val thumbnailSizeDp = Dimensions.thumbnails.album
    val thumbnailSizePx = thumbnailSizeDp.px

    val endPaddingValues = windowInsets.only(WindowInsetsSides.End).asPaddingValues()

    val sectionTextModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 24.dp, bottom = 8.dp)
        .padding(endPaddingValues)

    val today = LocalDate.now()
    val thisMonday = today.with(DayOfWeek.MONDAY)
    val lastMonday = thisMonday.minusDays(7)

    val events = Database.events()
        .map { events ->
            events.groupBy {
                val date = //it.event.timestamp.toLocalDate()
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(it.event.timestamp),
                    TimeZone.getDefault().toZoneId()).toLocalDate()
                val daysAgo = ChronoUnit.DAYS.between(date, today).toInt()
                when {
                    daysAgo == 0 -> DateAgo.Today
                    daysAgo == 1 -> DateAgo.Yesterday
                    date >= thisMonday -> DateAgo.ThisWeek
                    date >= lastMonday -> DateAgo.LastWeek
                    else -> DateAgo.Other(date.withDayOfMonth(1))
                }
            }.toSortedMap(compareBy { dateAgo ->
                when (dateAgo) {
                    DateAgo.Today -> 0L
                    DateAgo.Yesterday -> 1L
                    DateAgo.ThisWeek -> 2L
                    DateAgo.LastWeek -> 3L
                    is DateAgo.Other -> ChronoUnit.DAYS.between(dateAgo.date, today)
                }
            })
        }
        .collectAsState(initial = emptyMap(), context = Dispatchers.IO)


    val navigationBarPosition by rememberPreference(navigationBarPositionKey, NavigationBarPosition.Left)

    Column (
        modifier = Modifier
            .background(colorPalette.background0)
            //.fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth(
                if (navigationBarPosition == NavigationBarPosition.Left ||
                    navigationBarPosition == NavigationBarPosition.Top ||
                    navigationBarPosition == NavigationBarPosition.Bottom
                ) 1f
                else Dimensions.contentWidthRightBar
            )
    ) {

        LazyColumn(
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Vertical + WindowInsetsSides.End).asPaddingValues(),
            modifier = Modifier
                .background(colorPalette.background0)
                .fillMaxSize()
        ) {

            item(key = "header", contentType = 0) {
                HeaderWithIcon(
                    title = stringResource(R.string.history),
                    iconId = R.drawable.history,
                    enabled = false,
                    showIcon = false,
                    modifier = Modifier,
                    onClick = {}
                )
            }
            events.value.forEach { (dateAgo, events) ->
                stickyHeader {
                    Title(
                        title = when (dateAgo) {
                            DateAgo.Today -> stringResource(R.string.today)
                            DateAgo.Yesterday -> "Yesterday"
                            DateAgo.ThisWeek -> "This week"
                            DateAgo.LastWeek -> "Last week"
                            is DateAgo.Other -> dateAgo.date.format(DateTimeFormatter.ofPattern("yyyy/MM"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }

                items(
                    items = events.map {
                                       it.apply {
                                           this.event.timestamp = this.timestampDay!!
                                       }
                    }.distinctBy { it.song.id},
                    key = { it.event.id }
                ) { event ->
                    Text(
                        text = "${event.timestampDay} ${event.event.timestamp} ${event.song.id}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    /*
                    SongListItem(
                        song = event.song,
                        isActive = event.song.id == mediaMetadata?.id,
                        isPlaying = isPlaying,
                        showInLibraryIcon = true,
                        trailingContent = {
                            /*
                            IconButton(
                                onClick = {
                                    menuState.show {
                                        SongMenu(
                                            originalSong = event.song,
                                            event = event.event,
                                            navController = navController,
                                            onDismiss = menuState::dismiss
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ellipsis_vertical),
                                    contentDescription = null
                                )
                            }
                            */
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable {
                                /*
                                if (event.song.id == mediaMetadata?.id) {
                                    playerConnection.player.togglePlayPause()
                                } else {
                                    playerConnection.playQueue(
                                        YouTubeQueue(
                                            endpoint = WatchEndpoint(videoId = event.song.id),
                                            preloadItem = event.song.toMediaMetadata()
                                        )
                                    )
                                }
                                 */
                            }
                            .animateItemPlacement()
                    )
                    */
                }
            }
        }

    }
}

fun getDateTimeAsFormattedString(dateAsLongInMs: Long): String? {
    try {
        return SimpleDateFormat("dd/MM/yyyy").format(Date(dateAsLongInMs))
    } catch (e: Exception) {
        return null // parsing exception
    }
}

fun getTimestampFromDate(date: String): Long {
    return try {
        SimpleDateFormat("dd-MM-yyyy").parse(date).time
    } catch (e: Exception) {
        return 0
    }
}