package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Album

@Dao
@RewriteQueriesToDropUnusedColumns
interface AlbumTable: SqlTable<Album> {

    override val tableName: String
        get() = "Album"
}