package it.fast4x.rimusic.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import it.fast4x.compose.persist.persistList
import it.fast4x.piped.Piped
import it.fast4x.piped.models.Session
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.MainApplication
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.screens.home.PIPED_PREFIX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import kotlinx.serialization.Serializable

fun syncSongsInPipedPlaylist(context: Context,coroutineScope: CoroutineScope, pipedSession: Session, idPipedPlaylist: UUID, playlistId: Long) {

   if (!checkPipedAccount(context, pipedSession)) return

    coroutineScope.launch(Dispatchers.IO) {
        async {
            Piped.playlist.songs(
                session = pipedSession,
                id = idPipedPlaylist
            )
        }.await()?.map {playlist ->

            println("pipedInfo syncSongsInPipedPlaylist playlistId $playlistId songs ${playlist.videos.size}")

            playlistId.let {
                transaction {
                    Database.clearPlaylist(it)
                }
            }

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
    val pipedApiToken = MainApplication.pipedApiToken ?: ""
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
    val context = LocalContext.current
    val isPipedEnabled by rememberPreference(isPipedEnabledKey, false)
    val pipedSession = getPipedSession()
    if (isPipedEnabled && (pipedSession.token == "" || pipedSession.token.isEmpty())) {
        SmartMessage(stringResource(R.string.info_connect_your_piped_account_first), PopupType.Warning, context = context)
        return
    }

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

fun addToPipedPlaylist(context: Context, coroutineScope: CoroutineScope, pipedSession: Session, id: UUID, videos: List<String>) {
    if (!checkPipedAccount(context, pipedSession)) return
    coroutineScope.launch(Dispatchers.IO) {
            Piped.playlist.add(session = pipedSession, id = id, videos = videos.map { it.toID() })

    }

}

fun removeFromPipedPlaylist(context: Context, coroutineScope: CoroutineScope, pipedSession: Session, id: UUID, idx: Int) {
    if (!checkPipedAccount(context, pipedSession)) return
    coroutineScope.launch(Dispatchers.IO) {
        Piped.playlist.remove(session = pipedSession, id = id, idx = idx)

    }

}

fun deletePipedPlaylist(context: Context, coroutineScope: CoroutineScope, pipedSession: Session, id: UUID) {
    if (!checkPipedAccount(context, pipedSession)) return
    coroutineScope.launch(Dispatchers.IO) {
        Piped.playlist.delete(session = pipedSession, id = id)

    }

}

fun renamePipedPlaylist(context: Context, coroutineScope: CoroutineScope, pipedSession: Session, id: UUID, name: String) {
    if (!checkPipedAccount(context, pipedSession)) return
    coroutineScope.launch(Dispatchers.IO) {
        Piped.playlist.rename(session = pipedSession, id = id, name = name)

    }

}

fun createPipedPlaylist(context: Context, coroutineScope: CoroutineScope, pipedSession: Session, name: String): Long {
    var playlistId: Long = -1
    if (!checkPipedAccount(context, pipedSession)) return playlistId

    coroutineScope.launch(Dispatchers.IO) {
        async {
            Piped.playlist.create(session = pipedSession, name = name)
        }.await()?.map {
           playlistId = Database.insert(Playlist(name = "$PIPED_PREFIX$name", browseId = it.id.toString()))
        }
    }

    Timber.d("pipedInfo new playlistId $playlistId")

    return playlistId
}

fun String.toID(): String {
    return this
        .replace("/watch?v=", "") // videos
        .replace("/channel/", "") // channels
        .replace("/playlist?list=", "") // playlists
}

fun checkPipedAccount(context: Context, pipedSession: Session): Boolean {
    val isPipedEnabled = context.preferences.getBoolean(isPipedEnabledKey, false)
    if (isPipedEnabled && (pipedSession.token == "" || pipedSession.token.isEmpty())) {
        SmartMessage(context.getString(R.string.info_connect_your_piped_account_first), PopupType.Warning, context = context)
        return false
    }
    return true
}