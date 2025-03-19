package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.QueuedMediaItem
import kotlinx.coroutines.flow.Flow

@Dao
@RewriteQueriesToDropUnusedColumns
interface QueuedMediaItemTable: SqlTable<QueuedMediaItem> {

    override val tableName: String
        get() = "QueuedMediaItem"

    /**
     * @return all records from this table
     */
    @Query("SELECT DISTINCT * FROM QueuedMediaItem")
    fun all(): Flow<List<QueuedMediaItem>>
}