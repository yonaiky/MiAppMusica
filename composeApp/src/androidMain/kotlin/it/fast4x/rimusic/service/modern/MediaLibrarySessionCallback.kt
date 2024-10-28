package it.fast4x.rimusic.service.modern

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.service.MyDownloadHelper
import it.fast4x.rimusic.R
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongEntity
import it.fast4x.rimusic.utils.toggleRepeatMode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.plus
import javax.inject.Inject

@UnstableApi
class MediaLibrarySessionCallback @Inject constructor(
    @ApplicationContext val context: Context,
    val database: Database,
    val downloadHelper: MyDownloadHelper,
) : MediaLibrarySession.Callback {
    private val scope = CoroutineScope(Dispatchers.Main) + Job()
    var toggleLike: () -> Unit = {}
    var toggleDownload: () -> Unit = {}

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        return MediaSession.ConnectionResult.accept(
            connectionResult.availableSessionCommands.buildUpon()
                .add(MediaSessionConstants.CommandToggleDownload)
                .add(MediaSessionConstants.CommandToggleLike)
                .add(MediaSessionConstants.CommandToggleShuffle)
                .add(MediaSessionConstants.CommandToggleRepeatMode)
                .build(),
            connectionResult.availablePlayerCommands
        )
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle,
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            MediaSessionConstants.ACTION_TOGGLE_LIKE -> toggleLike()
            MediaSessionConstants.ACTION_TOGGLE_DOWNLOAD -> toggleDownload()
            MediaSessionConstants.ACTION_TOGGLE_SHUFFLE -> session.player.shuffleModeEnabled =
                !session.player.shuffleModeEnabled

            MediaSessionConstants.ACTION_TOGGLE_REPEAT_MODE -> session.player.toggleRepeatMode()
        }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    @OptIn(UnstableApi::class)
    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<MediaItem>> = Futures.immediateFuture(
        LibraryResult.ofItem(
            MediaItem.Builder()
                .setMediaId(PlayerServiceModern.ROOT)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setIsPlayable(false)
                        .setIsBrowsable(false)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                        .build()
                )
                .build(),
            params
        )
    )

    @OptIn(UnstableApi::class)
    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> = scope.future(Dispatchers.IO) {
        LibraryResult.ofItemList(
            when (parentId) {
                PlayerServiceModern.ROOT -> listOf(
                    browsableMediaItem(
                        PlayerServiceModern.SONG,
                        context.getString(R.string.songs),
                        null,
                        drawableUri(R.drawable.musical_notes),
                        MediaMetadata.MEDIA_TYPE_PLAYLIST
                    ),
                    browsableMediaItem(
                        PlayerServiceModern.ARTIST,
                        context.getString(R.string.artists),
                        null,
                        drawableUri(R.drawable.artist),
                        MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS
                    ),
                    browsableMediaItem(
                        PlayerServiceModern.ALBUM,
                        context.getString(R.string.albums),
                        null,
                        drawableUri(R.drawable.album),
                        MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS
                    ),
                    browsableMediaItem(
                        PlayerServiceModern.PLAYLIST,
                        context.getString(R.string.playlists),
                        null,
                        drawableUri(R.drawable.playlist),
                        MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS
                    )
                )

                PlayerServiceModern.SONG -> database.songsByRowIdAsc().first()
                    .map { it.song.toMediaItem(parentId) }

                PlayerServiceModern.ARTIST -> database.artistsByRowIdAsc().first().map { artist ->
                    browsableMediaItem(
                        "${PlayerServiceModern.ARTIST}/${artist.id}",
                        artist.name ?: "",
                        "",
                        artist.thumbnailUrl?.toUri(),
                        MediaMetadata.MEDIA_TYPE_ARTIST
                    )
                }

                PlayerServiceModern.ALBUM -> database.albumsByRowIdAsc().first().map { album ->
                    browsableMediaItem(
                        "${PlayerServiceModern.ALBUM}/${album.id}",
                        album.title ?: "",
                        album.authorsText,
                        album.thumbnailUrl?.toUri(),
                        MediaMetadata.MEDIA_TYPE_ALBUM
                    )
                }


                PlayerServiceModern.PLAYLIST -> {
                    val likedSongCount = database.likedSongsCount().first()
                    val downloadedSongCount = downloadHelper.downloads.value.size
                    val playlists = database.playlistPreviewsByDateSongCountAsc().first()
                    listOf(
                        browsableMediaItem(
                            "${PlayerServiceModern.PLAYLIST}/PREFERITES",
                            context.getString(R.string.favorites),
                            likedSongCount.toString(),
                            drawableUri(R.drawable.heart),
                            MediaMetadata.MEDIA_TYPE_PLAYLIST
                        ),
                        browsableMediaItem(
                            "${PlayerServiceModern.PLAYLIST}/DOWNLOADED",
                            context.getString(R.string.downloaded),
                            downloadedSongCount.toString(),
                            drawableUri(R.drawable.download),
                            MediaMetadata.MEDIA_TYPE_PLAYLIST
                        )
                    ) + playlists.map { playlist ->
                        browsableMediaItem(
                            "${PlayerServiceModern.PLAYLIST}/${playlist.playlist.id}",
                            playlist.playlist.name,
                            playlist.songCount.toString(),
                            null,
                            MediaMetadata.MEDIA_TYPE_PLAYLIST
                        )
                    }

                }


                else -> when {

                    parentId.startsWith("${PlayerServiceModern.ARTIST}/") ->
                        database.artistSongs(parentId.removePrefix("${PlayerServiceModern.ARTIST}/"))
                            .first().map {
                            it.toMediaItem(parentId)
                        }

                    parentId.startsWith("${PlayerServiceModern.ALBUM}/") ->
                        database.albumSongs(parentId.removePrefix("${PlayerServiceModern.ALBUM}/"))
                            .first().map {
                            it.toMediaItem(parentId)
                        }
                    /*
                                        parentId.startsWith("${PlayerServiceModern.PLAYLIST}/") ->
                                            when (val playlistId = parentId.removePrefix("${PlayerServiceModern.PLAYLIST}/")) {
                                                "PREFERITES" -> database.songsFavoritesByRowIdAsc()

                                                "DOWNLOADED" -> {
                                                    val downloads = downloadHelper.downloads.value
                                                    database.listAllSongsAsFlow()
                                                        .flowOn(Dispatchers.IO)
                                                        .map { songs ->
                                                            songs.filter {
                                                                downloads[it.song.id]?.state == Download.STATE_COMPLETED
                                                            }
                                                        }


                                                }

                                                else -> database.songsPlaylistByRowIdAsc(playlistId.toLong()).map { list ->
                                                    list.map { it.song }
                                                }
                                            }.first().map {
                                                it.toMediaItem(parentId)
                                            }
                    */
                    else -> emptyList()


                }

            },
            params
        )
    }

    @OptIn(UnstableApi::class)
    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String,
    ): ListenableFuture<LibraryResult<MediaItem>> = scope.future(Dispatchers.IO) {
        database.song(mediaId).first()?.toMediaItem("")?.let {
            LibraryResult.ofItem(it, null)
        } ?: LibraryResult.ofError(SessionError.ERROR_UNKNOWN)
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long,
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> = scope.future {
        // Play from Android Auto
        val defaultResult =
            MediaSession.MediaItemsWithStartPosition(emptyList(), startIndex, startPositionMs)
        val path = mediaItems.firstOrNull()?.mediaId?.split("/")
            ?: return@future defaultResult
        when (path.firstOrNull()) {
            PlayerServiceModern.SONG -> {
                val songId = path.getOrNull(1) ?: return@future defaultResult
                val allSongs = database.listAllSongsAsFlow().first()
                MediaSession.MediaItemsWithStartPosition(
                    allSongs.map { it.song.toMediaItem("") },
                    allSongs.indexOfFirst { it.song.id == songId }.takeIf { it != -1 } ?: 0,
                    startPositionMs
                )
            }

            PlayerServiceModern.ARTIST -> {
                val songId = path.getOrNull(2) ?: return@future defaultResult
                val artistId = path.getOrNull(1) ?: return@future defaultResult
                val songs = database.artistSongs(artistId).first()
                MediaSession.MediaItemsWithStartPosition(
                    songs.map { it.toMediaItem("") },
                    songs.indexOfFirst { it.id == songId }.takeIf { it != -1 } ?: 0,
                    startPositionMs
                )
            }

            PlayerServiceModern.ALBUM -> {
                val songId = path.getOrNull(2) ?: return@future defaultResult
                val albumId = path.getOrNull(1) ?: return@future defaultResult
                val albumWithSongs =
                    database.albumSongs(albumId).first() ?: return@future defaultResult
                MediaSession.MediaItemsWithStartPosition(
                    albumWithSongs.map { it.toMediaItem("") },
                    albumWithSongs.indexOfFirst { it.id == songId }.takeIf { it != -1 } ?: 0,
                    startPositionMs
                )
            }

            PlayerServiceModern.PLAYLIST -> {
                val songId = path.getOrNull(2) ?: return@future defaultResult
                val playlistId = path.getOrNull(1) ?: return@future defaultResult
                val songs = when (playlistId) {
                    "PREFERITES" -> database.songsFavoritesByRowIdDesc()
                    "DOWNLOADED" -> {
                        val downloads = downloadHelper.downloads.value
                        database.listAllSongsAsFlow()
                            .flowOn(Dispatchers.IO)
                            .map { songs ->
                                songs.filter {
                                    downloads[it.song.id]?.state == Download.STATE_COMPLETED
                                }
                            }
                            .map { songs ->
                                songs.map { it to downloads[it.song.id] }
                                    .sortedBy { it.second?.updateTimeMs ?: 0L }
                                    .map { it.first }
                            }
                    }

                    else -> database.songsPlaylistByRowIdAsc(playlistId.toLong()).map { list ->
                        list.map { it }
                    }
                }.first()

                MediaSession.MediaItemsWithStartPosition(
                    songs.map { it.song.toMediaItem("") },
                    songs.indexOfFirst { it.song.id == songId }.takeIf { it != -1 } ?: 0,
                    startPositionMs
                )
            }

            else -> defaultResult
        }
    }

    private fun drawableUri(@DrawableRes id: Int) = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(id))
        .appendPath(context.resources.getResourceTypeName(id))
        .appendPath(context.resources.getResourceEntryName(id))
        .build()

    private fun browsableMediaItem(
        id: String,
        title: String,
        subtitle: String?,
        iconUri: Uri?,
        mediaType: Int = MediaMetadata.MEDIA_TYPE_MUSIC
    ) =
        MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setArtist(subtitle)
                    .setArtworkUri(iconUri)
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .setMediaType(mediaType)
                    .build()
            )
            .build()

    private fun Song.toMediaItem(path: String) =
        MediaItem.Builder()
            .setMediaId("$path/${id}")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setSubtitle(artistsText)
                    .setArtist(artistsText)
                    .setArtworkUri(thumbnailUrl?.toUri())
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .build()
            )
            .build()
}

object MediaSessionConstants {
    const val ACTION_TOGGLE_DOWNLOAD = "TOGGLE_DOWNLOAD"
    const val ACTION_TOGGLE_LIKE = "TOGGLE_LIKE"
    const val ACTION_TOGGLE_SHUFFLE = "TOGGLE_SHUFFLE"
    const val ACTION_TOGGLE_REPEAT_MODE = "TOGGLE_REPEAT_MODE"
    val CommandToggleDownload = SessionCommand(ACTION_TOGGLE_DOWNLOAD, Bundle.EMPTY)
    val CommandToggleLike = SessionCommand(ACTION_TOGGLE_LIKE, Bundle.EMPTY)
    val CommandToggleShuffle = SessionCommand(ACTION_TOGGLE_SHUFFLE, Bundle.EMPTY)
    val CommandToggleRepeatMode = SessionCommand(ACTION_TOGGLE_REPEAT_MODE, Bundle.EMPTY)
}