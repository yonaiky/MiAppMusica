package it.fast4x.rimusic

import androidx.compose.ui.util.fastZip
import androidx.media3.common.MediaItem
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomWarnings
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Artist
import it.fast4x.rimusic.models.Event
import it.fast4x.rimusic.models.EventWithSong
import it.fast4x.rimusic.models.Format
import it.fast4x.rimusic.models.Lyrics
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.QueuedMediaItem
import it.fast4x.rimusic.models.SearchQuery
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.models.SongAlbumMap
import it.fast4x.rimusic.models.SongArtistMap
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.models.SortedSongPlaylistMap
import it.fast4x.rimusic.utils.isExplicit
import kotlinx.coroutines.flow.Flow
import me.knighthat.database.AlbumTable
import me.knighthat.database.ArtistTable
import me.knighthat.database.Converters
import me.knighthat.database.EventTable
import me.knighthat.database.FormatTable
import me.knighthat.database.LyricsTable
import me.knighthat.database.PlaylistTable
import me.knighthat.database.QueuedMediaItemTable
import me.knighthat.database.SearchQueryTable
import me.knighthat.database.SongAlbumMapTable
import me.knighthat.database.SongArtistMapTable
import me.knighthat.database.SongPlaylistMapTable
import me.knighthat.database.SongTable
import me.knighthat.database.migration.From10To11Migration
import me.knighthat.database.migration.From11To12Migration
import me.knighthat.database.migration.From14To15Migration
import me.knighthat.database.migration.From20To21Migration
import me.knighthat.database.migration.From21To22Migration
import me.knighthat.database.migration.From22To23Migration
import me.knighthat.database.migration.From23To24Migration
import me.knighthat.database.migration.From24To25Migration
import me.knighthat.database.migration.From25To26Migration
import me.knighthat.database.migration.From26To27Migration
import me.knighthat.database.migration.From3To4Migration
import me.knighthat.database.migration.From7To8Migration
import me.knighthat.database.migration.From8To9Migration

@Dao
interface Database {
    companion object : Database by DatabaseInitializer.Instance.database

    private val _internal: RoomDatabase
        get() = DatabaseInitializer.Instance

    val songTable: SongTable
        get() = DatabaseInitializer.Instance.songTable
    val albumTable: AlbumTable
        get() = DatabaseInitializer.Instance.albumTable
    val artistTable: ArtistTable
        get() = DatabaseInitializer.Instance.artistTable
    val eventTable: EventTable
        get() = DatabaseInitializer.Instance.eventTable
    val formatTable: FormatTable
        get() = DatabaseInitializer.Instance.formatTable
    val lyricsTable: LyricsTable
        get() = DatabaseInitializer.Instance.lyricsTable
    val playlistTable: PlaylistTable
        get() = DatabaseInitializer.Instance.playlistTable
    val queueTable: QueuedMediaItemTable
        get() = DatabaseInitializer.Instance.queueTable
    val searchTable: SearchQueryTable
        get() = DatabaseInitializer.Instance.searchQueryTable
    val songAlbumMapTable: SongAlbumMapTable
        get() = DatabaseInitializer.Instance.songAlbumMapTable
    val songArtistMapTable: SongArtistMapTable
        get() = DatabaseInitializer.Instance.songArtistMapTable
    val songPlaylistMapTable: SongPlaylistMapTable
        get() = DatabaseInitializer.Instance.songPlaylistMapTable

    //**********************************************

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT DISTINCT (timestamp / 86400000) as timestampDay, event.* FROM event ORDER BY rowId DESC")
    fun events(): Flow<List<EventWithSong>>

    @Transaction
    fun insert(mediaItem: MediaItem, block: (Song) -> Song = { it }) {
        var title = mediaItem.mediaMetadata.title!!.toString()
        if(!title.startsWith(EXPLICIT_PREFIX, true) && mediaItem.isExplicit){
            title = EXPLICIT_PREFIX + title
        }
        val song = Song(
            id = mediaItem.mediaId,
            title = title,
            artistsText = mediaItem.mediaMetadata.artist?.toString(),
            durationText = mediaItem.mediaMetadata.extras?.getString("durationText"),
            thumbnailUrl = mediaItem.mediaMetadata.artworkUri?.toString()
        ).let(block).also { song ->
            if (songTable.insertIgnore( song ) == -1L) return
        }

        mediaItem.mediaMetadata.extras?.getString("albumId")?.let { albumId ->
            albumTable.insertIgnore(
                Album(
                    id = albumId,
                    title = mediaItem.mediaMetadata.albumTitle?.toString()
                )
            )
            songAlbumMapTable.insertIgnore(
                SongAlbumMap( song.id, albumId, null )
            )
        }

        val artistsNames = mediaItem.mediaMetadata.extras?.getStringArrayList("artistNames")
        val artistsIds = mediaItem.mediaMetadata.extras?.getStringArrayList("artistIds")

        if ( artistsNames != null && artistsIds != null ) {
            artistsNames.fastZip( artistsIds ) { artistName, artistId ->
                if( artistId == null ) return@fastZip

                artistTable.insertIgnore(
                    Artist(id = artistId, name = artistName)
                )
                songArtistMapTable.insertIgnore(
                    SongArtistMap(song.id, artistId)
                )
            }
        }
    }

