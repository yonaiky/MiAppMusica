package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.QueuedMediaItem

@Dao
@RewriteQueriesToDropUnusedColumns
interface QueuedMediaItemTable: SqlTable<QueuedMediaItem>