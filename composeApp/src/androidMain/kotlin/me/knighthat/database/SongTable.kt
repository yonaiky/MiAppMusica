package me.knighthat.database

import android.database.SQLException
import androidx.media3.common.MediaItem
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Song
import kotlinx.coroutines.flow.Flow

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongTable: SqlTable<Song> {

    override val tableName: String
        get() = "Song"

    /**
     * Convert [MediaItem] into [Song] and attempt to it into database.
     *
     * ### Standalone use
     *
     * When error occurs and [SQLException] is thrown,
     * it'll simply be ignored.
     *
     * ### Transaction use
     *
     * When error occurs and [SQLException] is thrown,
     * it'll simply be ignored and the transaction continues.
     *
     * @param mediaItem intended to insert in to database
     * @return ROWID of this new record, -1 if error occurs
     */
    fun insertIgnore( mediaItem: MediaItem ): Long = insertIgnore( mediaItem.asSong )

    /**
     * @return whether any record in [Song] table has id [songId]
     */
    @Query("SELECT COUNT(*) > 0 FROM Song WHERE id = :songId")
    fun exists( songId: String ): Flow<Boolean>

    /**
     * Should not be called when you have a hold of [Song].
     *
     * The return value is **null safe**. When [Song.id] is
     * not found in database, it returns `false`
     *
     * @param songId of song to query
     * @return whether [Song.likedAt] is set
     */
    @Query("""
        SELECT COALESCE(
            (
                SELECT likedAt IS NOT NULL AND likedAt > 0
                FROM Song 
                WHERE id = :songId
            )
            , 0
        ) 
    """)
    fun isLiked( songId: String ): Flow<Boolean>

    /**
     * ### If song **IS NOT** liked
     *
     * Set [Song.likedAt] to current time
     *
     * ### If song **IS** liked
     *
     * Set [Song.likedAt] to `null`
     *
     * @return number of rows affected
     */
    @Query("""
        UPDATE Song
        SET likedAt = 
            CASE 
                WHEN likedAt IS NULL THEN strftime('%s', 'now') * 1000
                ELSE NULL
            END
        WHERE id = :songId
    """)
    fun toggleLike( songId: String ): Int
}