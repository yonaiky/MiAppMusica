package me.knighthat.component.tab.toolbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.MONTHLY_PREFIX
import it.fast4x.rimusic.PINNED_PREFIX
import it.fast4x.rimusic.PIPED_PREFIX
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.MenuStyle
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.PlaylistSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.components.themed.InputTextDialog
import it.fast4x.rimusic.ui.components.themed.Menu
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.ui.components.themed.SecondaryTextButton
import it.fast4x.rimusic.utils.menuStyleKey
import it.fast4x.rimusic.utils.playlistSortByKey
import it.fast4x.rimusic.utils.playlistSortOrderKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold
import kotlinx.coroutines.Dispatchers
import me.knighthat.colorPalette
import me.knighthat.typography

class AddToPlaylistComponent private constructor(
    override val menuState: MenuState,
    override val styleState: MutableState<MenuStyle>,
    private val onAdd: (PlaylistPreview) -> Unit,
    private val navController: NavController
): MenuIcon, Menu {

    companion object {
        @JvmStatic
        @Composable
        fun init(
            navController: NavController,
            onAdd: (PlaylistPreview) -> Unit
        ) = AddToPlaylistComponent(
            LocalMenuState.current,
            rememberPreference( menuStyleKey, MenuStyle.List ),
            onAdd,
            navController
        )
    }

    override val title: String
        @Composable
        get() = stringResource( R.string.add_to_playlist )
    override val iconId: Int = R.drawable.add_in_playlist

    @Composable
    override fun ListMenu() {
        val sortBy by rememberPreference(playlistSortByKey, PlaylistSortBy.DateAdded)
        val sortOrder by rememberPreference(playlistSortOrderKey, SortOrder.Descending)
        val playlistPreviews by remember {
            Database.playlistPreviews(sortBy, sortOrder)
        }.collectAsState(initial = emptyList(), context = Dispatchers.IO)

        val pinnedPlaylists = playlistPreviews.filter {
            it.playlist.name.startsWith(PINNED_PREFIX, 0, true)
        }

        val unpinnedPlaylists = playlistPreviews.filter {
            !it.playlist.name.startsWith(PINNED_PREFIX, 0, true) &&
                    !it.playlist.name.startsWith(MONTHLY_PREFIX, 0, true)
        }

        var isCreatingNewPlaylist by rememberSaveable { mutableStateOf( false ) }

        if ( isCreatingNewPlaylist ) {
            InputTextDialog(
                onDismiss = { isCreatingNewPlaylist = false },
                title = stringResource(R.string.enter_the_playlist_name),
                value = "",
                placeholder = stringResource(R.string.enter_the_playlist_name),
                setValue = { text ->
                    transaction {
                        val playlistId = Database.insert(Playlist(name = text))
                        onAdd(
                            PlaylistPreview(
                                Playlist(
                                    id = playlistId,
                                    name = text
                                ),
                                0
                            )
                        )
                    }

                    menuState.hide()
                }
            )
        }

        Menu( modifier = modifier ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = { menuState.hide() },
                    icon = R.drawable.chevron_back,
                    color = colorPalette().textSecondary,
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .size(20.dp)
                )

                SecondaryTextButton(
                    text = stringResource(R.string.new_playlist),
                    onClick = { isCreatingNewPlaylist = true },
                    alternative = true
                )
            }

            if (pinnedPlaylists.isNotEmpty()) {
                BasicText(
                    text = stringResource(R.string.pinned_playlists),
                    style = typography().m.semiBold,
                    modifier = modifier.padding(start = 20.dp, top = 5.dp)
                )

                pinnedPlaylists.forEach { playlistPreview ->
                    MenuEntry(
                        icon = R.drawable.add_in_playlist,
                        text = playlistPreview.playlist.name.substringAfter(
                            PINNED_PREFIX
                        ),
                        secondaryText = "${playlistPreview.songCount} " + stringResource(
                            R.string.songs
                        ),
                        onClick = {
                            onAdd(
                                PlaylistPreview(
                                    playlistPreview.playlist,
                                    playlistPreview.songCount
                                )
                            )
                            menuState.hide()
                        },
                        trailingContent = {
                            IconButton(
                                icon = R.drawable.open,
                                color = colorPalette().text,
                                onClick = {
                                    menuState.hide()
                                    navController.navigate(route = "${NavRoutes.localPlaylist.name}/${playlistPreview.playlist.id}")
                                },
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    )
                }
            }

            if (unpinnedPlaylists.isNotEmpty()) {
                BasicText(
                    text = stringResource(R.string.playlists),
                    style = typography().m.semiBold,
                    modifier = modifier.padding(start = 20.dp, top = 5.dp)
                )

                unpinnedPlaylists.forEach { playlistPreview ->
                    MenuEntry(
                        icon = R.drawable.add_in_playlist,
                        text = playlistPreview.playlist.name,
                        secondaryText = "${playlistPreview.songCount} " + stringResource(
                            R.string.songs
                        ),
                        onClick = {
                            onAdd(
                                PlaylistPreview(
                                    playlistPreview.playlist,
                                    playlistPreview.songCount
                                )
                            )
                            menuState.hide()
                        },
                        trailingContent = {
                            if (playlistPreview.playlist.name.startsWith(PIPED_PREFIX, 0, true))
                                Image(
                                    painter = painterResource(R.drawable.piped_logo),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(colorPalette().red),
                                    modifier = Modifier
                                        .size(18.dp)
                                )

                            IconButton(
                                icon = R.drawable.open,
                                color = colorPalette().text,
                                onClick = {
                                    menuState.hide()
                                    navController.navigate(route = "${NavRoutes.localPlaylist.name}/${playlistPreview.playlist.id}")
                                },
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    override fun GridMenu() {
        val sortBy by rememberPreference(playlistSortByKey, PlaylistSortBy.DateAdded)
        val sortOrder by rememberPreference(playlistSortOrderKey, SortOrder.Descending)
        val playlistPreviews by remember {
            Database.playlistPreviews(sortBy, sortOrder)
        }.collectAsState(initial = emptyList(), context = Dispatchers.IO)

        val pinnedPlaylists = playlistPreviews.filter {
            it.playlist.name.startsWith(PINNED_PREFIX, 0, true)
        }

        val unpinnedPlaylists = playlistPreviews.filter {
            !it.playlist.name.startsWith(PINNED_PREFIX, 0, true) &&
                    !it.playlist.name.startsWith(MONTHLY_PREFIX, 0, true)
        }

        var isCreatingNewPlaylist by rememberSaveable { mutableStateOf( false ) }

        Menu(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = { menuState.hide() },
                    icon = R.drawable.chevron_back,
                    color = colorPalette().textSecondary,
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .size(20.dp)
                )

                SecondaryTextButton(
                    text = stringResource(R.string.new_playlist),
                    onClick = { isCreatingNewPlaylist = true },
                    alternative = true
                )
            }

            if (pinnedPlaylists.isNotEmpty()) {
                BasicText(
                    text = stringResource(R.string.pinned_playlists),
                    style = typography().m.semiBold,
                    modifier = modifier.padding(start = 20.dp, top = 5.dp)
                )

                pinnedPlaylists.forEach { playlistPreview ->
                    MenuEntry(
                        icon = R.drawable.add_in_playlist,
                        text = playlistPreview.playlist.name.substringAfter(
                            PINNED_PREFIX
                        ),
                        secondaryText = "${playlistPreview.songCount} " + stringResource(
                            R.string.songs
                        ),
                        onClick = {
                            onAdd(
                                PlaylistPreview(
                                    playlistPreview.playlist,
                                    playlistPreview.songCount
                                )
                            )
                            menuState.hide()
                        },
                        trailingContent = {
                            IconButton(
                                icon = R.drawable.open,
                                color = colorPalette().text,
                                onClick = {
                                    menuState.hide()
                                    navController.navigate(route = "${NavRoutes.localPlaylist.name}/${playlistPreview.playlist.id}")
                                },
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    )
                }
            }

            if (unpinnedPlaylists.isNotEmpty()) {
                BasicText(
                    text = stringResource(R.string.playlists),
                    style = typography().m.semiBold,
                    modifier = modifier.padding(start = 20.dp, top = 5.dp)
                )

                unpinnedPlaylists.forEach { playlistPreview ->
                    MenuEntry(
                        icon = R.drawable.add_in_playlist,
                        text = playlistPreview.playlist.name,
                        secondaryText = "${playlistPreview.songCount} " + stringResource(
                            R.string.songs
                        ),
                        onClick = {
                            onAdd(
                                PlaylistPreview(
                                    playlistPreview.playlist,
                                    playlistPreview.songCount
                                )
                            )
                            menuState.hide()
                        },
                        trailingContent = {
                            if (playlistPreview.playlist.name.startsWith(PIPED_PREFIX, 0, true))
                                Image(
                                    painter = painterResource(R.drawable.piped_logo),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(colorPalette().red),
                                    modifier = Modifier
                                        .size(18.dp)
                                )

                            IconButton(
                                icon = R.drawable.open,
                                color = colorPalette().text,
                                onClick = {
                                    menuState.hide()
                                    navController.navigate(route = "${NavRoutes.localPlaylist.name}/${playlistPreview.playlist.id}")
                                },
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    override fun MenuComponent() {
        if( styleState.value == MenuStyle.Grid )
            GridMenu()
        else
            ListMenu()
    }
}