package me.knighthat.component.menu.song

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.guava.future

class GoToAlbum private constructor(
    private val navController: NavController,
    private val getSong: () -> Song
): MenuIcon, Descriptive {

    companion object {
        @Composable
        operator fun invoke( navController: NavController, getSong: () -> Song ): GoToAlbum =
            GoToAlbum( navController, getSong )
    }

    override val iconId: Int = R.drawable.album
    override val messageId: Int = R.string.go_to_album
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        val song = getSong()

        // TODO: Add a fetch function to get album' info if it's not in the database
        CoroutineScope( Dispatchers.IO ).future {
            Database.findAlbumOfSong( song.id ).firstOrNull()
        }.get()?.let {
            navController.navigate(route = "${NavRoutes.artist.name}/${it.id}")
        }
    }
}