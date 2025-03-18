package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.SongArtistMap

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongArtistMapTable: SqlTable<SongArtistMap> {

    override val tableName: String
        get() = "SongArtistMap"
}