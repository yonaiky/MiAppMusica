package it.fast4x.rimusic

import androidx.compose.ui.util.fastZip
import androidx.media3.common.MediaItem
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomWarnings
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Artist
import it.fast4x.rimusic.models.Event
import it.fast4x.rimusic.models.EventWithSong
import it.fast4x.rimusic.models.Format
import it.fast4x.rimusic.models.Info
import it.fast4x.rimusic.models.Lyrics
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.models.QueuedMediaItem
import it.fast4x.rimusic.models.SearchQuery
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongAlbumMap
import it.fast4x.rimusic.models.SongArtistMap
import it.fast4x.rimusic.models.SongEntity
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.models.SongWithContentLength
import it.fast4x.rimusic.models.SortedSongPlaylistMap
import it.fast4x.rimusic.service.LOCAL_KEY_PREFIX
import it.fast4x.rimusic.utils.isExplicit
import kotlinx.coroutines.flow.Flow
import me.knighthat.database.AlbumTable
import me.knighthat.database.ArtistTable
import me.knighthat.database.Converters
import me.knighthat.database.EventTable
import me.knighthat.database.FormatTable
import me.knighthat.database.LyricsTable
import me.knighthat.database.PlaylistTable
import me.knighthat.database.QueuedMediaItemTable
import me.knighthat.database.SearchQueryTable
import me.knighthat.database.SongAlbumMapTable
import me.knighthat.database.SongArtistMapTable
import me.knighthat.database.SongPlaylistMapTable
import me.knighthat.database.SongTable
import me.knighthat.database.migration.From10To11Migration
import me.knighthat.database.migration.From11To12Migration
import me.knighthat.database.migration.From14To15Migration
import me.knighthat.database.migration.From20To21Migration
import me.knighthat.database.migration.From21To22Migration
import me.knighthat.database.migration.From22To23Migration
import me.knighthat.database.migration.From23To24Migration
import me.knighthat.database.migration.From24To25Migration
import me.knighthat.database.migration.From25To26Migration
import me.knighthat.database.migration.From26To27Migration
import me.knighthat.database.migration.From3To4Migration
import me.knighthat.database.migration.From7To8Migration
import me.knighthat.database.migration.From8To9Migration

@Dao
interface Database {
    companion object : Database by DatabaseInitializer.Instance.database

    private val _internal: RoomDatabase
        get() = DatabaseInitializer.Instance

    val songTable: SongTable
        get() = DatabaseInitializer.Instance.songTable
    val albumTable: AlbumTable
        get() = DatabaseInitializer.Instance.albumTable
    val artistTable: ArtistTable
        get() = DatabaseInitializer.Instance.artistTable
    val eventTable: EventTable
        get() = DatabaseInitializer.Instance.eventTable
    val formatTable: FormatTable
        get() = DatabaseInitializer.Instance.formatTable
    val lyricsTable: LyricsTable
        get() = DatabaseInitializer.Instance.lyricsTable
    val playlistTable: PlaylistTable
        get() = DatabaseInitializer.Instance.playlistTable
    val queueTable: QueuedMediaItemTable
        get() = DatabaseInitializer.Instance.queueTable
    val searchTable: SearchQueryTable
        get() = DatabaseInitializer.Instance.searchQueryTable
    val songAlbumMapTable: SongAlbumMapTable
        get() = DatabaseInitializer.Instance.songAlbumMapTable
    val songArtistMapTable: SongArtistMapTable
        get() = DatabaseInitializer.Instance.songArtistMapTable
    val songPlaylistMapTable: SongPlaylistMapTable
        get() = DatabaseInitializer.Instance.songPlaylistMapTable

    //**********************************************

    @Transaction
    @Query("SELECT * FROM Format WHERE songId = :songId ORDER BY bitrate DESC LIMIT 1")
    fun getBestFormat(songId: String): Flow<Format?>

    @Transaction
    @Query("SELECT * FROM Format ORDER BY lastModified DESC LIMIT 1")
    fun getLastBestFormat(): Flow<Format?>

    @Transaction
    @Query("SELECT * FROM Song WHERE id in (SELECT songId FROM Format ORDER BY lastModified DESC LIMIT 1)")
    fun getLastSongPlayed(): Flow<Song?>

    @Transaction
    @Query("SELECT COUNT(id) from Song WHERE id = :id and title LIKE '${EXPLICIT_PREFIX}%'")
    fun isSongExplicit(id: String): Int

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT DISTINCT (timestamp / 86400000) as timestampDay, event.* FROM event ORDER BY rowId DESC")
    fun events(): Flow<List<EventWithSong>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT Event.* FROM Event JOIN Song ON Song.id = songId WHERE " +
            "Event.timestamp / 86400000 = :date / 86400000 LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun eventWithSongByPeriod(date: Long, limit:Long = Long.MAX_VALUE): Flow<List<EventWithSong>>

