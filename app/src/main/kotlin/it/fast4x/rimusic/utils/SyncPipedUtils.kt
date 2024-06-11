package it.fast4x.rimusic.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import it.fast4x.compose.persist.persistList
import it.fast4x.piped.Piped
import it.fast4x.piped.models.Session
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.screens.home.PIPED_PREFIX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

fun syncSongsInPipedPlaylist(coroutineScope: CoroutineScope, pipedSession: Session, idPipedPlaylist: UUID, playlistId: Long) {

    coroutineScope.launch(Dispatchers.IO) {
        async {
            Piped.playlist.songs(
                session = pipedSession,
                id = idPipedPlaylist
            )
        }.await()?.map {playlist ->

            playlist.videos.forEach {video ->
                val song = video.id?.let { id ->
                    Song(
                        id = id,
                        title = video.cleanTitle,
                        artistsText = video.cleanArtists,
                        durationText = video.durationText,
                        thumbnailUrl = video.thumbnailUrl.toString()
                    )
                }
                if (song != null) {
                    Database.insert(song)
                }
            }
            playlist.videos.forEachIndexed { index, song ->
                Database.insert(
                    SongPlaylistMap(
                        songId = song.id.toString(),
                        playlistId = playlistId,
                        position = index
                    )
                )
            }

        }
    }
}
@Composable
fun TestPipedPlaylists() {
    val isPipedEnabled by rememberPreference(isPipedEnabledKey, false)
    val pipedApiToken by rememberEncryptedPreference(pipedApiTokenKey, "")
    if (isPipedEnabled && pipedApiToken.isNotEmpty()) {
        val pipedSession = getPipedSession()
        LaunchedEffect(Unit) {
            async {
                Piped.playlist.listTest(session = pipedSession.toApiSession())
            }.await()
        }
    }
}
@Composable
fun ImportPipedPlaylists(){

    val coroutineScope = rememberCoroutineScope()

    val isPipedEnabled by rememberPreference(isPipedEnabledKey, false)
    val itemsPiped by persistList<it.fast4x.piped.models.PlaylistPreview>("home/pipedPlaylists")
    val pipedApiToken by rememberEncryptedPreference(pipedApiTokenKey, "")
    if (isPipedEnabled && pipedApiToken.isNotEmpty()) {
        val pipedSession = getPipedSession()
        //println("pipedInfo ${pipedSession}")
        LaunchedEffect(Unit) {
            async {
                Piped.playlist.list(session = pipedSession.toApiSession())
            }.await()?.map {
                //itemsPiped = it
                transaction {
                    it.forEach {
                        val playlistExist = Database.playlistExistByName("$PIPED_PREFIX${it.name}")
                        if (playlistExist == 0L) {
                            val playlistId =
                                Database.insert(
                                    Playlist(
                                        name = "$PIPED_PREFIX${it.name}",
                                        browseId = it.id.toString()
                                    )
                                )
                            coroutineScope.launch(Dispatchers.IO) {
                                async {
                                    Piped.playlist.songs(
                                        session = pipedSession.toApiSession(),
                                        id = it.id
                                    )
                                }.await()?.map {playlist ->

                                    playlist.videos.forEach {video ->
                                        val song = video.id?.let { id ->
                                            Song(
                                                id = id,
                                                title = video.cleanTitle,
                                                artistsText = video.cleanArtists,
                                                durationText = video.durationText,
                                                thumbnailUrl = video.thumbnailUrl.toString()
                                            )
                                        }
                                        if (song != null) {
                                            Database.insert(song)
                                        }
                                    }
                                    playlist.videos.forEachIndexed { index, song ->
                                        Database.insert(
                                            SongPlaylistMap(
                                                songId = song.id.toString(),
                                                playlistId = playlistId,
                                                position = index
                                            )
                                        )
                                    }

                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

fun addSongsToPipedPlaylist(coroutineScope: CoroutineScope, pipedSession: Session, id: UUID, videos: List<String>) {

    coroutineScope.launch(Dispatchers.IO) {
        runCatching {
            Piped.playlist.add(session = pipedSession, id = id, videos = videos)
        }.onFailure {
            Timber.e(it)
        }
        /*
        async {
            Piped.playlist.add(session = pipedSession, id = id, videos = videos)
        }.await()?.onFailure {
            println("pipedInfo addSongsToPlaylist ${it.message}")
        }?.onSuccess {
            println("pipedInfo addSongsToPlaylist success")
        }

         */
    }

}

/*
@Composable
fun AddSongsToPipedPlaylist(id: UUID, videos: List<String>) {
    val isPipedEnabled by rememberPreference(isPipedEnabledKey, false)
    val pipedApiToken by rememberEncryptedPreference(pipedApiTokenKey, "")
    if (isPipedEnabled && pipedApiToken.isNotEmpty()) {
        val pipedSession = getPipedSession()
        LaunchedEffect(Unit) {
            async {
                Piped.playlist.add(session = pipedSession.toApiSession(), id = id, videos = videos)
            }.await()?.onFailure {
                println("pipedInfo addSongsToPlaylist ${it.message}")
            }
        }
    }
}
 */

