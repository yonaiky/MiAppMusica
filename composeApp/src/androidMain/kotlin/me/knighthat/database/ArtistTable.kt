package me.knighthat.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.enums.ArtistSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
@RewriteQueriesToDropUnusedColumns
interface ArtistTable: SqlTable<Artist> {

    override val tableName: String
        get() = "Artist"

    /**
     * @return all artists from this table that are followed by user
     */
    @Query("""
        SELECT DISTINCT * 
        FROM Artist
        WHERE bookmarkedAt IS NOT NULL
        ORDER BY ROWID 
        LIMIT :limit
    """)
    fun allFollowing(limit: Long = Long.MAX_VALUE ): Flow<List<Artist>>

    @Query("""
        SELECT DISTINCT A.*
        FROM Artist A
        JOIN SongArtistMap sam ON sam.artistId = A.id
        JOIN SongPlaylistMap spm ON spm.songId = sam.songId
        ORDER BY A.ROWID
        LIMIT :limit
    """)
    fun allInLibrary( limit: Long = Long.MAX_VALUE ): Flow<List<Artist>>

    /**
     * @param artistId of artist to look for
     * @return [Artist] that has [Artist.id] matches [artistId]
     */
    @Query("SELECT DISTINCT * FROM Artist WHERE id = :artistId")
    fun findById( artistId: String ): Flow<Artist?>

    //<editor-fold defaultstate="collapsed" desc="Sort all">
    fun sortAllByName( limit: Long = Long.MAX_VALUE ): Flow<List<Artist>> =
        allFollowing( limit ).map { list ->
            list.sortedBy( Artist::cleanName )
        }

    /**
     * Fetch all following artists and sort
     * them according to [sortBy] and [sortOrder].
     *
     * [sortBy] sorts all based on each artist's property
     * such as [ArtistSortBy.Name], [ArtistSortBy.DateAdded], etc.
     * While [sortOrder] arranges order of sorted songs
     * to follow alphabetical order A to Z, or numerical order 0 to 9, etc.
     *
     * @param sortBy which artist's property is used to sort
     * @param sortOrder what order should results be in
     * @param limit stop query once number of results reaches this number
     *
     * @return a **SORTED** list of [Artist]'s that are continuously
     * updated to reflect changes within the database - wrapped by [Flow]
     *
     * @see ArtistSortBy
     * @see SortOrder
     */
    fun sortAll(
        sortBy: ArtistSortBy,
        sortOrder: SortOrder,
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Artist>> = when( sortBy ) {
        ArtistSortBy.Name       -> sortAllByName( limit )
        ArtistSortBy.DateAdded  -> allFollowing( limit )
    }.map( sortOrder::applyTo )
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Sort artists in library">
    fun sortInLibraryByName( limit: Long = Long.MAX_VALUE ): Flow<List<Artist>> =
        allInLibrary( limit ).map { list ->
            list.sortedBy( Artist::cleanName )
        }

    /**
     * Fetch all artists that have their songs mapped to
     * at least 1 playlist in library and sort
     * them according to [sortBy] and [sortOrder].
     *
     * [sortBy] sorts all based on each artist's property
     * such as [ArtistSortBy.Name], [ArtistSortBy.DateAdded], etc.
     * While [sortOrder] arranges order of sorted songs
     * to follow alphabetical order A to Z, or numerical order 0 to 9, etc.
     *
     * @param sortBy which artist's property is used to sort
     * @param sortOrder what order should results be in
     * @param limit stop query once number of results reaches this number
     *
     * @return a **SORTED** list of [Artist]'s that are continuously
     * updated to reflect changes within the database - wrapped by [Flow]
     *
     * @see ArtistSortBy
     * @see SortOrder
     */
    fun sortInLibrary(
        sortBy: ArtistSortBy,
        sortOrder: SortOrder,
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Artist>> = when( sortBy ) {
        ArtistSortBy.Name       -> sortInLibraryByName( limit )
        ArtistSortBy.DateAdded  -> allInLibrary( limit )     // Already sorted by ROWID
    }.map( sortOrder::applyTo )
    //</editor-fold>
}