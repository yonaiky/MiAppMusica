package me.knighthat.component.tab.toolbar

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold
import me.knighthat.component.header.TabToolBar
import me.knighthat.enums.Drawable
import me.knighthat.enums.MenuTitle
import me.knighthat.typography
import org.intellij.lang.annotations.MagicConstant
import kotlin.enums.EnumEntries

open class SortComponent<T: Enum<T>> protected constructor(
    val menuState: MenuState,
    protected val sortOrderState: MutableState<SortOrder>,
    protected val sortByEntries: EnumEntries<T>,
    protected val sortByState: MutableState<T>
): Icon, Clickable {

    companion object {
        @JvmStatic
        @Composable
        fun <T: Enum<T>> init(
            @MagicConstant sortOrderKey: String,
            sortByEnums: EnumEntries<T>,
            sortByState: MutableState<T>
        ): SortComponent<T> =
            SortComponent(
                LocalMenuState.current,
                rememberPreference(sortOrderKey, SortOrder.Descending),
                sortByEnums,
                sortByState
            )

        @Composable
        fun <T: Enum<T>> Menu(
            onDismiss: () -> Unit,
            entries: EnumEntries<T>,
            actions: (T) -> Unit
        ) {
            Menu( entries ) {
                val icon =
                    if( it is Drawable)
                        it.icon
                    else
                        painterResource( R.drawable.text )

                if( it is MenuTitle)
                    MenuEntry(
                        painter = icon,
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
            it.fast4x.rimusic.ui.components.themed.Menu {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    BasicText(
                        text = stringResource(R.string.sorting_order),
                        style = typography().m.semiBold,
                        modifier = Modifier.padding(
                            vertical = 8.dp,
                            horizontal = 24.dp
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                entries.forEach { entry(it) }
            }
        }
    }

    private val arrowDirection: State<Float>
        @Composable
        get() = animateFloatAsState(
            targetValue = sortOrder.rotationZ,
            animationSpec = tween(durationMillis = 400, easing = LinearEasing),
            label = ""
        )
    var sortOrder: SortOrder = sortOrderState.value
        set(value) {
            sortOrderState.value = value
            field = value
        }
    var sortBy: T = sortByState.value
        set(value) {
            sortByState.value = value
            field = value
        }
    override val iconId: Int
        @DrawableRes
        get() = R.drawable.arrow_up

    override fun onShortClick() { sortOrder = !sortOrder }

    override fun onLongClick() {
        menuState.display {
            Menu(
                onDismiss = menuState::hide,
                entries = sortByEntries
            ) { sortByState.value = it }
        }
    }

    @Composable
    override fun ToolBarButton() {
        val animatedArrow by arrowDirection

        TabToolBar.Icon(
            this.iconId,
            color,
            sizeDp,
            Modifier.graphicsLayer { rotationZ = animatedArrow },
            isEnabled,
            ::onShortClick,
            ::onLongClick
        )
    }
}