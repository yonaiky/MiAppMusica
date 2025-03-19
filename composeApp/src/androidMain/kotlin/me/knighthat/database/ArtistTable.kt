package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Artist
import kotlinx.coroutines.flow.Flow

@Dao
@RewriteQueriesToDropUnusedColumns
interface ArtistTable: SqlTable<Artist> {

    override val tableName: String
        get() = "Artist"

    /**
     * @return all records from this table
     */
    @Query("SELECT DISTINCT * FROM Artist")
    fun all(): Flow<List<Artist>>

    /**
     * @param artistId of artist to look for
     * @return [Artist] that has [Artist.id] matches [artistId]
     */
    @Query("SELECT DISTINCT * FROM Artist WHERE id = :artistId")
    fun findById( artistId: String ): Flow<Artist?>
}