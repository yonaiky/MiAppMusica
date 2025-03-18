package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Playlist

@Dao
@RewriteQueriesToDropUnusedColumns
interface PlaylistTable: SqlTable<Playlist> {

    override val tableName: String
        get() = "Playlist"
}