    @Transaction
    @Query("SELECT * FROM Song WHERE totalPlayTimeMs > 0 ORDER BY totalPlayTimeMs DESC LIMIT :count")
    @RewriteQueriesToDropUnusedColumns
    fun topSongs(count: Int = 10): Flow<List<Song>>

    @Transaction
    @Query("SELECT count(playlistId) FROM SongPlaylistMap WHERE songId = :id")
    fun songUsedInPlaylists(id: String): Int

    data class PlayListIdPosition(val playlistId: Long, val position: Int)
    @Transaction
    @Query("SELECT playlistId, position FROM SongPlaylistMap WHERE songId = :id")
    fun playlistsUsedForSong(id: String): List<PlayListIdPosition>

    @Transaction
    @Query("SELECT position FROM SongPlaylistMap WHERE playlistId = :playlistId AND songId = :id")
    fun positionInPlaylist(id: String, playlistId: Long): Int

    /**
     *  Whether the song is mapped to a playlist
     */
    @Query("""
        SELECT COUNT(s.id) > 0
        FROM Song s
        JOIN SongPlaylistMap spm ON spm.songId = s.id 
        WHERE s.id = :songId
    """)
    fun isSongMappedToPlaylist( songId: String ): Flow<Boolean>

    @Query("SELECT id FROM Playlist WHERE name = :playlistName")
    fun playlistExistByName(playlistName: String): Long

    @Query("UPDATE Playlist SET name = :playlistName WHERE id = :playlistId")
    fun updatePlaylistName(playlistName: String, playlistId: Long): Int

    @Transaction
    @Query("UPDATE Song SET title = :title WHERE id = :id")
    fun updateSongTitle(id: String, title: String): Int

    @Transaction
    @Query("UPDATE Song SET artistsText = :artist WHERE id = :id")
    fun updateSongArtist(id: String, artist: String): Int

    @Query("UPDATE Album SET thumbnailUrl = :thumb WHERE id = :id")
    fun updateAlbumCover(id: String, thumb: String): Int

    @Query("UPDATE Album SET authorsText = :artist WHERE id = :id")
    fun updateAlbumAuthors(id: String, artist: String): Int

    @Query("UPDATE Album SET title = :title WHERE id = :id")
    fun updateAlbumTitle(id: String, title: String): Int

    @Query("UPDATE Artist SET name = :name WHERE id = :id")
    fun updateArtistName(id: String, name: String): Int

    @Query("SELECT thumbnailUrl FROM Song WHERE id in (:idsList) ")
    fun getSongsListThumbnailUrls(idsList: List<String>): Flow<List<String?>>

    @Query("""
        SELECT SongArtistMap.artistId
        FROM SongArtistMap
        WHERE SongArtistMap.songId = :songId
    """)
    fun findArtistIdOfSong( songId: String ): Flow<String?>

    @Query("""
        SELECT SongAlbumMap.albumId
        FROM SongAlbumMap
        WHERE SongAlbumMap.songId = :songId
    """)
    fun findAlbumIdOfSong( songId: String ): Flow<String?>

