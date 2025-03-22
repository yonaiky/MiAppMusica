package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Artist
import it.fast4x.rimusic.models.Event
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.models.Song
import kotlinx.coroutines.flow.Flow
import me.knighthat.database.ext.EventWithSong

@Dao
@RewriteQueriesToDropUnusedColumns
interface EventTable: SqlTable<Event> {

    override val tableName: String
        get() = "Event"

    @Query("SELECT DISTINCT * FROM Event LIMIT :limit")
    fun allWithSong( limit: Int = Int.MAX_VALUE ): Flow<List<EventWithSong>>

    /**
     * Delete any record if its [Event.songId]
     * equals to provided [song]'s id.
     *
     * @return number of rows deleted
     */
    fun delete( song: Song ) = delete( "WHERE songId = ${song.id}" )

    /**
     * Return a list of songs that were listened to by user.
     *
     * Songs must be listened at least once within [from] and [to]
     * be included in the results.
     *
     * Results are sorted from most listened to least listened to.
     *
     * By default, only [from] is required, [to] is set to current time.
     * Meaning fetch all from [from] to present.
     *
     * @param from beginning of period to query in epoch millis format
     * @param to the end of period to query in epoch millis format
     * @param limit trim result to have maximum size of this value
     *
     * @return [Song]s that were listened to at least once in period in descending order
     */
    @Query("""
        SELECT DISTINCT S.*
        FROM Event E
        JOIN Song S ON S.id = E.songId
        WHERE E."timestamp" BETWEEN :from and :to
        ORDER BY S.totalPlaytimeMs DESC
        LIMIT :limit
    """)
    fun findSongsMostPlayedBetween(
        from: Long,
        to: Long = System.currentTimeMillis(),
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Song>>

    /**
     * Return a list of artists that have their songs listened to by user.
     *
     * Songs must be listened at least once within [from] and [to]
     * be included in the results.
     *
     * Results are sorted by total [Song.totalPlayTimeMs] in descending order.
     *
     * By default, only [from] is required, [to] is set to current time.
     * Meaning fetch all from [from] to present.
     *
     * @param from beginning of period to query in epoch millis format
     * @param to the end of period to query in epoch millis format
     * @param limit trim result to have maximum size of this value
     *
     * @return [Artist]s that have their songs listened to at least once in period in descending order
     */
    @Query("""
        SELECT DISTINCT A.*
        FROM Artist A
        JOIN songartistmap sam ON sam.artistId = A.id
        JOIN Event E ON E.songId = sam.songId
        JOIN Song S ON S.id = sam.songId
        WHERE E."timestamp" BETWEEN :from and :to
        GROUP BY A.id
        ORDER BY SUM(S.totalPlaytimeMs) DESC
        LIMIT :limit
    """)
    fun findArtistsMostPlayedBetween(
        from: Long,
        to: Long = System.currentTimeMillis(),
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Artist>>

    /**
     * Return a list of albums that have their songs listened to by user.
     *
     * Songs must be listened at least once within [from] and [to]
     * be included in the results.
     *
     * Results are sorted by total [Song.totalPlayTimeMs] in descending order.
     *
     * By default, only [from] is required, [to] is set to current time.
     * Meaning fetch all from [from] to present.
     *
     * @param from beginning of period to query in epoch millis format
     * @param to the end of period to query in epoch millis format
     * @param limit trim result to have maximum size of this value
     *
     * @return [Album]s that have their songs listened to at least once in period in descending order
     */
    @Query("""
        SELECT DISTINCT A.*
        FROM Album A
        JOIN SongAlbumMap sam ON sam.albumId = A.id
        JOIN Event E ON E.songId = sam.songId
        JOIN Song S ON S.id = sam.songId
        WHERE E."timestamp" BETWEEN :from and :to
        GROUP BY A.id
        ORDER BY SUM(S.totalPlaytimeMs) DESC
        LIMIT :limit
    """)
    fun findAlbumsMostPlayedBetween(
        from: Long,
        to: Long = System.currentTimeMillis(),
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Album>>

    /**
     * Return a list of playlists that have their songs were listened to by user.
     *
     * Songs must be listened at least once within [from] and [to]
     * be included in the results.
     *
     * Results are converted into [PlaylistPreview] and
     * sorted by total [Song.totalPlayTimeMs] in descending order.
     *
     * By default, only [from] is required, [to] is set to current time.
     * Meaning fetch all from [from] to present.
     *
     * @param from beginning of period to query in epoch millis format
     * @param to the end of period to query in epoch millis format
     * @param limit trim result to have maximum size of this value
     *
     * @return [PlaylistPreview] that their songs were listened to at least once in period in descending order
     */
    @Query("""
        SELECT DISTINCT P.*, COUNT(spm.songId) AS songCount
        FROM Playlist P
        JOIN SongPlaylistMap spm ON spm.playlistId = P.id
        JOIN Event E ON E.songId = spm.songId
        JOIN Song S ON S.id = spm.songId
        WHERE E."timestamp" BETWEEN :from and :to
        GROUP BY P.id
        ORDER BY SUM(S.totalPlaytimeMs) DESC
        LIMIT :limit
    """)
    fun findPlaylistMostPlayedBetweenAsPreview(
        from: Long,
        to: Long = System.currentTimeMillis(),
        limit: Long = Long.MAX_VALUE
    ): Flow<List<PlaylistPreview>>
}