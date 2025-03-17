package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Format

@Dao
@RewriteQueriesToDropUnusedColumns
interface FormatTable: SqlTable<Format>