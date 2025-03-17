package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Event

@Dao
@RewriteQueriesToDropUnusedColumns
interface EventTable: SqlTable<Event>