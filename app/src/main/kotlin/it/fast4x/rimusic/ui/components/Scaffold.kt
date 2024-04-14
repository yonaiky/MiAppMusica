package it.fast4x.rimusic.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.ui.components.NavigationRail
import it.fast4x.rimusic.ui.components.ScaffoldTB
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.UiTypeKey
import it.fast4x.rimusic.utils.bold
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun Scaffold(
    topIconButtonId: Int,
    onTopIconButtonClick: () -> Unit,
    showButton1: Boolean = true,
    topIconButton2Id: Int,
    onTopIconButton2Click: () -> Unit,
    showButton2: Boolean,
    bottomIconButtonId: Int? = R.drawable.search,
    onBottomIconButtonClick: (() -> Unit)? = {},
    showBottomButton: Boolean? = false,
    hideTabs: Boolean? = false,
    tabIndex: Int,
    onTabChanged: (Int) -> Unit,
    tabColumnContent: @Composable ColumnScope.(@Composable (Int, String, Int) -> Unit) -> Unit,
    onSettingsClick: (() -> Unit)? = {},
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current
    val navigationBarPosition by rememberPreference(navigationBarPositionKey, NavigationBarPosition.Left)
    val uiType  by rememberPreference(UiTypeKey, UiType.RiMusic)

    if (navigationBarPosition == NavigationBarPosition.Top || navigationBarPosition == NavigationBarPosition.Bottom) {
            ScaffoldTB(
                topIconButtonId = topIconButtonId,
                onTopIconButtonClick = onTopIconButtonClick,
                showButton1 = showButton1,
                topIconButton2Id = topIconButton2Id,
                onTopIconButton2Click = onTopIconButton2Click,
                showButton2 = showButton2,
                tabIndex = tabIndex,
                onTabChanged = onTabChanged,
                tabColumnContent = tabColumnContent,
                showBottomButton = showBottomButton,
                bottomIconButtonId = bottomIconButtonId,
                onBottomIconButtonClick = onBottomIconButtonClick ?: {},
                content = content,
                hideTabs = hideTabs
            )
    } else {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val customModifier = if(uiType == UiType.RiMusic)
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        else Modifier

        androidx.compose.material3.Scaffold(
            modifier = customModifier,
            containerColor = colorPalette.background0,
            topBar = {
                if(uiType == UiType.RiMusic) {
                    TopAppBar(
                        title = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.app_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp)
                                )
                                BasicText(
                                    text = "Music",
                                    style = TextStyle(
                                        fontSize = typography.xxxl.semiBold.fontSize,
                                        fontWeight = typography.xxxl.semiBold.fontWeight,
                                        color = colorPalette.text
                                    )
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.search),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.history),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.stats_chart),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            if (onSettingsClick != null) {
                                IconButton(onClick = onSettingsClick) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.settings),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarColors(
                            containerColor = colorPalette.background0,
                            titleContentColor = colorPalette.text,
                            scrolledContainerColor = colorPalette.background0,
                            navigationIconContentColor = colorPalette.background0,
                            actionIconContentColor = colorPalette.text
                        )
                    )
                }
            },

            bottomBar = {

            }

        ) {
            //it.calculateTopPadding()
            //**

            Row(
                //horizontalArrangement = Arrangement.spacedBy(0.dp),
                modifier = modifier
                    .border(BorderStroke(1.dp,Color.Red))
                    //.padding(top = 50.dp)
                    .padding(it)
                    .background(colorPalette.background0)
                    .fillMaxSize()
            ) {
                val navigationRail: @Composable () -> Unit = {
                    NavigationRail(
                        topIconButtonId = topIconButtonId,
                        onTopIconButtonClick = onTopIconButtonClick,
                        showButton1 = showButton1,
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

                if (navigationBarPosition == NavigationBarPosition.Left)
                    navigationRail()

                val topPadding = if (uiType == UiType.ViMusic) 30.dp else 0.dp

                AnimatedContent(
                    targetState = tabIndex,
                    transitionSpec = {
                        val slideDirection = when (targetState > initialState) {
                            true -> AnimatedContentTransitionScope.SlideDirection.Up
                            false -> AnimatedContentTransitionScope.SlideDirection.Down
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
                        //.fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = topPadding)
                )

                if (navigationBarPosition == NavigationBarPosition.Right)
                    navigationRail()

            }
            //**
        }
    }

}
