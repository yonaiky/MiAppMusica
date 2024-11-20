package me.knighthat.component.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R
import it.fast4x.rimusic.utils.autoShuffleKey
import it.fast4x.rimusic.utils.rememberPreference
import me.knighthat.component.tab.toolbar.Descriptive
import me.knighthat.component.tab.toolbar.DualIcon
import me.knighthat.component.tab.toolbar.DynamicColor
import me.knighthat.component.tab.toolbar.MenuIcon

@Composable
fun hiddenSongs(): DualIcon = object: MenuIcon, DualIcon, Descriptive {

    override val iconId: Int = R.drawable.eye
    override val secondIconId: Int = R.drawable.eye_off
    override val messageId: Int = R.string.info_show_hide_hidden_songs
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override var isFirstIcon: Boolean by rememberSaveable { mutableStateOf( false ) }

    override fun onShortClick() { isFirstIcon = !isFirstIcon }
}

@Composable
fun randomSort(): MenuIcon = object: MenuIcon, DynamicColor, Descriptive {

    override var isFirstColor: Boolean by rememberPreference( autoShuffleKey, false )
    override val iconId: Int = R.drawable.random
    override val messageId: Int = R.string.random_sorting
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() { isFirstColor = !isFirstColor }
}