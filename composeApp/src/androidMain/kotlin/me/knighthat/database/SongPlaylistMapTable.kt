package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongPlaylistMap
import kotlinx.coroutines.flow.Flow

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongPlaylistMapTable: SqlTable<SongPlaylistMap> {

    override val tableName: String
        get() = "SongPlaylistMap"

    /**
     * @param playlistId of playlist to look for
     * @return all [Song]s that were mapped to playlist has [Playlist.id] matches [playlistId]
     */
    @Query("""
        SELECT DISTINCT Song.*
        FROM SongPlaylistMap smp
        JOIN Song ON Song.id = smp.songId
        WHERE smp.playlistId = :playlistId
    """)
    fun findAllSongsOf( playlistId: Long ): Flow<List<Song>>
}