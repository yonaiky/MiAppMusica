package me.knighthat.database

import android.database.SQLException
import androidx.media3.common.MediaItem
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.MODIFIED_PREFIX
import it.fast4x.rimusic.enums.SongSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.service.modern.LOCAL_KEY_PREFIX
import it.fast4x.rimusic.utils.asSong
import it.fast4x.rimusic.utils.durationToMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
@RewriteQueriesToDropUnusedColumns
interface SongTable: SqlTable<Song> {

    override val tableName: String
        get() = "Song"

    /**
     * @return all records from this table
     */
    @Query("""
        SELECT DISTINCT * 
        FROM Song 
        WHERE totalPlayTimeMs >= :excludeHidden
        ORDER BY ROWID 
        LIMIT :limit
    """)
    fun all(
        limit: Long = Long.MAX_VALUE,
        excludeHidden: Boolean = false
    ): Flow<List<Song>>

    /**
     * @return all records that have [Song.id] start with [LOCAL_KEY_PREFIX]
     */
    @Query("SELECT DISTINCT * FROM Song WHERE id LIKE '$LOCAL_KEY_PREFIX%'")
    fun allOnDevice(): Flow<List<Song>>

    @Query("""
        SELECT DISTINCT * 
        FROM Song 
        WHERE likedAt IS NOT NULL AND likedAt > 0
        ORDER BY ROWID
        LIMIT :limit
    """)
    fun allFavorites( limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    @Query("""
        SELECT DISTINCT * 
        FROM Song 
        WHERE likedAt IS NOT NULL AND likedAt < 0
        ORDER BY ROWID
        LIMIT :limit
    """)
    fun allDisliked( limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    /**
     * @param songId of album to look for
     * @return [Song] that has [Song.id] matches [songId]
     */
    @Query("SELECT DISTINCT * FROM Song WHERE id = :songId")
    fun findById( songId: String ): Flow<Song?>

    /**
     * [searchTerm] appears in [Song.title] or [Song.artistsText].
     * Additionally, it's **case-insensitive**
     *
     * I.E.: `name` matches `1name_to` and `1_NaMe_to`
     *
     * @param searchTerm what to look for
     * @return all [Song]s that have [Song.title] or [Song.artistsText] contain [searchTerm]
     */
    @Query("""
        SELECT DISTINCT * 
        FROM Song 
        WHERE title LIKE '%' || :searchTerm || '%' COLLATE NOCASE
        OR artistsText LIKE '%' || :searchTerm || '%' COLLATE NOCASE
    """)
    fun findAllTitleArtistContains( searchTerm: String ): Flow<List<Song>>

    /**
     * Require [artistName] to match [Song.artistsText], except for case-sensitive.
     *
     * I.E.: `Michael` matches both `michael` and `MICHAEL`
     *
     * Additionally, [MODIFIED_PREFIX] is removed before comparision.
     *
     * @param artistName [Song.artistsText] to look for
     * @return all [Song]s that have [Song.artistsText] match [artistName]
     */
    @Query("""
        SELECT DISTINCT * 
        FROM Song 
        WHERE trim(
            CASE 
                WHEN artistsText LIKE '$MODIFIED_PREFIX%' THEN SUBSTR(artistsText, LENGTH('$MODIFIED_PREFIX') + 1)
                ELSE artistsText
            END
        ) COLLATE NOCASE = trim(:artistName) COLLATE NOCASE
    """)
    fun findAllByArtist( artistName: String ): Flow<List<Song>>

    /**
     * Convert [MediaItem] into [Song] and attempt to it into database.
     *
     * ### Standalone use
     *
     * When error occurs and [SQLException] is thrown,
     * it'll simply be ignored.
     *
     * ### Transaction use
     *
     * When error occurs and [SQLException] is thrown,
     * it'll simply be ignored and the transaction continues.
     *
     * @param mediaItem intended to insert in to database
     * @return ROWID of this new record, -1 if error occurs
     */
    fun insertIgnore( mediaItem: MediaItem ): Long = insertIgnore( mediaItem.asSong )

    /**
     * @return whether any record in [Song] table has id [songId]
     */
    @Query("SELECT COUNT(*) > 0 FROM Song WHERE id = :songId")
    fun exists( songId: String ): Flow<Boolean>

    /**
     * Should not be called when you have a hold of [Song].
     *
     * The return value is **null safe**. When [Song.id] is
     * not found in database, it returns `false`
     *
     * @param songId of song to query
     * @return whether [Song.likedAt] is set
     */
    @Query("""
        SELECT COALESCE(
            (
                SELECT likedAt IS NOT NULL AND likedAt > 0
                FROM Song 
                WHERE id = :songId
            )
            , 0
        ) 
    """)
    fun isLiked( songId: String ): Flow<Boolean>

    /**
     * A tri-state represents 3 different states of like.
     *
     * - `true` - when song is **liked**
     * - `false` - when song is **disliked**
     * - `null` - if value is **unset** (neutral)
     *
     * @param songId of song to query
     * @return value represent [Song.likedAt] state
     */
    @Query("""
        SELECT 
            CASE 
                WHEN likedAt > 0 THEN 1 
                WHEN likedAt < 0 THEN 0 
                ELSE NULL 
            END 
        FROM Song 
        WHERE id = :songId
    """)
    fun likeState( songId: String ): Flow<Boolean?>

    /**
     * This query updates the [Song.likedAt] column to
     * cycle through three values in a fixed rotation:
     *
     * - `-1` to `0`
     * - `0` to `1`
     * - `1` to `-1`
     *
     * @param songId of song to be updated
     * @return number of rows affected by this operation
     */
    @Query("""
        UPDATE Song  
        SET likedAt = 
            CASE  
                WHEN likedAt = -1 THEN NULL
                WHEN likedAt IS NULL THEN 1  
                WHEN likedAt = 1 THEN -1  
                ELSE likedAt  
            END  
        WHERE id = :songId
    """)
    fun rotateLikeState( songId: String ): Int

    /**
     * ### If song **IS NOT** liked
     *
     * Set [Song.likedAt] to current time
     *
     * ### If song **IS** liked
     *
     * Set [Song.likedAt] to `null`
     *
     * @return number of rows affected
     */
    @Query("""
        UPDATE Song
        SET likedAt = 
            CASE 
                WHEN likedAt IS NULL THEN strftime('%s', 'now') * 1000
                ELSE NULL
            END
        WHERE id = :songId
    """)
    fun toggleLike( songId: String ): Int

    /**
     * Set [Song.likedAt] according to provided [likeState]
     *
     * - `false` is dislike
     * - `null` is neutral
     * - `true` is like
     *
     * @param songId  of song to be updated
     * @return number of rows affected
     */
    @Query("""
        UPDATE Song
        SET likedAt = 
            CASE
                WHEN :likeState = 0 THEN -1
                WHEN :likeState = 1 THEN strftime('%s', 'now') * 1000 
                ELSE :likeState 
            END
        WHERE id = :songId
    """)
    fun likeState( songId: String, likeState: Boolean? ): Int

    /**
     * @param songId identifier of [Song]
     * @param title new name of this song
     *
     * @return number of albums affected by this operation
     */
    @Query("UPDATE Song SET title = :title WHERE id = :songId")
    fun updateTitle( songId: String, title: String ): Int

    /**
     * @param songId identifier of [Song]
     * @param artistsText artists to display
     *
     * @return number of albums affected by this operation
     */
    @Query("UPDATE Song SET artistsText = :artistsText WHERE id = :songId")
    fun updateArtists( songId: String, artistsText: String ): Int

    //<editor-fold defaultstate="collapsed" desc="Sort all">
    fun sortAllByPlayTime( limit: Long = Long.MAX_VALUE, excludeHidden: Boolean = false ): Flow<List<Song>> =
        all( limit ).map { list ->
            list.sortedBy( Song::totalPlayTimeMs )
        }

    fun sortAllByRelativePlayTime( limit: Long = Long.MAX_VALUE, excludeHidden: Boolean = false ): Flow<List<Song>> =
        all( limit ).map { list ->
            list.sortedBy( Song::relativePlayTime )
        }

    fun sortAllByTitle( limit: Long = Long.MAX_VALUE, excludeHidden: Boolean = false ): Flow<List<Song>> =
        all( limit ).map { list ->
            list.sortedBy( Song::cleanTitle )
        }

    @Query("""
        SELECT DISTINCT S.* 
        FROM Song S 
        LEFT JOIN Event E ON E.songId = S.id 
        WHERE totalPlayTimeMs >= :excludeHidden
        ORDER BY E.timestamp
        LIMIT :limit
    """)
    fun sortAllByDatePlayed( limit: Long = Long.MAX_VALUE, excludeHidden: Boolean = false ): Flow<List<Song>>

    fun sortAllByLikedAt( limit: Long = Long.MAX_VALUE, excludeHidden: Boolean = false ): Flow<List<Song>> =
        all( limit ).map { list ->
            list.sortedBy( Song::likedAt )
        }

    fun sortAllByArtist( limit: Long = Long.MAX_VALUE, excludeHidden: Boolean = false ): Flow<List<Song>> =
        all( limit ).map { list ->
            list.sortedBy( Song::cleanArtistsText )
        }

    fun sortAllByDuration( limit: Long = Long.MAX_VALUE, excludeHidden: Boolean = false ): Flow<List<Song>> =
        all( limit ).map { list ->
            list.sortedBy {
                durationToMillis( it.durationText ?: "0:0" )
            }
        }

    @Query("""
        SELECT DISTINCT S.*
        FROM Song S
        LEFT JOIN SongAlbumMap sam ON sam.songId = S.id
        LEFT JOIN Album A ON A.id = sam.albumId
        WHERE totalPlayTimeMs >= :excludeHidden
        ORDER BY 
            CASE 
                WHEN A.title LIKE '$MODIFIED_PREFIX%' THEN SUBSTR(A.title, LENGTH('$MODIFIED_PREFIX') + 1)
                ELSE A.title
            END
        LIMIT :limit
    """)
    fun sortAllByAlbumName( limit: Long = Long.MAX_VALUE, excludeHidden: Boolean = false ): Flow<List<Song>>

    /**
     * Fetch all songs from the database and sort them
     * according to [sortBy] and [sortOrder]. It also
     * excludes songs if condition of [excludeHidden] is met.
     *
     * [sortBy] sorts all based on each song's property
     * such as [SongSortBy.Title], [SongSortBy.PlayTime], etc.
     * While [sortOrder] arranges order of sorted songs
     * to follow alphabetical order A to Z, or numerical order 0 to 9, etc.
     *
     * [excludeHidden] is an optional parameter that indicates
     * whether the final results contain songs that are hidden
     * (in)directly by the user.
     * `-1` shows hidden while `0` does not.
     *
     * @param sortBy which song's property is used to sort
     * @param sortOrder what order should results be in
     * @param excludeHidden whether to include hidden songs in final results or not
     *
     * @return a **SORTED** list of [Song]s that are continuously
     * updated to reflect changes within the database - wrapped by [Flow]
     *
     * @see SongSortBy
     * @see SortOrder
     */
    fun sortAll(
        sortBy: SongSortBy,
        sortOrder: SortOrder,
        limit: Long = Long.MAX_VALUE,
        excludeHidden: Boolean = false
    ): Flow<List<Song>> = when( sortBy ){
        SongSortBy.PlayTime         -> sortAllByPlayTime( limit, excludeHidden )
        SongSortBy.RelativePlayTime -> sortAllByRelativePlayTime( limit, excludeHidden )
        SongSortBy.Title            -> sortAllByTitle( limit, excludeHidden )
        SongSortBy.DateAdded        -> all( limit, excludeHidden )      // Already sorted by ROWID
        SongSortBy.DatePlayed       -> sortAllByDatePlayed( limit, excludeHidden )
        SongSortBy.DateLiked        -> sortAllByLikedAt( limit, excludeHidden )
        SongSortBy.Artist           -> sortAllByArtist( limit, excludeHidden )
        SongSortBy.Duration         -> sortAllByDuration( limit, excludeHidden )
        SongSortBy.AlbumName        -> sortAllByAlbumName( limit, excludeHidden )
    }.map( sortOrder::applyTo )
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Sort favorites">
    fun sortFavoritesByArtist( limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allFavorites( limit ).map { list ->
            list.sortedBy( Song::cleanArtistsText )
        }

    fun sortFavoritesByPlayTime( limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allFavorites( limit ).map { list ->
            list.sortedBy( Song::totalPlayTimeMs )
        }

    fun sortFavoritesByRelativePlayTime( limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allFavorites( limit ).map { list ->
            list.sortedBy( Song::relativePlayTime )
        }

    fun sortFavoritesByTitle( limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allFavorites( limit ).map { list ->
            list.sortedBy( Song::cleanTitle )
        }

    fun sortFavoritesByLikedAt( limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allFavorites( limit ).map { list ->
            list.sortedBy( Song::likedAt )
        }

    @Query("""
        SELECT DISTINCT S.* 
        FROM Song S 
        LEFT JOIN Event E ON E.songId = S.id 
        WHERE likedAt IS NOT NULL AND likedAt > 0
        ORDER BY E.timestamp
        LIMIT :limit
    """)
    fun sortFavoritesByDatePlayed( limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    fun sortFavoritesByDuration( limit: Long = Long.MAX_VALUE ): Flow<List<Song>> =
        allFavorites( limit ).map { list ->
            list.sortedBy {
                durationToMillis( it.durationText ?: "0:0" )
            }
        }

    @Query("""
        SELECT DISTINCT S.*
        FROM Song S
        LEFT JOIN SongAlbumMap sam ON sam.songId = S.id
        LEFT JOIN Album A ON A.id = sam.albumId
        WHERE likedAt IS NOT NULL AND likedAt > 0
        ORDER BY 
            CASE 
                WHEN A.title LIKE '$MODIFIED_PREFIX%' THEN SUBSTR(A.title, LENGTH('$MODIFIED_PREFIX') + 1)
                ELSE A.title
            END
        LIMIT :limit
    """)
    fun sortFavoritesByAlbumName( limit: Long = Long.MAX_VALUE ): Flow<List<Song>>

    /**
     * Fetch all favorite songs and sort them according to [sortBy] and [sortOrder].
     *
     * [sortBy] sorts all based on each song's property
     * such as [SongSortBy.Title], [SongSortBy.PlayTime], etc.
     * While [sortOrder] arranges order of sorted songs
     * to follow alphabetical order A to Z, or numerical order 0 to 9, etc.
     *
     * @param sortBy which song's property is used to sort
     * @param sortOrder what order should results be in
     * @param limit stop query once number of results reaches this number
     *
     * @return a **SORTED** list of [Song]s that are continuously
     * updated to reflect changes within the database - wrapped by [Flow]
     *
     * @see SongSortBy
     * @see SortOrder
     */
    fun sortFavorites(
        sortBy: SongSortBy,
        sortOrder: SortOrder,
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Song>> = when( sortBy ) {
        SongSortBy.PlayTime         -> sortFavoritesByPlayTime( limit )
        SongSortBy.RelativePlayTime -> sortFavoritesByRelativePlayTime( limit )
        SongSortBy.Title            -> sortFavoritesByTitle( limit )
        SongSortBy.DateAdded        -> allFavorites( limit )      // Already sorted by ROWID
        SongSortBy.DatePlayed       -> sortFavoritesByDatePlayed( limit )
        SongSortBy.DateLiked        -> sortFavoritesByLikedAt( limit )
        SongSortBy.Artist           -> sortFavoritesByArtist( limit )
        SongSortBy.Duration         -> sortFavoritesByDuration( limit )
        SongSortBy.AlbumName        -> sortFavoritesByAlbumName( limit )
    }.map( sortOrder::applyTo )
    //</editor-fold>
}