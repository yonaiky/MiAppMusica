package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.SongPlaylistMap

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongPlaylistMapTable: SqlTable<SongPlaylistMap>