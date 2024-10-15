package me.knighthat.component.tab.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.utils.semiBold
import me.knighthat.component.header.TabToolBar
import me.knighthat.typography

interface DetailedSort<T: Enum<T>>: Sort<T> {

    @Composable
    fun title( currentValue: T ): String

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

                    Sort.Menu( sortByEnum ) {
                        MenuEntry(
                            icon = R.drawable.text,
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
        var sortOrder by sortOrderState

        TabToolBar.Icon(
            iconId = R.drawable.arrow_up,
            modifier = Modifier.graphicsLayer { rotationZ = sortOrder.rotationZ },
            onShortClick = { sortOrder = !sortOrder },
            onLongClick = {}
        )
    }
}