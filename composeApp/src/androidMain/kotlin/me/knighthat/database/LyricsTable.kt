package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Lyrics

@Dao
@RewriteQueriesToDropUnusedColumns
interface LyricsTable: SqlTable<Lyrics> {

    override val tableName: String
        get() = "Lyrics"
}