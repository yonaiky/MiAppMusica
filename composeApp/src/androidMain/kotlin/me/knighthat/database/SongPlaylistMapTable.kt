package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.MODIFIED_PREFIX
import it.fast4x.rimusic.enums.PlaylistSongSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.utils.durationTextToMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        SELECT DISTINCT S.*
        FROM SongPlaylistMap smp
        JOIN Song S ON S.id = smp.songId
        WHERE smp.playlistId = :playlistId
        ORDER BY S.ROWID
        LIMIT :limit
    """)
    fun allSongsOf( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    //<editor-fold defaultstate="collapsed" desc="Sort songs of playlist">
    @Query("""
        SELECT DISTINCT S.*
        FROM SongPlaylistMap spm
        LEFT JOIN Song S ON S.id = spm.songId
        LEFT JOIN SongAlbumMap sam ON sam.songId = S.id
        LEFT JOIN Album A ON A.id = sam.albumId
        WHERE spm.playlistId = :playlistId
        ORDER BY 
            A.title IS NULL, 
            CASE
                WHEN A.title LIKE "$MODIFIED_PREFIX%" THEN SUBSTR(A.title, LENGTH('$MODIFIED_PREFIX') + 1)
                ELSE A.title
            END
        LIMIT :limit
    """)
    fun sortSongsByAlbum( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    @Query("""
        SELECT DISTINCT S.*
        FROM SongPlaylistMap spm
        LEFT JOIN Song S ON S.id = spm.songId
        LEFT JOIN SongAlbumMap sam ON sam.songId = S.id
        LEFT JOIN Album A ON A.id = sam.albumId
        WHERE spm.playlistId = :playlistId
        ORDER BY A.year IS NULL, A.year
        LIMIT :limit
    """)
    fun sortSongsByAlbumYear( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    fun sortSongsByArtist( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allSongsOf( playlistId, limit ).map { list ->
            list.sortedBy( Song::cleanArtistsText )
        }

    @Query("""
        SELECT DISTINCT S.*
        FROM SongPlaylistMap spm
        LEFT JOIN Song S ON S.id = spm.songId
        LEFT JOIN SongAlbumMap sam ON sam.songId = S.id
        LEFT JOIN Album A ON A.id = sam.albumId
        WHERE spm.playlistId = :playlistId
        ORDER BY 
            A.title IS NULL, 
            S.artistsText IS NULL,
            CASE
                WHEN A.title LIKE "$MODIFIED_PREFIX%" THEN SUBSTR(A.title, LENGTH('$MODIFIED_PREFIX') + 1)
                ELSE A.title
            END,
            CASE
                WHEN S.artistsText LIKE "$MODIFIED_PREFIX%" THEN SUBSTR(S.artistsText, LENGTH('$MODIFIED_PREFIX') + 1)
                ELSE S.artistsText
            END
        LIMIT :limit
    """)
    fun sortSongsByAlbumAndArtist( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    @Query("""
        SELECT S.*
        FROM SongPlaylistMap spm
        LEFT JOIN Song S ON S.id = spm.songId
        LEFT JOIN Event E ON E.songId = S.id
        WHERE spm.playlistId = :playlistId
        ORDER BY E.timestamp
        LIMIT :limit
    """)
    fun sortSongsByDatePlayed( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    fun sortSongsByPlayTime( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allSongsOf( playlistId, limit ).map { list ->
            list.sortedBy( Song::totalPlayTimeMs )
        }

    fun sortSongsByRelativePlayTime( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allSongsOf( playlistId, limit ).map { list ->
            list.sortedBy( Song::relativePlayTime )
        }

    @Query("""
        SELECT S.*
        FROM SongPlaylistMap spm
        LEFT JOIN Song S ON S.id = spm.songId
        WHERE spm.playlistId = :playlistId
        ORDER BY spm.position
        LIMIT :limit
    """)
    fun sortSongsByPosition( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    fun sortSongsByTitle( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allSongsOf( playlistId, limit ).map { list ->
            list.sortedBy( Song::cleanTitle )
        }

    fun sortSongsByDuration( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allSongsOf( playlistId, limit ).map { list ->
            list.sortedBy { durationTextToMillis( it.durationText ?: "0" ) }
        }

    fun sortSongsByLikedAt( playlistId: Long, limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allSongsOf( playlistId, limit ).map { list ->
            list.sortedBy( Song::likedAt )
        }

    /**
     * Fetch all songs that were mapped to [playlistId] and sort
     * them according to [sortBy] and [sortOrder].
     *
     * [sortBy] sorts all based on each song's property
     * such as [PlaylistSongSortBy.Artist], [PlaylistSongSortBy.Duration], etc.
     * While [sortOrder] arranges order of sorted songs
     * to follow alphabetical order A to Z, or numerical order 0 to 9, etc.
     *
     * @param sortBy which song's property is used to sort
     * @param sortOrder what order should results be in
     * @param limit stop query once number of results reaches this number
     *
     * @return a **SORTED** list of [Song]'s that are continuously
     * updated to reflect changes within the database - wrapped by [Flow]
     *
     * @see PlaylistSongSortBy
     * @see SortOrder
     */
    fun sortSongs(
        playlistId: Long,
        sortBy: PlaylistSongSortBy,
        sortOrder: SortOrder,
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Song>> = when( sortBy ) {
        PlaylistSongSortBy.Album            -> sortSongsByAlbum( playlistId, limit )
        PlaylistSongSortBy.AlbumYear        -> sortSongsByAlbumYear( playlistId, limit )
        PlaylistSongSortBy.Artist           -> sortSongsByArtist( playlistId, limit )
        PlaylistSongSortBy.ArtistAndAlbum   -> sortSongsByAlbumAndArtist( playlistId, limit )
        PlaylistSongSortBy.DatePlayed       -> sortSongsByDatePlayed( playlistId, limit )
        PlaylistSongSortBy.PlayTime         -> sortSongsByPlayTime( playlistId, limit )
        PlaylistSongSortBy.RelativePlayTime -> sortSongsByRelativePlayTime( playlistId, limit )
        PlaylistSongSortBy.Position         -> sortSongsByPosition( playlistId, limit )
        PlaylistSongSortBy.Title            -> sortSongsByTitle( playlistId, limit )
        PlaylistSongSortBy.Duration         -> sortSongsByDuration( playlistId, limit )
        PlaylistSongSortBy.DateLiked        -> sortSongsByLikedAt( playlistId, limit )
        PlaylistSongSortBy.DateAdded        -> allSongsOf( playlistId )     // Already sorted by ROWID
    }.map( sortOrder::applyTo )
    //</editor-fold>
}