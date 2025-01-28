package it.fast4x.rimusic.utils

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.YtMusic
import it.fast4x.innertube.utils.completed
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.ui.screens.settings.isYouTubeSyncEnabled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

suspend fun importYTMPrivatePlaylists() {
    if (!isYouTubeSyncEnabled()) return

    Innertube.library("FEmusic_liked_playlists").completed().onSuccess { page ->

        val ytmPrivatePlaylists = page.items.filterIsInstance<Innertube.PlaylistItem>()
            .filterNot { it.key == "VLLM" || it.key == "VLSE" }

        val localPlaylists = Database.ytmPrivatePlaylists().firstOrNull()

        //coroutineScope {
        ytmPrivatePlaylists.forEach { remotePlaylist ->
            withContext(Dispatchers.IO) {
                val playlistIdChecked =
                    if (remotePlaylist.key.startsWith("VL")) remotePlaylist.key.substringAfter("VL") else remotePlaylist.key
                var localPlaylist =
                    localPlaylists?.find { it?.browseId == playlistIdChecked }
                println("Local playlist: $localPlaylist")
                println("Remote playlist: $remotePlaylist")
                if (localPlaylist == null && playlistIdChecked.isNotEmpty()) {
                    localPlaylist = Playlist(
                        name = remotePlaylist.title ?: "",
                        browseId = playlistIdChecked,
                    )
                    Database.insert(localPlaylist.copy(browseId = playlistIdChecked))
                }

                Database.playlistWithSongsByBrowseId(playlistIdChecked).firstOrNull()?.let {
                    if (it.songs.isEmpty())
                        localPlaylist?.id?.let { it1 -> ytmPlaylistSync(it.playlist, it1) }
                }
            }
        }
        //}
    }.onFailure {
        println("Error importing YTM private playlists: ${it.stackTraceToString()}")
    }

}

@OptIn(UnstableApi::class)
fun ytmPlaylistSync(playlist: Playlist, playlistId: Long) {
    playlist.let { plist ->
        Database.asyncTransaction {
            runBlocking(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    plist.browseId?.let {
                        YtMusic.getPlaylist(
                            playlistId = it
                        ).completed()
                    }
                }
            }?.getOrNull()?.let { remotePlaylist ->
                Database.clearPlaylist(playlistId)

                remotePlaylist.songs
                    .map(Innertube.SongItem::asMediaItem)
                    .onEach(Database::insert)
                    .mapIndexed { position, mediaItem ->
                        SongPlaylistMap(
                            songId = mediaItem.mediaId,
                            playlistId = playlistId,
                            position = position
                        )
                    }.let(Database::insertSongPlaylistMaps)
            }
        }

    }
}