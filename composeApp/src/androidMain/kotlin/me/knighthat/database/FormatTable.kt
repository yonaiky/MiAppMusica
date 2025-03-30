package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import it.fast4x.rimusic.models.Format
import kotlinx.coroutines.flow.Flow
import me.knighthat.database.ext.FormatWithSong

@Dao
@RewriteQueriesToDropUnusedColumns
interface FormatTable: SqlTable<Format> {

    @Transaction
    @Query("SELECT DISTINCT * FROM Format LIMIT :limit")
    fun allWithSongs( limit: Int = Int.MAX_VALUE ): Flow<List<FormatWithSong>>

    /**
     * Song with [songId] will have its [Format] removed
     *
     * @return number of rows affected by this operation
     */
    @Query("DELETE FROM Format WHERE songId = :songId")
    fun deleteBySongId( songId: String ): Int

    /**
     * @param songId of song to look for
     * @return [Format] that has [Format.songId] matches [songId]
     */
    @Query("SELECT DISTINCT * FROM Format WHERE songId = :songId")
    fun findBySongId( songId: String ): Flow<Format?>

    /**
     * @return stored [Format.contentLength] of song with id [songId], `0` otherwise
     */
    @Query("""
        SELECT COALESCE(
            (
                SELECT contentLength
                FROM Format
                WHERE songId = :songId
            ),
            0
        )
    """)
    fun findContentLengthOf( songId: String ): Flow<Long>

    /**
     * Set [Format.contentLength] of song with id [songId] to [contentLength]
     *
     * @return number of rows affected by this operation
     */
    @Query("UPDATE Format SET contentLength = :contentLength WHERE songId = :songId")
    fun updateContentLengthOf( songId: String, contentLength: Long = 0L ): Int
}