package me.knighthat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationDefaults.windowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PlayerPosition
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.utils.playerPositionKey
import it.fast4x.rimusic.utils.rememberPreference
import me.knighthat.component.header.AppHeader
import me.knighthat.component.nav.AbstractNavigationBar
import me.knighthat.component.nav.HorizontalNavigationBar
import me.knighthat.component.nav.VerticalNavigationBar

// THIS IS THE SCAFFOLD
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Skeleton(
    navController: NavController,
    tabIndex: Int = 0,
    onTabChanged: (Int) -> Unit = {},
    mediaPlayer: @Composable (() -> Unit)? = null,
    navBarContent: @Composable (@Composable (Int, String, Int) -> Unit) -> Unit,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val navigationBar: AbstractNavigationBar =
        when( navBarPos() ) {
            NavigationBarPosition.Left, NavigationBarPosition.Right ->
                VerticalNavigationBar( tabIndex, onTabChanged, navController )
            NavigationBarPosition.Top, NavigationBarPosition.Bottom ->
                HorizontalNavigationBar( tabIndex, onTabChanged, navController )
        }
    navigationBar.add( navBarContent )

    val appHeader: @Composable () -> Unit = {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if( uiType() == UiType.RiMusic )
                AppHeader( navController ).Draw()

            if ( navBarPos() == NavigationBarPosition.Top )
                navigationBar.Draw()
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val modifier: Modifier =
        if( uiType() == UiType.ViMusic && navigationBar is HorizontalNavigationBar )
            Modifier
        else
            Modifier.nestedScroll( scrollBehavior.nestedScrollConnection )

    Scaffold(
        modifier = modifier,
        containerColor = colorPalette().background0,
        topBar = appHeader,
        bottomBar = {
            if ( navBarPos() == NavigationBarPosition.Bottom )
                navigationBar.Draw()
        }
    ) {
        val paddingSides = WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
        val innerPadding =
            if( navBarPos() == NavigationBarPosition.Top )
                windowInsets.only( paddingSides ).asPaddingValues()
            else
                PaddingValues( Dp.Hairline )

        Box(
            Modifier
                .padding(it)
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                Modifier
                    .background(colorPalette().background0)
                    .fillMaxSize()
            ) {
                if( navBarPos() == NavigationBarPosition.Left )
                    navigationBar.Draw()

                val topPadding = if ( uiType() == UiType.ViMusic ) 30.dp else 0.dp
                AnimatedContent(
                    targetState = tabIndex,
                    transitionSpec = transition(),
                    content = content,
                    label = "",
                    modifier = Modifier.fillMaxHeight().padding( top = topPadding )
                )

                if( navBarPos() == NavigationBarPosition.Right )
                    navigationBar.Draw()
            }

            val playerPosition by rememberPreference(playerPositionKey, PlayerPosition.Bottom)
            val playerAlignment =
                if (playerPosition == PlayerPosition.Top)
                    Alignment.TopCenter
                else
                    Alignment.BottomCenter

            Box(
                Modifier.padding( vertical = 200.dp )
                        .align( playerAlignment ),
                content = { mediaPlayer?.invoke() }
            )
        }
    }
}