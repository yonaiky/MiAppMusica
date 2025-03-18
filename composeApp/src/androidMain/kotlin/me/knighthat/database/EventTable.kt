package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Event
import it.fast4x.rimusic.models.Song

@Dao
@RewriteQueriesToDropUnusedColumns
interface EventTable: SqlTable<Event> {

    override val tableName: String
        get() = "Event"

    /**
     * Delete any record if its [Event.songId]
     * equals to provided [song]'s id.
     *
     * @return number of rows deleted
     */
    fun delete( song: Song ) = delete( "WHERE songId = ${song.id}" )
}