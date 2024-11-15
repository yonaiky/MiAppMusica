package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.TopPlaylistPeriod
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.PeriodMenu
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.topPlaylistPeriodKey

class PeriodSelectorComponent private constructor(
    override val menuState: MenuState,
    private val periodState: MutableState<TopPlaylistPeriod>
): Menu {

    companion object {
        @JvmStatic
        @Composable
        fun init() = PeriodSelectorComponent(
            LocalMenuState.current,
            rememberPreference(topPlaylistPeriodKey, TopPlaylistPeriod.PastWeek)
        )
    }

    var period: TopPlaylistPeriod = periodState.value
        set(value) {
            periodState.value
            field = value
        }
    override val iconId: Int = R.drawable.stat

    fun onDismiss( period: TopPlaylistPeriod ) {
        this.period = period
        menuState.hide()
    }

    @Composable
    override fun MenuComponent() = PeriodMenu(::onDismiss)
}