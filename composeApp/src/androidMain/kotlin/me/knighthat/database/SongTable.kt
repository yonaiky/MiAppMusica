package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Song

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongTable: SqlTable<Song> {

    override val tableName: String
        get() = "Song"
}