package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.SongAlbumMap

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongAlbumMapTable: SqlTable<SongAlbumMap> {

    override val tableName: String
        get() = "SongAlbumMap"
}