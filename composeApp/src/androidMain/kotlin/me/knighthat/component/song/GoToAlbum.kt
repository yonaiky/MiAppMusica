package me.knighthat.component.song

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import app.kreate.android.R
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.bodies.SearchBody
import it.fast4x.innertube.requests.searchPage
import it.fast4x.innertube.utils.from
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.MODIFIED_PREFIX
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.future
import me.knighthat.utils.Toaster
import timber.log.Timber

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

        CoroutineScope( Dispatchers.IO ).future {
            var result = Database.albumTable.findBySongId( song.id ).first()?.id

            /*
             * If album isn't stored inside database, attempt to fetch
             *
             * There isn't an official way to get album from song's id (AFAIK).
             * The fastest way I can come up with is to search for song online
             * then grab first album from innertube as the result.
             *
             * TBH, not the best way, but it gets the job done, for now!
             */
            if( result == null ) {
                Toaster.n( R.string.looking_up_album_from_the_internet )

                var songTitle = song.cleanTitle()
                var songArtists = song.cleanArtistsText()
                /**
                 * Non-default title can lead to different results, therefore,
                 * a fetch for original title must be made.
                 */
                if( songTitle.startsWith( MODIFIED_PREFIX, true )
                    || songArtists.startsWith( MODIFIED_PREFIX, true )
                )
                    Innertube.searchPage(
                                 body = SearchBody(
                                     query = song.id,
                                     params = Innertube.SearchFilter.Song.value
                                 ),
                                 fromMusicShelfRendererContent = Innertube.SongItem.Companion::from
                             )
                             ?.onSuccess { response ->
                                 Timber.tag("go_to_album").d("Fetched song - ${response?.items?.size}")

                                 response?.items
                                         ?.first()
                                         ?.let { songItem ->
                                             songTitle = songItem.title ?: songTitle
                                             songArtists = songItem.authors
                                                                   ?.joinToString { it.name.toString() }
                                                                   .toString()
                                         }
                             }
                             ?.onFailure {
                                 Timber.tag("go_to_album").e(it)
                                 Toaster.e( R.string.failed_to_fetch_original_property )
                             }

                // If this value remains unchanged after fetch, it means fetch failed
                if( !songTitle.startsWith( MODIFIED_PREFIX, true )
                    && !songArtists.startsWith( MODIFIED_PREFIX, true )
                )
                    Innertube.searchPage(
                                 body = SearchBody(
                                     query = "$songTitle - $songArtists",
                                     params = Innertube.SearchFilter.Song.value
                                 ),
                                 fromMusicShelfRendererContent = Innertube.SongItem.Companion::from
                             )
                             ?.onSuccess { response ->
                                 response?.items
                                         ?.first()
                                         ?.album
                                         ?.endpoint
                                         ?.browseId
                                         ?.let {
                                             result = it
                                             Album(it)
                                         }
                                         ?.let { Database.mapIgnore( it, song ) }
                             }
                             ?.onFailure {
                                 Timber.tag("go_to_album").e(it)

                                 Toaster.e( R.string.failed_to_fetch_album )
                             }
            }

            result
        }.get()?.let { albumId ->
            navController.navigate(route = "${NavRoutes.album.name}/$albumId")
        }
    }
}