    /**
     * Commit statements in BULK. If anything goes wrong during the transaction,
     * other statements will be cancelled and reversed to preserve database's integrity.
     * [Read more](https://sqlite.org/lang_transaction.html)
     *
     * [asyncTransaction] runs all statements on non-blocking
     * thread to prevent UI from going unresponsive.
     *
     * ## Best use cases:
     * - Commit multiple write statements that require data integrity
     * - Processes that take longer time to complete
     *
     * > Do NOT use this to retrieve data from the database.
     * > Use [asyncQuery] to retrieve records.
     *
     * @param block of statements to write to database
     */
    fun asyncTransaction( block: Database.() -> Unit ) =
        _internal.transactionExecutor.execute {
            this.block()
        }


    /**
     * Access and retrieve records from database.
     *
     * [asyncQuery] runs all statements asynchronously to
     * prevent blocking UI thread from going unresponsive.
     *
     * ## Best use cases:
     * - Background data retrieval
     * - Non-immediate UI component update (i.e. count number of songs)
     *
     * > Do NOT use this method to write data to database
     * > because it offers no fail-safe during write.
     * > Use [asyncTransaction] to modify database.
     *
     * @param block of statements to retrieve data from database
     */
    fun asyncQuery( block: Database.() -> Unit ) =
        _internal.queryExecutor.execute {
            this.block()
        }

    @RawQuery
    fun raw(supportSQLiteQuery: SupportSQLiteQuery): Int

    fun checkpoint() {
        raw(SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)"))
    }

    fun path() = _internal.openHelper.writableDatabase.path

    fun close() = _internal.close()
}

@androidx.room.Database(
    entities = [
        Song::class,
        SongPlaylistMap::class,
        Playlist::class,
        Artist::class,
        SongArtistMap::class,
        Album::class,
        SongAlbumMap::class,
        SearchQuery::class,
        QueuedMediaItem::class,
        Format::class,
        Event::class,
        Lyrics::class,
    ],
    views = [
        SortedSongPlaylistMap::class
    ],
    version = 27,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = From3To4Migration::class),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8, spec = From7To8Migration::class),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 11, to = 12, spec = From11To12Migration::class),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
        AutoMigration(from = 15, to = 16),
        AutoMigration(from = 16, to = 17),
        AutoMigration(from = 17, to = 18),
        AutoMigration(from = 18, to = 19),
        AutoMigration(from = 19, to = 20),
        AutoMigration(from = 20, to = 21, spec = From20To21Migration::class),
        AutoMigration(from = 21, to = 22, spec = From21To22Migration::class),
    ],
)
@TypeConverters(Converters::class)
abstract class DatabaseInitializer protected constructor() : RoomDatabase() {
    abstract val database: Database
    abstract val albumTable: AlbumTable
    abstract val artistTable: ArtistTable
    abstract val eventTable: EventTable
    abstract val formatTable: FormatTable
    abstract val lyricsTable: LyricsTable
    abstract val playlistTable: PlaylistTable
    abstract val queueTable: QueuedMediaItemTable
    abstract val searchQueryTable: SearchQueryTable
    abstract val songAlbumMapTable: SongAlbumMapTable
    abstract val songArtistMapTable: SongArtistMapTable
    abstract val songPlaylistMapTable: SongPlaylistMapTable
    abstract val songTable: SongTable

    companion object {

        lateinit var Instance: DatabaseInitializer

        private fun getDatabase() = Room
            .databaseBuilder(appContext(), DatabaseInitializer::class.java, "data.db")
            .addMigrations(
                From8To9Migration(),
                From10To11Migration(),
                From14To15Migration(),
                From22To23Migration(),
                From23To24Migration(),
                From24To25Migration(),
                From25To26Migration(),
                From26To27Migration()
            )
            .build()


        operator fun invoke() {
            if (!::Instance.isInitialized) reload()
        }

        fun reload() = synchronized(this) {
            Instance = getDatabase()
        }
    }
}
