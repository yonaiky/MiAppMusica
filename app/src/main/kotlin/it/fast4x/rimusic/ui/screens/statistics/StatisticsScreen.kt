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
import it.fast4x.compose.persist.PersistMapCleanup
import it.fast4x.compose.routing.RouteHandler
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.StatisticsType
import it.fast4x.rimusic.ui.components.Scaffold
import it.fast4x.rimusic.ui.screens.globalRoutes
import it.fast4x.rimusic.ui.screens.searchRoute
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.showSearchTabKey

@ExperimentalMaterialApi
@ExperimentalTextApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@UnstableApi
@Composable
fun StatisticsScreen(
    statisticsType: StatisticsType
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
    val showSearchTab by rememberPreference(showSearchTabKey, false)

    RouteHandler(listenToGlobalEmitter = true) {
        globalRoutes()

        host {
            Scaffold(
                topIconButtonId = R.drawable.chevron_back,
                onTopIconButtonClick = pop,
                topIconButton2Id = R.drawable.chevron_back,
                onTopIconButton2Click = pop,
                showButton2 = false,
                showBottomButton = false, //showSearchTab,
                onBottomIconButtonClick = { searchRoute("") },
                tabIndex = tabIndex,
                onTabChanged = onTabIndexChanged,
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
                        0 -> StatisticsPage(statisticsType = StatisticsType.Today)
                        1 -> StatisticsPage(statisticsType = StatisticsType.OneWeek)
                        2 -> StatisticsPage(statisticsType = StatisticsType.OneMonth)
                        3 -> StatisticsPage(statisticsType = StatisticsType.ThreeMonths)
                        4 -> StatisticsPage(statisticsType = StatisticsType.SixMonths)
                        5 -> StatisticsPage(statisticsType = StatisticsType.OneYear)
                        6 -> StatisticsPage(statisticsType = StatisticsType.All)
                    }
                }
            }
        }
    }
}
