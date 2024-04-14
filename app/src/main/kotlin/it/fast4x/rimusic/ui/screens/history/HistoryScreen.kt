package it.fast4x.rimusic.ui.screens.history

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.media3.common.util.UnstableApi
import it.fast4x.compose.persist.PersistMapCleanup
import it.fast4x.compose.routing.RouteHandler
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.StatisticsType
import it.fast4x.rimusic.models.Mood
import it.fast4x.rimusic.ui.components.Scaffold
import it.fast4x.rimusic.ui.screens.globalRoutes
import it.fast4x.rimusic.ui.screens.historyRoute
import it.fast4x.rimusic.ui.screens.homeRoute
import it.fast4x.rimusic.ui.screens.searchRoute
import it.fast4x.rimusic.ui.screens.settingsRoute
import it.fast4x.rimusic.ui.screens.statisticsTypeRoute

@ExperimentalMaterialApi
@ExperimentalTextApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@UnstableApi
@Composable
fun HistoryScreen() {
    val saveableStateHolder = rememberSaveableStateHolder()

    PersistMapCleanup(tagPrefix = "history")

    RouteHandler(listenToGlobalEmitter = true) {
        globalRoutes()

        host {
            Scaffold(
                topIconButtonId = R.drawable.chevron_back,
                onTopIconButtonClick = pop,
                topIconButton2Id = R.drawable.chevron_back,
                onTopIconButton2Click = pop,
                showButton2 = false,
                tabIndex = 0,
                onTabChanged = { },
                onHomeClick = { homeRoute() },
                showTopActions = false,
                /*
                onSettingsClick = { settingsRoute() },
                onStatisticsClick = { statisticsTypeRoute(StatisticsType.Today) },
                onHistoryClick = { historyRoute() },
                onSearchClick = { searchRoute("") },

                 */
                tabColumnContent = { item ->
                    item(0, stringResource(R.string.history), R.drawable.history)
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                    when (currentTabIndex) {
                        0 -> HistoryList()
                    }
                }
            }
        }
    }
}
