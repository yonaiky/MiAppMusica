package it.fast4x.rimusic.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import database.MusicDatabaseDesktop
import it.fast4x.rimusic.ui.screens.ArtistsScreen
import it.fast4x.rimusic.ui.screens.QuickPicsScreen
import org.jetbrains.compose.resources.painterResource
import rimusic.composeapp.generated.resources.Res
import rimusic.composeapp.generated.resources.app_icon
import rimusic.composeapp.generated.resources.app_logo_text


@Composable
fun PlayerApp(
    navController: NavHostController = rememberNavController()
) {

    MusicDatabaseDesktop.getAll()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: "artists"

    Scaffold(
        topBar = {
            DesktopTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            DesktopBottomAppBar()
        }
    ) { innerPadding ->

        ThreeColumnsLayout()

    }


}

@Composable
fun ThreeColumnsLayout() {
    Row(Modifier.fillMaxSize()) {
        LeftPanelContent()
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = Color.Blue
        )
        CenterPanelContent()
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        RightPanelContent()
    }
}

@Composable
fun LeftPanelContent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.2f)
            .padding(horizontal = layoutColumnsHorizontalPadding)
            .padding(top = layoutColumnTopPadding)
    ) {
        Column(Modifier.fillMaxSize().border(1.dp, color = Color.Black)) {
            Text(text = "Left Panel  ", modifier = Modifier.padding(start = 8.dp, top = layoutColumnTopPadding))
        }
        //Spacer(Modifier.size(100.dp))
        //Text(text = "Left Pane bottom Text Box")
    }
}

@Composable
fun CenterPanelContent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.6f)
            .padding(horizontal = layoutColumnsHorizontalPadding)
            .padding(top = layoutColumnTopPadding)
    ) {
        Column(Modifier.fillMaxSize().border(1.dp, color = Color.Black)) {
            Text(text = "Center Panel", modifier = Modifier.padding(start = 8.dp, top = layoutColumnTopPadding))
        }
        //Spacer(Modifier.size(100.dp))
        //Text(text = "Left Pane bottom Text Box")
    }
}

@Composable
fun RightPanelContent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = layoutColumnsHorizontalPadding)
            .padding(top = layoutColumnTopPadding)
    ) {
        Text(text = "Right Panel")
        Spacer(Modifier.size(20.dp))
        Row(Modifier.border(1.dp, color = Color.Yellow)) {
            Text(text = "Right Pane Second Text", modifier = Modifier.weight(1f))
            Spacer(Modifier.size(20.dp))
            Text(text = "Right Pane Third Text", modifier = Modifier.weight(1f))
        }
    }
}