    @Transaction
    //@Query("SELECT Playlist.*, (SELECT COUNT(*) FROM SongPlaylistMap WHERE playlistId = Playlist.id) as songCount " +
    //        "FROM Song JOIN SongPlaylistMap ON Song.id = SongPlaylistMap.songId " +
    //        "JOIN Event ON Song.id = Event.songId JOIN Playlist ON Playlist.id = SongPlaylistMap.playlistId " +
    //        "WHERE Event.timestamp BETWEEN :from AND :to GROUP BY Playlist.id ORDER BY Event.timestamp DESC LIMIT :limit")
    @Query("SELECT Playlist.*, (SELECT COUNT(*) FROM SongPlaylistMap WHERE playlistId = Playlist.id) as songCount " +
            "FROM Song JOIN SongPlaylistMap ON Song.id = SongPlaylistMap.songId " +
            "JOIN Event ON Song.id = Event.songId JOIN Playlist ON Playlist.id = SongPlaylistMap.playlistId " +
            "WHERE (:to - Event.timestamp) <= :from GROUP BY Playlist.id ORDER BY SUM(Event.playTime) DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun playlistsMostPlayedByPeriod(from: Long,to: Long, limit:Int): Flow<List<PlaylistPreview>>

    @Transaction
    //@Query("SELECT Album.* FROM Song JOIN SongAlbumMap ON Song.id = SongAlbumMap.songId " +
    //        "JOIN Event ON Song.id = Event.songId JOIN Album ON Album.id = SongAlbumMap.albumId " +
    //        "WHERE Event.timestamp BETWEEN :from AND :to GROUP BY Album.id ORDER BY Event.timestamp DESC LIMIT :limit")
    @Query("SELECT Album.* FROM Song JOIN SongAlbumMap ON Song.id = SongAlbumMap.songId " +
            "JOIN Event ON Song.id = Event.songId JOIN Album ON Album.id = SongAlbumMap.albumId " +
            "WHERE (:to - Event.timestamp) <= :from GROUP BY Album.id ORDER BY SUM(Event.playTime) DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun albumsMostPlayedByPeriod(from: Long,to: Long, limit:Int): Flow<List<Album>>

    @Transaction
    //@Query("SELECT Artist.* FROM Song JOIN SongArtistMap ON Song.id = SongArtistMap.songId " +
    //        "JOIN Event ON Song.id = Event.songId JOIN Artist ON Artist.id = SongArtistMap.artistId " +
    //        "WHERE Event.timestamp BETWEEN :from AND :to GROUP BY Artist.id ORDER BY Event.timestamp DESC LIMIT :limit")
    @Query("SELECT Artist.* FROM Song JOIN SongArtistMap ON Song.id = SongArtistMap.songId " +
            "JOIN Event ON Song.id = Event.songId JOIN Artist ON Artist.id = SongArtistMap.artistId " +
            "WHERE (:to - Event.timestamp) <= :from GROUP BY Artist.id ORDER BY SUM(Event.playTime) DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun artistsMostPlayedByPeriod(from: Long,to: Long, limit:Int): Flow<List<Artist>>

    @Transaction
    //@Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE timestamp " +
    //        "BETWEEN :from AND :to GROUP BY songId  ORDER BY timestamp DESC LIMIT :limit")
    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE " +
            "(:to - Event.timestamp) <= :from GROUP BY songId  ORDER BY SUM(playTime) DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun songsMostPlayedByPeriod(from: Long, to: Long, limit:Long = Long.MAX_VALUE): Flow<List<Song>>

    @Transaction
    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE " +
            "CAST(strftime('%m',timestamp / 1000,'unixepoch') AS INTEGER) = :month AND CAST(strftime('%Y',timestamp / 1000,'unixepoch') as INTEGER) = :year " +
            "GROUP BY songId  ORDER BY timestamp DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun songsMostPlayedByYearMonth(year: Long, month: Long, limit:Long = Long.MAX_VALUE): Flow<List<Song>>

    @Transaction
    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE " +
            "CAST(strftime('%m',timestamp / 1000,'unixepoch') AS INTEGER) = :month AND CAST(strftime('%Y',timestamp / 1000,'unixepoch') as INTEGER) = :year " +
            "GROUP BY songId  ORDER BY timestamp DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun songsMostPlayedByYearMonthNoFlow(year: Long, month: Long, limit:Long = Long.MAX_VALUE): List<Song>


    @Query("SELECT thumbnailUrl FROM Song WHERE likedAt IS NOT NULL AND id NOT LIKE '$LOCAL_KEY_PREFIX%'  LIMIT 4")
    fun preferitesThumbnailUrls(): Flow<List<String?>>

    @Transaction
    @Query("SELECT Song.*, contentLength FROM Song LEFT JOIN Format ON id = songId WHERE songId = :songId")
    fun songCached(songId: String): Flow<SongWithContentLength?>

    /**
     * Return a list of songs that were listened to by this user.
     *
     * Songs must be listened at least once within [period] time
     * to be included in the results. For example, `604,800,000` ms
     * (_1 week in millis_) indicates that a song must be listened to
     * once in the past week in order to be included in the results.
     *
     * @param period require song to be listened to once, represent in millis
     * @param limit final results must contain at most this many elements
     *
     * @return a list of [Song]s that were listened to withing the provided [period]
     */
    @Query("""
        SELECT DISTINCT Song.*
        FROM Song 
        JOIN Event ON Event.songId = Song.id 
        WHERE (strftime('%s', 'now') * 1000 - Event.timestamp) <= :period
        AND Song.title NOT LIKE 'e:%'
        GROUP BY Event.songId 
        ORDER BY SUM(Event.playTime) DESC
        LIMIT :limit
    """)
    fun mostListenedSongs(
        period: Long = Long.MAX_VALUE,
        limit: Long = 0L
    ): Flow<List<Song>>

//    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
//    @Transaction
//    @Query("SELECT Song.*, Album.title as albumTitle FROM Song JOIN SongAlbumMap ON Song.id = SongAlbumMap.songId  " +
//            "JOIN Album ON Album.id = SongAlbumMap.albumId " +
//            "WHERE (Song.totalPlayTimeMs > :showHiddenSongs OR Song.likedAt NOT NULL) AND Song.id NOT LIKE '$LOCAL_KEY_PREFIX%' ORDER BY Song.artistsText DESC")
//    @RewriteQueriesToDropUnusedColumns
//    fun songsWithAlbumByPlayTimeDesc(showHiddenSongs: Int = 0): Flow<List<SongEntity>>

    @Query("SELECT thumbnailUrl FROM Song JOIN Format ON id = songId WHERE contentLength IS NOT NULL AND totalPlayTimeMs > 0  LIMIT 4")
    fun offlineThumbnailUrls(): Flow<List<String?>>

    @Query("UPDATE Playlist SET name = '${PINNED_PREFIX}'||name WHERE id = :playlistId")
    fun pinPlaylist(playlistId: Long): Int
    @Query("UPDATE Playlist SET name = REPLACE(name,'${PINNED_PREFIX}','') WHERE id = :playlistId")
    fun unPinPlaylist(playlistId: Long): Int

    @Query("UPDATE Album SET bookmarkedAt = :bookmarkedAt WHERE id = :id")
    fun bookmarkAlbum(id: String, bookmarkedAt: Long?): Int

    @Query("""
        SELECT 
            CASE
                WHEN bookmarkedAt IS NOT NULL THEN 1   
                ELSE 0
            END
        FROM Album
        WHERE id = :albumId 
    """)
    fun isAlbumBookmarked( albumId: String ): Flow<Boolean>

    /**
     * There are 2 possible actions.
     *
     * ### If album IS bookmarked
     * This will remove [Album.bookmarkedAt] timestamp (replaces with NULL)
     *
     * ## If album IS NOT bookmarked
     * It will assign [Album.bookmarkedAt] with current time in millis
     *
     * @param albumId album identifier to update its [Album.bookmarkedAt]
     */
    @Query("""
        UPDATE Album
        SET bookmarkedAt = 
            CASE 
                WHEN bookmarkedAt IS NULL THEN strftime('%s', 'now') * 1000
                ELSE NULL
            END
        WHERE id = :albumId
    """)
    fun toggleAlbumBookmark( albumId: String )

    @Query("UPDATE Song SET durationText = :durationText WHERE id = :songId")
    fun updateDurationText(songId: String, durationText: String): Int

    @Query("SELECT * FROM Artist WHERE bookmarkedAt IS NOT NULL ORDER BY name")
    fun preferitesArtistsByName(): Flow<List<Artist>>

    @Query("""
        SELECT DISTINCT Song.*
        FROM Song
        JOIN SongArtistMap ON SongArtistMap.songId = Song.id
        WHERE SongArtistMap.artistId = :artistId
        ORDER BY SongArtistMap.ROWID DESC
        LIMIT :limit
    """)
    fun findLatestSongsOfArtist( artistId: String, limit: Long = 5L ): Flow<List<Song>>

    @Query("""
        UPDATE Artist
        SET bookmarkedAt = CASE
            WHEN bookmarkedAt IS NULL THEN strftime('%s', 'now') * 1000
            ELSE NULL
        END
        WHERE id = :artistId
    """)
    fun toggleArtistFollow( artistId: String )

    /**
     * Keeps track [Artist.bookmarkedAt] of provided artist.
     * When artist is not in database, returns `false`
     */
    @Query("""
        SELECT COALESCE(
            (
                SELECT 1 
                FROM Artist 
                WHERE id = :artistId 
                AND bookmarkedAt IS NOT NULL 
                LIMIT 1
            ),
            0
        )
    """)
    fun isArtistFollowed( artistId: String ): Flow<Boolean>

    @Query("SELECT timestamp FROM Album WHERE id = :id")
    fun albumTimestamp(id: String): Long?

    @Query("SELECT bookmarkedAt FROM Album WHERE id = :id")
    fun albumBookmarkedAt(id: String): Flow<Long?>

    @Query("SELECT count(id) FROM Album WHERE id = :id and bookmarkedAt IS NOT NULL")
    fun albumBookmarked(id: String): Int

    @Query("SELECT count(id) FROM Album WHERE id = :id")
    fun albumExist(id: String): Int

    @Transaction
    @Query("SELECT DISTINCT * FROM Song JOIN SongAlbumMap ON Song.id = SongAlbumMap.songId WHERE SongAlbumMap.albumId = :albumId ORDER BY position")
    @RewriteQueriesToDropUnusedColumns
    fun albumSongsList(albumId: String): List<Song>

    @Transaction
    @Query("SELECT DISTINCT * FROM Song JOIN SongAlbumMap ON Song.id = SongAlbumMap.songId WHERE SongAlbumMap.albumId = :albumId AND position IS NOT NULL ORDER BY position")
    @RewriteQueriesToDropUnusedColumns
    fun albumSongs(albumId: String): Flow<List<Song>>

    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT DISTINCT Song.*
        FROM Album 
        LEFT JOIN SongAlbumMap ON SongAlbummap.albumId = Album.id
        LEFT JOIN Song ON Song.id = SongAlbumMap.songId 
        WHERE Album.id = :albumId
        ORDER BY SongAlbumMap.position
    """)
    fun findSongsOfAlbum( albumId: String ): Flow<List<Song>>

    @Query("UPDATE Song SET totalPlayTimeMs = 0 WHERE id = :id")
    fun resetTotalPlayTimeMs(id: String)

    @Query("UPDATE Song SET totalPlayTimeMs = totalPlayTimeMs + :addition WHERE id = :id")
    fun incrementTotalPlayTimeMs(id: String, addition: Long)

    @Transaction
    @Query("SELECT max(position) maxPos FROM SongPlaylistMap WHERE playlistId = :id")
    fun getSongMaxPositionToPlaylist(id: Long): Int

    @Transaction
    @Query("SELECT PM.playlistId FROM SongPlaylistMap PM WHERE PM.songId = :id")
    fun getPlaylistsWithSong(id: String): Flow<List<Long>>

    @Transaction
    @Query("SELECT max(position) maxPos FROM SongPlaylistMap WHERE playlistId = :id")
    fun updateSongMaxPositionToPlaylist(id: Long): Int

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM SongPlaylistMap WHERE playlistId = id) as songCount FROM Playlist WHERE name LIKE '${MONTHLY_PREFIX}' || :name || '%'  ")
    fun monthlyPlaylistsPreview(name: String?): Flow<List<PlaylistPreview>>


    @Transaction
    @Query("SELECT DISTINCT S.* FROM Song S INNER JOIN SongAlbumMap SM ON S.id=SM.songId " +
            "INNER JOIN Album A ON A.id=SM.albumId WHERE A.bookmarkedAt IS NOT NULL")
    fun songsInAllBookmarkedAlbums(): Flow<List<Song>>

    @Transaction
    @Query("SELECT DISTINCT S.* FROM Song S INNER JOIN SongArtistMap SM ON S.id=SM.songId " +
            "INNER JOIN Artist A ON A.id=SM.artistId WHERE A.bookmarkedAt IS NOT NULL")
    fun songsInAllFollowedArtists(): Flow<List<Song>>

    @Transaction
    @Query("SELECT DISTINCT S.* FROM Song S INNER JOIN songplaylistmap SM ON S.id=SM.songId")
    fun songsInAllPlaylists(): Flow<List<Song>>

    @Transaction
    @Query("SELECT DISTINCT S.* FROM Song S INNER JOIN songplaylistmap SM ON S.id=SM.songId " +
            "INNER JOIN Playlist P ON P.id=SM.playlistId WHERE P.isYoutubePlaylist = 1")
    fun songsInAllYTPrivatePlaylists(): Flow<List<Song>>

    @Transaction
    @Query("SELECT DISTINCT S.* FROM Song S INNER JOIN songplaylistmap SM ON S.id=SM.songId " +
            "INNER JOIN Playlist P ON P.id=SM.playlistId WHERE P.name LIKE '${PIPED_PREFIX}' || '%'")
    fun songsInAllPipedPlaylists(): Flow<List<Song>>

    @Transaction
    @Query("SELECT DISTINCT S.* FROM Song S INNER JOIN songplaylistmap SM ON S.id=SM.songId " +
            "INNER JOIN Playlist P ON P.id=SM.playlistId WHERE P.name LIKE '${PINNED_PREFIX}' || '%'")
    fun songsInAllPinnedPlaylists(): Flow<List<Song>>

    @Transaction
    @Query("SELECT DISTINCT S.* FROM Song S INNER JOIN songplaylistmap SM ON S.id=SM.songId " +
            "INNER JOIN Playlist P ON P.id=SM.playlistId WHERE P.name LIKE '${MONTHLY_PREFIX}' || '%'")
    fun songsInAllMonthlyPlaylists(): Flow<List<Song>>


    @Transaction
    @Query("SELECT S.* FROM Song S INNER JOIN songplaylistmap SP ON S.id=SP.songId WHERE SP.playlistId=:id ORDER BY SP.position LIMIT 4")
    fun songsPlaylistTop4Positions(id: Long): Flow<List<Song>>

    @Transaction
    @Query("SELECT SP.position FROM Song S INNER JOIN songplaylistmap SP ON S.id=SP.songId WHERE SP.playlistId=:id AND S.id NOT LIKE '$LOCAL_KEY_PREFIX%' ORDER BY SP.position")
    fun songsPlaylistMap(id: Long): Flow<List<Int>>

    @Transaction
    @Query("SELECT id FROM SONG WHERE likedAt IS NOT NULL AND likedAt < 0")
    fun dislikedSongsById(): Flow<List<String>>

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM SongPlaylistMap WHERE playlistId = id) as songCount FROM Playlist WHERE id=:id")
    fun singlePlaylistPreview(id: Long): Flow<PlaylistPreview?>

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM SongPlaylistMap WHERE playlistId = id) as songCount FROM Playlist WHERE name LIKE '${PINNED_PREFIX}%' ORDER BY name COLLATE NOCASE ASC")
    fun playlistPinnedPreviewsByNameAsc(): Flow<List<PlaylistPreview>>

    @Query("SELECT thumbnailUrl FROM Song JOIN SongPlaylistMap ON id = songId WHERE playlistId = :id AND thumbnailUrl <>'' ORDER BY position LIMIT 4")
    fun playlistThumbnailUrls(id: Long): Flow<List<String?>>

    @Query("SELECT contentLength FROM Format WHERE songId = :songId")
    fun formatContentLength(songId: String): Long

    @Transaction
    @Query("UPDATE Format SET contentLength = 0 WHERE songId = :songId")
    fun resetFormatContentLength(songId: String)

    @Transaction
    @Query("DELETE FROM Song WHERE totalPlayTimeMs = 0")
    fun deleteHiddenSongs()
    // USEFUL FOR MAINTENANCE
    @Transaction
    @Query("DELETE FROM SongArtistMap WHERE songId NOT IN (SELECT DISTINCT id FROM Song)")
    fun cleanSongArtistMap()
    @Transaction
    @Query("DELETE FROM SongPlaylistMap WHERE songId NOT IN (SELECT DISTINCT id FROM Song)")
    fun cleanSongPlaylistMap()
    @Transaction
    @Query("DELETE FROM SongAlbumMap WHERE songId NOT IN (SELECT DISTINCT id FROM Song)")
    fun cleanSongAlbumMap()
    @Transaction
    @Query("DELETE FROM Format WHERE songId = :songId")
    fun deleteFormat(songId: String)

    @Transaction
    @Query("SELECT Song.*, contentLength FROM Song JOIN Format ON id = songId WHERE contentLength IS NOT NULL AND totalPlayTimeMs > 0 ORDER BY Song.ROWID DESC")
    fun songsWithContentLength(): Flow<List<SongWithContentLength>>

    @Transaction
    @Query("""
        UPDATE SongPlaylistMap SET position = 
          CASE 
            WHEN position < :fromPosition THEN position + 1
            WHEN position > :fromPosition THEN position - 1
            ELSE :toPosition
          END 
        WHERE playlistId = :playlistId AND position BETWEEN MIN(:fromPosition,:toPosition) and MAX(:fromPosition,:toPosition)
    """)
    fun move(playlistId: Long, fromPosition: Int, toPosition: Int)

    @Transaction
    @Query("UPDATE SongPlaylistMap SET position = :toPosition WHERE playlistId = :playlistId and songId = :songId")
    fun updateSongPosition(playlistId: Long, songId: String, toPosition: Int)

    @Query("DELETE FROM SongPlaylistMap WHERE playlistId = :id")
    fun clearPlaylist(id: Long)

    @Query("DELETE FROM SongPlaylistMap WHERE songId = :id")
    fun deleteSongFromPlaylists(id: String)

    @Query("DELETE FROM SongPlaylistMap WHERE songId = :id and playlistId = :playlistId")
    fun deleteSongFromPlaylist(id: String, playlistId: Long)

    @Query("SELECT setVideoId FROM SongPlaylistMap WHERE songId = :id and playlistId = :playlistId")
    fun getSetVideoIdFromPlaylist(id: String, playlistId: Long): Flow<String?>

    @Query("DELETE FROM SongAlbumMap WHERE albumId = :id")
    fun clearAlbum(id: String)

    @Query("SELECT loudnessDb FROM Format WHERE songId = :songId")
    fun loudnessDb(songId: String): Flow<Float?>

    @Query("SELECT albumId AS id, Album.title AS name, 0 AS size FROM SongAlbumMap LEFT JOIN Album ON id=albumId WHERE songId = :songId")
    fun songAlbumInfo(songId: String): Info?

    @Query("SELECT thumbnailUrl FROM Song LEFT JOIN SongAlbumMap ON id=songId WHERE albumId = :albumId")
    fun albumThumbnailFromSong(albumId: String): String?

    @Query("SELECT id, name, 0 AS size FROM Artist LEFT JOIN SongArtistMap ON id = artistId WHERE songId = :songId")
    fun songArtistInfo(songId: String): List<Info>

    /*
        @Transaction
        @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId GROUP BY songId ORDER BY SUM(CAST(playTime AS REAL) / (((:now - timestamp) / 86400000) + 1)) DESC LIMIT 1")
    //    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId GROUP BY songId ORDER BY timestamp DESC LIMIT 1")
        @RewriteQueriesToDropUnusedColumns
        fun trending(now: Long = System.currentTimeMillis()): Flow<Song?>
    //    fun trending(): Flow<Song?>
     */

    @Transaction
    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE Song.id NOT LIKE '$LOCAL_KEY_PREFIX%' GROUP BY songId ORDER BY SUM(CAST(playTime AS REAL) / (((:now - timestamp) / 86400000) + 1)) DESC LIMIT 1")
    @RewriteQueriesToDropUnusedColumns
    fun trendingReal(now: Long = System.currentTimeMillis()): Flow<List<Song>>

    @Transaction
    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE Song.id NOT LIKE '$LOCAL_KEY_PREFIX%' GROUP BY songId ORDER BY SUM(playTime) DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun trending(limit: Int = 3): Flow<List<Song>>

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Transaction
    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE Song.id NOT LIKE '$LOCAL_KEY_PREFIX%' GROUP BY songId ORDER BY SUM(playTime) DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun trendingSongEntity(limit: Int = 3): Flow<List<SongEntity>>

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Transaction
    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE (:now - Event.timestamp) <= :period AND Song.id NOT LIKE '$LOCAL_KEY_PREFIX%' GROUP BY songId ORDER BY SUM(playTime) DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun trendingSongEntity(
        limit: Int = 3,
        now: Long = System.currentTimeMillis(),
        period: Long
    ): Flow<List<SongEntity>>

    @Transaction
    @Query("SELECT Song.* FROM Event JOIN Song ON Song.id = songId WHERE playTime > 0 and Song.id NOT LIKE '$LOCAL_KEY_PREFIX%' GROUP BY songId ORDER BY timestamp DESC LIMIT :limit")
    @RewriteQueriesToDropUnusedColumns
    fun lastPlayed( limit: Int = 10 ): Flow<List<Song>>

    /**
     * Insert provided song into indicated playlist
     * at the next available position.
     *
     * @param songId     song to add
     * @param playlistId playlist to add song into
     */
    @Query("""
        INSERT INTO SongPlaylistMap ( songId, playlistId, position )
        VALUES( 
            :songId,
            :playlistId,
            COALESCE(
                (
                    SELECT MAX(position) + 1 
                    FROM SongPlaylistMap 
                    WHERE playlistId = :playlistId
                ), 
                0
            )
        )
    """)
    fun insertSongToPlaylist( songId: String, playlistId: Long )

    @Transaction
    fun insert(mediaItem: MediaItem, block: (Song) -> Song = { it }) {
        var title = mediaItem.mediaMetadata.title!!.toString()
        if(!title.startsWith(EXPLICIT_PREFIX, true) && mediaItem.isExplicit){
            title = EXPLICIT_PREFIX + title
        }
        val song = Song(
            id = mediaItem.mediaId,
            title = title,
            artistsText = mediaItem.mediaMetadata.artist?.toString(),
            durationText = mediaItem.mediaMetadata.extras?.getString("durationText"),
            thumbnailUrl = mediaItem.mediaMetadata.artworkUri?.toString()
        ).let(block).also { song ->
            if (songTable.insertIgnore( song ) == -1L) return
        }

        mediaItem.mediaMetadata.extras?.getString("albumId")?.let { albumId ->
            albumTable.insertIgnore(
                Album(
                    id = albumId,
                    title = mediaItem.mediaMetadata.albumTitle?.toString()
                )
            )
            songAlbumMapTable.insertIgnore(
                SongAlbumMap( song.id, albumId, null )
            )
        }

        val artistsNames = mediaItem.mediaMetadata.extras?.getStringArrayList("artistNames")
        val artistsIds = mediaItem.mediaMetadata.extras?.getStringArrayList("artistIds")

        if ( artistsNames != null && artistsIds != null ) {
            artistsNames.fastZip( artistsIds ) { artistName, artistId ->
                if( artistId == null ) return@fastZip

                artistTable.insertIgnore(
                    Artist(id = artistId, name = artistName)
                )
                songArtistMapTable.insertIgnore(
                    SongArtistMap(song.id, artistId)
                )
            }
        }
    }

    /**
     * Reset [Format.contentLength] of provided song.
     *
     * This method is already wrapped by [Transaction] call,
     * therefore, it's unnecessary to wrap it with a coroutine
     * or other transaction call.
     *
     * To use it inside another [Transaction] wrapper, please
     * refer to [resetFormatContentLength].
     *
     * @param songId id of song to have its [Format.contentLength] reset
     */
    @Transaction
    fun resetContentLength( songId: String ) = asyncTransaction {
        resetFormatContentLength( songId )
    }

    /**
     * Commit statements in BULK. If anything goes wrong during the transaction,
     * other statements will be cancelled and reversed to preserve database's integrity.
     * [Read more](https://sqlite.org/lang_transaction.html)
     *
     * [asyncTransaction] runs all statements on non-blocking
     * thread to prevent UI from going unresponsive.
     *
     * ## Best use cases:
     * - Commit multiple write statements that require data integrity
     * - Processes that take longer time to complete
     *
     * > Do NOT use this to retrieve data from the database.
     * > Use [asyncQuery] to retrieve records.
     *
     * @param block of statements to write to database
     */
    fun asyncTransaction( block: Database.() -> Unit ) =
        _internal.transactionExecutor.execute {
            this.block()
        }


    /**
     * Access and retrieve records from database.
     *
     * [asyncQuery] runs all statements asynchronously to
     * prevent blocking UI thread from going unresponsive.
     *
     * ## Best use cases:
     * - Background data retrieval
     * - Non-immediate UI component update (i.e. count number of songs)
     *
     * > Do NOT use this method to write data to database
     * > because it offers no fail-safe during write.
     * > Use [asyncTransaction] to modify database.
     *
     * @param block of statements to retrieve data from database
     */
    fun asyncQuery( block: Database.() -> Unit ) =
        _internal.queryExecutor.execute {
            this.block()
        }

    @RawQuery
    fun raw(supportSQLiteQuery: SupportSQLiteQuery): Int

    fun checkpoint() {
        raw(SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)"))
    }

    fun path() = _internal.openHelper.writableDatabase.path

    fun close() = _internal.close()
}

@androidx.room.Database(
    entities = [
        Song::class,
        SongPlaylistMap::class,
        Playlist::class,
        Artist::class,
        SongArtistMap::class,
        Album::class,
        SongAlbumMap::class,
        SearchQuery::class,
        QueuedMediaItem::class,
        Format::class,
        Event::class,
        Lyrics::class,
    ],
    views = [
        SortedSongPlaylistMap::class
    ],
    version = 27,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = From3To4Migration::class),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8, spec = From7To8Migration::class),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 11, to = 12, spec = From11To12Migration::class),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
        AutoMigration(from = 15, to = 16),
        AutoMigration(from = 16, to = 17),
        AutoMigration(from = 17, to = 18),
        AutoMigration(from = 18, to = 19),
        AutoMigration(from = 19, to = 20),
        AutoMigration(from = 20, to = 21, spec = From20To21Migration::class),
        AutoMigration(from = 21, to = 22, spec = From21To22Migration::class),
    ],
)
@TypeConverters(Converters::class)
abstract class DatabaseInitializer protected constructor() : RoomDatabase() {
    abstract val database: Database
    abstract val albumTable: AlbumTable
    abstract val artistTable: ArtistTable
    abstract val eventTable: EventTable
    abstract val formatTable: FormatTable
    abstract val lyricsTable: LyricsTable
    abstract val playlistTable: PlaylistTable
    abstract val queueTable: QueuedMediaItemTable
    abstract val searchQueryTable: SearchQueryTable
    abstract val songAlbumMapTable: SongAlbumMapTable
    abstract val songArtistMapTable: SongArtistMapTable
    abstract val songPlaylistMapTable: SongPlaylistMapTable
    abstract val songTable: SongTable

    companion object {

        lateinit var Instance: DatabaseInitializer

        private fun getDatabase() = Room
            .databaseBuilder(appContext(), DatabaseInitializer::class.java, "data.db")
            .addMigrations(
                From8To9Migration(),
                From10To11Migration(),
                From14To15Migration(),
                From22To23Migration(),
                From23To24Migration(),
                From24To25Migration(),
                From25To26Migration(),
                From26To27Migration()
            )
            .build()


        operator fun invoke() {
            if (!::Instance.isInitialized) reload()
        }

        fun reload() = synchronized(this) {
            Instance = getDatabase()
        }
    }
}
