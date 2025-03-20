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

    override val tableName: String
        get() = "SongAlbumMap"

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
    fun allSongsOf( albumId: String, limit: Long = Long.MAX_VALUE ): Flow<List<Song>>
}