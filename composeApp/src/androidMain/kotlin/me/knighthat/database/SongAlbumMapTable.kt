package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongAlbumMap
import kotlinx.coroutines.flow.Flow

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongAlbumMapTable: SqlTable<SongAlbumMap> {

    /**
     * Remove all songs belong to album with id [albumId]
     *
     * @param albumId album to have its songs wiped
     *
     * @return number of rows affected by this operation
     */
    @Query("DELETE FROM SongAlbumMap WHERE albumId = :albumId")
    fun clear( albumId: String ): Int

    /**
     * Delete all mappings where songs aren't exist in `Song` table
     *
     * @return number of rows affected by this operation
     */
    @Query("""
        DELETE FROM SongAlbumMap 
        WHERE songId NOT IN (
            SELECT DISTINCT id
            FROM Song
        )
    """)
    fun clearGhostMaps(): Int

    /**
     * Results are sorted by [SongAlbumMap.position].
     *
     * @param albumId of artist to look for
     * @param limit number of results cannot go over this value
     *
     * @return all [Song]s that were mapped to album has [Album.id] matches [albumId],
     * sorted by song's position in album
     */
    @Query("""
        SELECT DISTINCT Song.*
        FROM SongAlbumMap
        JOIN Song ON id = songId
        WHERE albumId = :albumId
        ORDER BY position
        LIMIT :limit
    """)
    fun allSongsOf( albumId: String, limit: Int = Int.MAX_VALUE ): Flow<List<Song>>
}