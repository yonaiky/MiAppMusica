package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.enums.AlbumSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
@RewriteQueriesToDropUnusedColumns
interface AlbumTable: SqlTable<Album> {

    override val tableName: String
        get() = "Album"

    /**
     * @return all records from this table
     */
    @Query("SELECT DISTINCT * FROM Album")
    fun all(): Flow<List<Album>>

    /**
     * @return all albums from this table that are bookmarked by user
     */
    @Query("""
        SELECT DISTINCT *
        FROM Album
        WHERE bookmarkedAt IS NOT NULL
        ORDER BY ROWID
        LIMIT :limit
    """)
    fun allBookmarked( limit: Long = Long.MAX_VALUE ): Flow<List<Album>>

    /**
     * @return albums that have their songs mapped to at least 1 playlist
     */
    @Query("""
        SELECT DISTINCT A.*
        FROM Album A
        JOIN SongAlbumMap sam ON sam.albumId = A.id
        JOIN SongPlaylistMap spm ON spm.songId = sam.songId 
        ORDER BY A.ROWID
        LIMIT :limit
    """)
    fun allInLibrary( limit: Long = Long.MAX_VALUE ): Flow<List<Album>>

    /**
     * @return all songs of bookmarked albums
     */
    @Query("""
        SELECT DISTINCT S.*
        FROM SongAlbumMap sam
        JOIN Album A ON A.id = sam.albumId
        JOIN Song S ON S.id = sam.songId
        WHERE A.bookmarkedAt IS NOT NULL
        ORDER BY S.ROWID
        LIMIT :limit
    """)
    fun allSongsInBookmarked( limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    /**
     * @param albumId of album to look for
     * @return [Album] that has [Album.id] matches [albumId]
     */
    @Query("SELECT DISTINCT * FROM Album WHERE id = :albumId")
    fun findById( albumId: String ): Flow<Album?>

    //<editor-fold defaultstate="collapsed" desc="Sort bookmarked">
    fun sortBookmarkedByTitle( limit: Long = Long.MAX_VALUE ): Flow<List<Album>> =
        allBookmarked( limit ).map { list ->
            list.sortedBy( Album::cleanTitle )
        }

    fun sortBookmarkedByYear( limit: Long = Long.MAX_VALUE ): Flow<List<Album>> =
        allBookmarked( limit ).map { list ->
            list.sortedBy( Album::year )
        }

    fun sortBookmarkedByArtist( limit: Long = Long.MAX_VALUE ): Flow<List<Album>> =
        allBookmarked( limit ).map { list ->
            list.sortedBy( Album::cleanAuthorsText )
        }

    @Query("""
        SELECT DISTINCT A.* 
        FROM Album A
        JOIN SongAlbumMap sam ON sam.albumId = A.id 
        WHERE A.bookmarkedAt IS NOT NULL
        GROUP BY A.id
        ORDER BY COUNT(sam.songId)
        LIMIT :limit
    """)
    fun sortBookmarkedBySongsCount( limit: Long = Long.MAX_VALUE ): Flow<List<Album>>

    @Query("""
        SELECT DISTINCT A.*
        FROM Album A
        JOIN SongAlbumMap sam ON sam.albumId = A.id
        JOIN Song S ON S.id = sam.songId
        WHERE A.bookmarkedAt IS NOT NULL
        GROUP BY A.id
        ORDER BY SUM(
            CASE 
                WHEN S.durationText LIKE '%:%:%' THEN (
                    (SUBSTR(S.durationText, 1, INSTR(S.durationText, ':') - 1) * 3600) + 
                    (SUBSTR(S.durationText, INSTR(S.durationText, ':') + 1, INSTR(SUBSTR(S.durationText, INSTR(S.durationText, ':') + 1), ':') - 1) * 60) + 
                    SUBSTR(S.durationText, INSTR(S.durationText, ':') + INSTR(SUBSTR(S.durationText, INSTR(S.durationText, ':') + 1), ':') + 1)
                )
                ELSE (
                    (SUBSTR(S.durationText, 1, INSTR(S.durationText, ':') - 1) * 60) + 
                    (SUBSTR(S.durationText, INSTR(S.durationText, ':') + 1))
                )
            END 
        )
        LIMIT :limit
    """)
    // Duration conversion is baked into SQL syntax to reduce code complexity
    // at the cost of unfriendly syntax, potentially makes it harder to maintain or reuse.
    fun sortBookmarkedByDuration( limit: Long = Long.MAX_VALUE ): Flow<List<Album>>

    /**
     * Fetch all bookmarked albums and sort
     * them according to [sortBy] and [sortOrder].
     *
     * [sortBy] sorts all based on each album's property
     * such as [AlbumSortBy.Title], [AlbumSortBy.Year], etc.
     * While [sortOrder] arranges order of sorted songs
     * to follow alphabetical order A to Z, or numerical order 0 to 9, etc.
     *
     * @param sortBy which album's property is used to sort
     * @param sortOrder what order should results be in
     * @param limit stop query once number of results reaches this number
     *
     * @return a **SORTED** list of [Album]'s that are continuously
     * updated to reflect changes within the database - wrapped by [Flow]
     *
     * @see AlbumSortBy
     * @see SortOrder
     */
    fun sortBookmarked(
        sortBy: AlbumSortBy,
        sortOrder: SortOrder,
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Album>> = when( sortBy ) {
        AlbumSortBy.Title       -> sortBookmarkedByTitle( limit )
        AlbumSortBy.Year        -> sortBookmarkedByYear( limit )
        AlbumSortBy.DateAdded   -> allBookmarked( limit )       // Already sorted by ROWID
        AlbumSortBy.Artist      -> sortBookmarkedByArtist( limit )
        AlbumSortBy.Songs       -> sortBookmarkedBySongsCount( limit )
        AlbumSortBy.Duration    -> sortBookmarkedByDuration( limit )
    }.map( sortOrder::applyTo )
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Sort albums in library">
    fun sortInLibraryByTitle( limit: Long = Long.MAX_VALUE ): Flow<List<Album>> =
        allInLibrary( limit ).map { list ->
            list.sortedBy( Album::cleanTitle )
        }

    fun sortInLibraryByYear( limit: Long = Long.MAX_VALUE ): Flow<List<Album>> =
        allInLibrary( limit ).map { list ->
            list.sortedBy( Album::year )
        }

    fun sortInLibraryByArtist( limit: Long = Long.MAX_VALUE ): Flow<List<Album>> =
        allInLibrary( limit ).map { list ->
            list.sortedBy( Album::cleanAuthorsText )
        }

    @Query("""
        SELECT DISTINCT A.*
        FROM SongPlaylistMap spm
        JOIN SongAlbumMap sam ON sam.songId = spm.songId
        JOIN Album A ON A.id = sam.albumId
        GROUP BY A.id
        ORDER BY COUNT(sam.songId)
        LIMIT :limit
    """)
    fun sortInLibraryBySongsCount( limit: Long = Long.MAX_VALUE ): Flow<List<Album>>

    @Query("""
        SELECT DISTINCT A.*
        FROM SongPlaylistMap spm
        JOIN SongAlbumMap sam ON sam.songId = spm.songId
        JOIN Album A ON A.id = sam.albumId
        JOIN Song S ON S.id = sam.songId
        GROUP BY A.id
        ORDER BY SUM(
            CASE 
                WHEN S.durationText LIKE '%:%:%' THEN (
                    (SUBSTR(S.durationText, 1, INSTR(S.durationText, ':') - 1) * 3600) + 
                    (SUBSTR(S.durationText, INSTR(S.durationText, ':') + 1, INSTR(SUBSTR(S.durationText, INSTR(S.durationText, ':') + 1), ':') - 1) * 60) + 
                    SUBSTR(S.durationText, INSTR(S.durationText, ':') + INSTR(SUBSTR(S.durationText, INSTR(S.durationText, ':') + 1), ':') + 1)
                )
                ELSE (
                    (SUBSTR(S.durationText, 1, INSTR(S.durationText, ':') - 1) * 60) + 
                    (SUBSTR(S.durationText, INSTR(S.durationText, ':') + 1))
                )
            END 
        )
        LIMIT :limit
    """)
    fun sortInLibraryByDuration( limit: Long = Long.MAX_VALUE ): Flow<List<Album>>

    /**
     * Fetch all albums that have their songs mapped to
     * at least 1 playlist in library and sort
     * them according to [sortBy] and [sortOrder].
     *
     * [sortBy] sorts all based on each album's property
     * such as [AlbumSortBy.Title], [AlbumSortBy.Year], etc.
     * While [sortOrder] arranges order of sorted songs
     * to follow alphabetical order A to Z, or numerical order 0 to 9, etc.
     *
     * @param sortBy which album's property is used to sort
     * @param sortOrder what order should results be in
     * @param limit stop query once number of results reaches this number
     *
     * @return a **SORTED** list of [Album]'s that are continuously
     * updated to reflect changes within the database - wrapped by [Flow]
     *
     * @see AlbumSortBy
     * @see SortOrder
     */
    fun sortInLibrary(
        sortBy: AlbumSortBy,
        sortOrder: SortOrder,
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Album>> = when( sortBy ) {
        AlbumSortBy.Title       -> sortInLibraryByTitle( limit )
        AlbumSortBy.Year        -> sortInLibraryByYear( limit )
        AlbumSortBy.DateAdded   -> allInLibrary( limit )        // Already sorted by ROWID
        AlbumSortBy.Artist      -> sortInLibraryByArtist( limit )
        AlbumSortBy.Songs       -> sortInLibraryBySongsCount( limit )
        AlbumSortBy.Duration    -> sortInLibraryByDuration( limit )
    }.map( sortOrder::applyTo )
    //</editor-fold>
}