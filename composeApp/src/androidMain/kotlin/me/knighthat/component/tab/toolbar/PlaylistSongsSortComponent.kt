package me.knighthat.component.tab.toolbar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PlaylistSongSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.utils.playlistSongSortByKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.utils.songSortOrderKey
import me.knighthat.component.header.TabToolBar
import me.knighthat.typography
import kotlin.enums.EnumEntries

class PlaylistSongsSortComponent private constructor(
    menuState: MenuState,
    sortOrderState: MutableState<SortOrder>,
    sortByEntries: EnumEntries<PlaylistSongSortBy>,
    sortByState: MutableState<PlaylistSongSortBy>
) : SortComponent<PlaylistSongSortBy>(menuState, sortOrderState, sortByEntries, sortByState) {

    companion object {
        @JvmStatic
        @Composable
        fun init(): PlaylistSongsSortComponent =
            PlaylistSongsSortComponent(
                LocalMenuState.current,
                rememberPreference(songSortOrderKey, SortOrder.Descending),
                PlaylistSongSortBy.entries,
                rememberPreference(playlistSongSortByKey, PlaylistSongSortBy.Title)
            )
    }

    @Composable
    fun title( currentValue: PlaylistSongSortBy ): String =
        when (currentValue) {
            PlaylistSongSortBy.ArtistAndAlbum ->
                "${stringResource(R.string.sort_artist)}, ${stringResource(R.string.sort_album)}"

            else -> stringResource(currentValue.titleId)
        }

    @Composable
    fun SortTitle() {
        val sortBy by sortByState

        BasicText(
            text = title( sortBy ),
            style = typography().xs.semiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable {
                menuState.display {

                    Menu( sortByEntries ) {
                        val icon = it.icon

                        MenuEntry(
                            painter = icon,
                            text = title( it ),
                            onClick = {
                                // Don't pass menuState::hide, it won't work
                                menuState.hide()
                                sortByState.value = it
                            }
                        )
                    }
                }
            }
        )
    }

    @Composable
    override fun ToolBarButton() {
        val animatedArrow by animateFloatAsState(
            targetValue = sortOrder.rotationZ,
            animationSpec = tween(durationMillis = 400, easing = LinearEasing),
            label = ""
        )

        TabToolBar.Icon(
            iconId = R.drawable.arrow_up,
            modifier = Modifier.graphicsLayer { rotationZ = animatedArrow },
            onShortClick = { sortOrder = !sortOrder },
            onLongClick = {}
        )
    }
}