package me.knighthat.component.tab.toolbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.Menu
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.utils.semiBold
import me.knighthat.component.header.TabToolBar
import me.knighthat.enums.MenuTitle
import me.knighthat.typography
import kotlin.enums.EnumEntries

interface Sort<T: Enum<T>>: Button {

    companion object {

        @Composable
        fun <T: Enum<T>> Menu(
            onDismiss: () -> Unit,
            entries: EnumEntries<T>,
            actions: (T) -> Unit
        ) {
            Menu( entries ) {
                if( it is MenuTitle )
                    MenuEntry(
                        icon = R.drawable.text,
                        text = stringResource( it.titleId ),
                        onClick = {
                            onDismiss()
                            actions( it )
                        }
                    )
            }
        }

        @Composable
        fun <T: Enum<T>> Menu(
            entries: EnumEntries<T>,
            entry: @Composable ( T ) -> Unit
        ) {
            Menu {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding( end = 12.dp )
                ) {
                    BasicText(
                        text = stringResource( R.string.sorting_order ),
                        style = typography().m.semiBold,
                        modifier = Modifier.padding(
                            vertical = 8.dp,
                            horizontal = 24.dp
                        )
                    )
                }

                Spacer( Modifier.height( 8.dp ) )

                entries.forEach{ entry( it ) }
            }
        }
    }

    val menuState: MenuState
    val sortOrderState: MutableState<SortOrder>
    val sortByEnum: EnumEntries<T>
    val sortByState: MutableState<T>

    @Composable
    override fun ToolBarButton() {
        var sortOrder by sortOrderState

        TabToolBar.Icon(
            iconId = R.drawable.arrow_up,
            modifier = Modifier.graphicsLayer { rotationZ = sortOrder.rotationZ },
            onShortClick = { sortOrder = !sortOrder },
            onLongClick = {
                menuState.display {
                    Menu(
                        onDismiss = menuState::hide,
                        entries = sortByEnum
                    ) { sortByState.value = it }
                }
            }
        )
    }
}