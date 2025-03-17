package me.knighthat.component.tab

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import app.kreate.android.R
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.utils.formatAsDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.knighthat.component.ImportFromFile
import me.knighthat.utils.Toaster
import me.knighthat.utils.csv.SongCSV
import java.io.InputStream

/**
 * Import songs from custom CSV file.
 *
 * If song is assigned to a playlist,
 * this will handle adding that song
 * to corresponding playlist upon process.
 *
 * This is the template of CSV file:
 *
 * | PlaylistBrowseId | PlaylistName | MediaId | Title | Artists | Duration | ThumbnailUrl |
 * | --- | --- | --- | --- | --- | --- | --- |
 * | 1 | Awesome Playlist | dQw4w9WgXcQ | A song | Anonymous | 212 | https://... |
 * | 2 | Summer | fNFzfwLM72c | Lovely | Anonymous | 250 | https://... |
 * | 3 | New Songs | kPa7bsKwL-c | DWAS | Anonymous | 253 | https://... |
 */
class ImportSongsFromCSV(
    launcher: ManagedActivityResultLauncher<Array<String>, Uri?>
) : ImportFromFile(launcher), MenuIcon, Descriptive {

    companion object {
        private fun parseFromCsvFile( inputStream: InputStream ): List<SongCSV> =
            csvReader().readAllWithHeader(inputStream)
                       .fastMap { row ->      // Experimental, revert back to [map] if needed
                           SongCSV(
                               playlistId = (row["PlaylistBrowseId"] ?: "-1").toLong(),
                               playlistName = row["PlaylistName"] ?: "",
                               songId = row["MediaId"] ?: "",
                               title = row["Title"] ?: "",
                               artists = row["Artists"] ?: "",
                               thumbnailUrl = row["ThumbnailUrl"] ?: "",
                               duration = (row["Duration"]?.toLong() ?: 0L).run {
                                   // Duration is saved as second
                                   formatAsDuration(this.times(1000L))
                               }
                           )
                       }
                       .toList()        // Make it immutable

        private fun importSongs( songs: List<SongCSV> ): Unit =
            Database.asyncTransaction {
                songs.fastForEach {
                    // Skip this song if it doesn't have id
                    if( it.songId.isBlank() ) return@fastForEach

                    this.songTable.upsert(      // Existing songs should be replaced by the one defined in CSV file, else, insert it.
                        Song(
                            id = it.songId,
                            title = it.title,
                            artistsText = it.artists,
                            thumbnailUrl = it.thumbnailUrl,
                            durationText = it.duration,
                            totalPlayTimeMs = 1L       // Bypass hidden song checker
                        )
                    )

                    // '-1' playlistId indicates song doesn't belong to any playlist
                    if( it.playlistId != -1L && it.playlistName.isNotBlank() ) {
                        playlistTable.upsert(       // Existing playlists should be replaced by the one defined in CSV file, else, insert it.
                            Playlist( it.playlistId, it.playlistName )
                        )

                        insertSongToPlaylist( it.songId, it.playlistId )
                    }
                }

                // Show message when it's done
                Toaster.done()
            }

        @Composable
        operator fun invoke() = ImportSongsFromCSV(
            rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri ->
                // [uri] must be non-null (meaning path exists) in order to work
                uri ?: return@rememberLauncherForActivityResult

                // Run in background to prevent UI thread
                // from freezing due to large file.
                CoroutineScope( Dispatchers.IO ).launch {
                    appContext().contentResolver
                                .openInputStream( uri )
                                ?.use( ::parseFromCsvFile )       // Use [use] because it closes stream on exit
                                ?.also( ::importSongs )
                }
            }
        )
    }

    override val supportedMimes: Array<String> = arrayOf("text/csv", "text/comma-separated-values")
    override val iconId: Int = R.drawable.resource_import
    override val messageId: Int = R.string.import_playlist
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )
}