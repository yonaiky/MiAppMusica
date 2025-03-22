package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Artist
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongArtistMap
import kotlinx.coroutines.flow.Flow

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongArtistMapTable: SqlTable<SongArtistMap> {

    override val tableName: String
        get() = "SongArtistMap"

    /**
     * Delete all mappings where songs aren't exist in `Song` table
     *
     * @return number of rows affected by this operation
     */
    fun clearGhostMaps() = delete( "songId NOT IN (SELECT DISTINCT id FROM Song)" )

    /**
     * @param artistId of artist to look for
     * @param limit number of results cannot go over this value
     *
     * @return all [Song]s that were mapped to artist has [Artist.id] matches [artistId]
     */
    @Query("""
        SELECT DISTINCT Song.*
        FROM SongArtistMap sam 
        JOIN Song ON Song.id = sam.songId
        WHERE sam.artistId = :artistId
        ORDER BY Song.ROWID
        LIMIT :limit
    """)
    fun allSongsBy( artistId: String, limit: Long = Long.MAX_VALUE ): Flow<List<Song>>
}