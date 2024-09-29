package me.knighthat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.NavigationBarType
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.utils.UiTypeKey
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.navigationBarTypeKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.showSearchTabKey
import it.fast4x.rimusic.utils.showStatsInNavbarKey

@Composable
fun uiType() = rememberPreference( UiTypeKey, UiType.RiMusic ).value

@Composable
fun navBarType() = rememberPreference( navigationBarTypeKey, NavigationBarType.IconAndText ).value

@Composable
fun navBarPos() = rememberPreference(navigationBarPositionKey, NavigationBarPosition.Bottom).value

@Composable
fun typography() = LocalAppearance.current.typography

@Composable
@ReadOnlyComposable
fun colorPalette() = LocalAppearance.current.colorPalette

@Composable
fun thumbnailShape() = LocalAppearance.current.thumbnailShape

@Composable
fun showSearchIconInNav() = rememberPreference( showSearchTabKey, false ).value

@Composable
fun showStatsIconInNav() = rememberPreference( showStatsInNavbarKey, false ).value