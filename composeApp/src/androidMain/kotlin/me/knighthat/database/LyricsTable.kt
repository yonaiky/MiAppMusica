package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Lyrics
import kotlinx.coroutines.flow.Flow

@Dao
@RewriteQueriesToDropUnusedColumns
interface LyricsTable: SqlTable<Lyrics> {

    /**
     * @param songId of song to look for
     * @return [Lyrics] that has [Lyrics.songId] matches [songId]
     */
    @Query("SELECT DISTINCT * FROM Lyrics WHERE songId = :songId")
    fun findBySongId( songId: String ): Flow<Lyrics?>
}