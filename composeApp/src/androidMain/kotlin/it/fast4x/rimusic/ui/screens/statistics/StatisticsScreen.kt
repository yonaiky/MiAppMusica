package it.fast4x.rimusic.ui.screens.statistics

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import it.fast4x.compose.persist.PersistMapCleanup
import it.fast4x.compose.routing.RouteHandler
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.StatisticsType
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.ui.components.Scaffold
import it.fast4x.rimusic.ui.screens.globalRoutes
import it.fast4x.rimusic.utils.UiTypeKey
import it.fast4x.rimusic.utils.rememberPreference

@ExperimentalMaterialApi
@ExperimentalTextApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@UnstableApi
@Composable
fun StatisticsScreen(
    navController: NavController,
    statisticsType: StatisticsType,
    playerEssential: @Composable () -> Unit = {},
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    val (tabIndex, onTabIndexChanged) = rememberSaveable {
        mutableStateOf(when (statisticsType) {
            StatisticsType.Today -> 0
            StatisticsType.OneWeek -> 1
            StatisticsType.OneMonth -> 2
            StatisticsType.ThreeMonths -> 3
            StatisticsType.SixMonths -> 4
            StatisticsType.OneYear -> 5
            StatisticsType.All -> 6

        })
    }

    PersistMapCleanup(tagPrefix = "${statisticsType.name}/")

    RouteHandler(listenToGlobalEmitter = true) {
        globalRoutes()
        val uiType  by rememberPreference(UiTypeKey, UiType.RiMusic)
        host {
            Scaffold(
                navController = navController,
                playerEssential = playerEssential,
                onTopIconButtonClick = pop,
                showButton1 = uiType != UiType.RiMusic,
                onTopIconButton2Click = pop,
                onBottomIconButtonClick = {
                    //searchRoute("")
                    navController.navigate(NavRoutes.search.name)
                },
                tabIndex = tabIndex,
                onTabChanged = onTabIndexChanged,
                /*
                onSettingsClick = { settingsRoute() },
                onStatisticsClick = { statisticsTypeRoute(StatisticsType.Today) },
                onHistoryClick = { historyRoute() },
                onSearchClick = { searchRoute("") },
                 */
                tabColumnContent = { Item ->
                    Item(0, stringResource(R.string.today), R.drawable.stat_today)
                    Item(1, stringResource(R.string._1_week), R.drawable.stat_week)
                    Item(2, stringResource(R.string._1_month), R.drawable.stat_month)
                    Item(3, stringResource(R.string._3_month), R.drawable.stat_3months)
                    Item(4, stringResource(R.string._6_month), R.drawable.stat_6months)
                    Item(5, stringResource(R.string._1_year), R.drawable.stat_year)
                    Item(6, stringResource(R.string.all), R.drawable.calendar_clear)
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                    when (currentTabIndex) {
                        0 -> StatisticsPageModern(
                            navController = navController,
                            statisticsType = StatisticsType.Today
                        )
                        1 -> StatisticsPageModern(
                            navController = navController,
                            statisticsType = StatisticsType.OneWeek
                        )
                        2 -> StatisticsPageModern(
                            navController = navController,
                            statisticsType = StatisticsType.OneMonth
                        )
                        3 -> StatisticsPageModern(
                            navController = navController,
                            statisticsType = StatisticsType.ThreeMonths
                        )
                        4 -> StatisticsPageModern(
                            navController = navController,
                            statisticsType = StatisticsType.SixMonths
                        )
                        5 -> StatisticsPageModern(
                            navController = navController,
                            statisticsType = StatisticsType.OneYear
                        )
                        6 -> StatisticsPageModern(
                            navController = navController,
                            statisticsType = StatisticsType.All
                        )
                    }
                }
            }
        }
    }
}
