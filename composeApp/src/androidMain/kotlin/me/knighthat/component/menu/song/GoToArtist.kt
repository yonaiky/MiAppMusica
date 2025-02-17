package me.knighthat.component.menu.song

import androidx.compose.runtime.Composable
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

class GoToArtist private constructor(
    private val navController: NavController,
    private val getSong: () -> Song
): MenuIcon, Descriptive {

    companion object {
        @Composable
        operator fun invoke( navController: NavController, getSong: () -> Song ): GoToArtist =
            GoToArtist( navController, getSong )
    }

    override val iconId: Int = R.drawable.artists
    // TODO: Add string "About this artist"
    override val messageId: Int = R.string.artists
    // TODO: Add string "About this artist" to strings.xml
    override val menuIconTitle: String
        @Composable
        get() = "About this artist"

    override fun onShortClick() {
        val song = getSong()

        // TODO: Add a fetch function to get artists' info if it's not in the database
        CoroutineScope( Dispatchers.IO ).future {
            Database.findArtistOfSong( song.id ).firstOrNull()
        }.get()?.let {
            navController.navigate(route = "${NavRoutes.artist.name}/${it.id}")
        }
    }
}