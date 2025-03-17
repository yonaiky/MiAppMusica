package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Artist

@Dao
@RewriteQueriesToDropUnusedColumns
interface ArtistTable: SqlTable<Artist>