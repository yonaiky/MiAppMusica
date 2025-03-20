package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.enums.PlaylistSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.Artist
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.PlaylistPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
@RewriteQueriesToDropUnusedColumns
interface PlaylistTable: SqlTable<Playlist> {

    override val tableName: String
        get() = "Playlist"

    /**
     * @return all playlists from this table with number of songs they carry
     */
    @Query("""
        SELECT DISTINCT P.*, COUNT(spm.songId) as songCount
        FROM SongPlaylistMap spm
        JOIN Playlist P ON P.id = spm.playlistId
        GROUP BY P.id
        ORDER BY P.ROWID
        LIMIT :limit
    """)
    fun allAsPreview( limit: Long = Long.MAX_VALUE ): Flow<List<PlaylistPreview>>

    /**
     * @param browseId of playlist to look for
     * @return [Playlist] that has [Playlist.browseId] matches [browseId]
     */
    @Query("SELECT DISTINCT * FROM Playlist WHERE browseId = :browseId")
    fun findByBrowseId( browseId: String ): Flow<Playlist?>

    /**
     * @return [Playlist] that has [Playlist.name] equals to [playlistName], case-insensitive
     */
    @Query("""
        SELECT DISTINCT * 
        FROM Playlist 
        WHERE trim(name) COLLATE NOCASE = trim(:playlistName) COLLATE NOCASE
        LIMIT 1
    """)
    fun findByName( playlistName: String ): Flow<Playlist?>

    //<editor-fold defaultstate="collapsed" desc="Sort as preview">
    @Query("""
        SELECT DISTINCT P.*, COUNT(spm.songId) as songCount
        FROM SongPlaylistMap spm
        JOIN Playlist P ON P.id = spm.playlistId
        JOIN Song S ON S.id = spm.songId
        GROUP BY P.id
        ORDER BY SUM(S.totalPlayTimeMs)
        LIMIT :limit
    """)
    fun sortPreviewsByMostPlayed( limit: Long = Long.MAX_VALUE ): Flow<List<PlaylistPreview>>

    fun sortPreviewsByName( limit: Long = Long.MAX_VALUE ): Flow<List<PlaylistPreview>> =
        allAsPreview( limit ).map { list ->
            list.sortedBy { it.playlist.cleanName() }
        }

    fun sortPreviewsBySongCount( limit: Long = Long.MAX_VALUE ): Flow<List<PlaylistPreview>> =
        allAsPreview( limit ).map { list ->
            list.sortedBy( PlaylistPreview::songCount )
        }

    /**
     * Fetch all playlists, sort them according to [sortBy] and [sortOrder],
     * and return [PlaylistPreview] as the result.
     *
     * [sortBy] sorts all based on each playlist's property
     * such as [PlaylistSortBy.Name], [PlaylistSortBy.DateAdded], etc.
     * While [sortOrder] arranges order of sorted songs
     * to follow alphabetical order A to Z, or numerical order 0 to 9, etc.
     *
     * @param sortBy which playlist's property is used to sorts
     * @param sortOrder what order should results be in
     * @param limit stop query once number of results reaches this number
     *
     * @return a **SORTED** list of [Artist]'s that are continuously
     * updated to reflect changes within the database - wrapped by [Flow]
     *
     * @see PlaylistSortBy
     * @see SortOrder
     */
    fun sortPreviews(
        sortBy: PlaylistSortBy,
        sortOrder: SortOrder,
        limit: Long = Long.MAX_VALUE
    ): Flow<List<PlaylistPreview>> = when( sortBy ) {
        PlaylistSortBy.MostPlayed   -> sortPreviewsByMostPlayed( limit )
        PlaylistSortBy.Name         -> sortPreviewsByName( limit )
        PlaylistSortBy.DateAdded    -> allAsPreview( limit )       // Already sorted by ROWID
        PlaylistSortBy.SongCount    -> sortPreviewsBySongCount( limit )
    }.map( sortOrder::applyTo )
    //</editor-fold>
}