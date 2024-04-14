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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun ScaffoldTB(
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
    showTopActions: Boolean? = false,
    onHomeClick: () -> Unit,
    onSettingsClick: (() -> Unit)? = {},
    onStatisticsClick: (() -> Unit)? = {},
    onHistoryClick: (() -> Unit)? = {},
    onSearchClick: (() -> Unit)? = {},
    tabIndex: Int,
    onTabChanged: (Int) -> Unit,
    tabColumnContent: @Composable() (ColumnScope.(@Composable (Int, String, Int) -> Unit) -> Unit),
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current
    val navigationBarPosition by rememberPreference(navigationBarPositionKey, NavigationBarPosition.Left)

    val navigationRailTB: @Composable () -> Unit = {
        NavigationRailTB(
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

    val topPadding =  if (navigationBarPosition == NavigationBarPosition.Top) 60.dp else 10.dp
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.material3.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
       containerColor = colorPalette.background0,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.app_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { onHomeClick() }
                            )
                            BasicText(
                                text = "Music",
                                style = TextStyle(
                                    fontSize = typography.xxxl.semiBold.fontSize,
                                    fontWeight = typography.xxxl.semiBold.fontWeight,
                                    color = colorPalette.text
                                ),
                                modifier = Modifier
                                    .clickable { onHomeClick() }
                            )
                        }
                    },
                    actions = {
                        if (showTopActions == true) {
                            IconButton(
                                onClick = {
                                    if (onSearchClick != null) {
                                        onSearchClick()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.search),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.burger),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {

                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.history)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.history),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    onClick = {
                                        expanded = false
                                        if (onHistoryClick != null) {
                                            onHistoryClick()
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.statistics)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.stats_chart),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    onClick = {
                                        expanded = false
                                        if (onStatisticsClick != null) {
                                            onStatisticsClick()
                                        }
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.settings),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    onClick = {
                                        expanded = false
                                        if (onSettingsClick != null) {
                                            onSettingsClick()
                                        }
                                    }
                                )
                            }

                            /*
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
                     */
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

                if (navigationBarPosition == NavigationBarPosition.Top)
                    navigationRailTB()
            }
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

        Row(
            modifier = modifier
                //.border(BorderStroke(1.dp, Color.Red))
                //.padding(top = 50.dp)
                .padding(it)
                .background(colorPalette.background0)
                .fillMaxSize()
        ) {
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
                content = content,
                label = "",
                modifier = Modifier
                    //.border(BorderStroke(1.dp, Color.Blue))
                    //.fillMaxWidth()
                    .fillMaxHeight()
                    //.padding(top = topPadding) //only with top navigation
            )
        }
    }
}
