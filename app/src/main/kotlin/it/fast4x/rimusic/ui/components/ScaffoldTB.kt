package it.fast4x.rimusic.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun ScaffoldTB(
    topIconButtonId: Int,
    onTopIconButtonClick: () -> Unit,
    topIconButton2Id: Int,
    onTopIconButton2Click: () -> Unit,
    showButton2: Boolean,
    bottomIconButtonId: Int? = R.drawable.search,
    onBottomIconButtonClick: (() -> Unit)? = {},
    showBottomButton: Boolean? = false,
    hideTabs: Boolean? = false,
    tabIndex: Int,
    onTabChanged: (Int) -> Unit,
    tabColumnContent: @Composable() (ColumnScope.(@Composable (Int, String, Int) -> Unit) -> Unit),
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val (colorPalette) = LocalAppearance.current
    val navigationBarPosition by rememberPreference(navigationBarPositionKey, NavigationBarPosition.Left)

    val navigationRailTB: @Composable () -> Unit = {
        NavigationRailTB(
            topIconButtonId = topIconButtonId,
            onTopIconButtonClick = onTopIconButtonClick,
            topIconButton2Id = topIconButton2Id,
            onTopIconButton2Click = onTopIconButton2Click,
            showButton2 = showButton2,
            bottomIconButtonId = bottomIconButtonId,
            onBottomIconButtonClick = onBottomIconButtonClick ?: {},
            showBottomButton = showBottomButton,
            tabIndex = tabIndex,
            onTabIndexChanged = onTabChanged,
            content = tabColumnContent,
            hideTabs = hideTabs
        )
    }

    val topPadding =  if (navigationBarPosition == NavigationBarPosition.Top) 60.dp else 0.dp

    androidx.compose.material3.Scaffold(
       containerColor = colorPalette.background0,
        topBar = {
            if (navigationBarPosition == NavigationBarPosition.Top)
                navigationRailTB()
        },

        bottomBar = {
            if (navigationBarPosition == NavigationBarPosition.Bottom)
                Row(
                    modifier = Modifier.background(colorPalette.background0)
                ){
                    navigationRailTB()
                }

        }

    ) {
        it.calculateTopPadding()
        AnimatedContent(
            targetState = tabIndex,
            transitionSpec = {
                val slideDirection = when (targetState > initialState) {
                    true -> when (navigationBarPosition) {
                        NavigationBarPosition.Left, NavigationBarPosition.Right -> AnimatedContentTransitionScope.SlideDirection.Up
                        NavigationBarPosition.Top, NavigationBarPosition.Bottom -> AnimatedContentTransitionScope.SlideDirection.Left
                    }
                    false -> when (navigationBarPosition) {
                        NavigationBarPosition.Left, NavigationBarPosition.Right -> AnimatedContentTransitionScope.SlideDirection.Down
                        NavigationBarPosition.Top, NavigationBarPosition.Bottom -> AnimatedContentTransitionScope.SlideDirection.Right
                    }
                }

                val animationSpec = spring(
                    dampingRatio = 0.9f,
                    stiffness = Spring.StiffnessLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )

                slideIntoContainer(slideDirection, animationSpec) togetherWith
                        slideOutOfContainer(slideDirection, animationSpec)
            },
            content = content, label = "",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = topPadding) //only with top navigation
        )
        //it.calculateBottomPadding()
    }
}
