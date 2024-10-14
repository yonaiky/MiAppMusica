package database

import androidx.compose.ui.window.application
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.entities.Album
import database.entities.Artist
import database.entities.Event
import database.entities.Format
import database.entities.Lyrics
import database.entities.Playlist
import database.entities.SearchQuery
import database.entities.Song
import database.entities.SongAlbumMap
import database.entities.SongArtistMap
import database.entities.SongPlaylistMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [
        Album::class,
        Artist::class,
        Event::class,
        Format::class,
        Lyrics::class,
        //QueuedMediaItem::class, //TODO: implement
        Playlist::class,
        SearchQuery::class,
        Song::class,
        SongAlbumMap::class,
        SongArtistMap::class,
        SongPlaylistMap::class
    ],
    version = 23,
    exportSchema = true
)
@ConstructedBy(MusicDatabaseConstructor::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun getDao(): MusicDatabaseDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MusicDatabaseConstructor : RoomDatabaseConstructor<MusicDatabase>

@Dao
interface MusicDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Song): Long

    @Delete
    suspend fun delete(item: Song)

    @Query("SELECT * FROM Song")
    fun getAll(): Flow<List<Song>>
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<MusicDatabase>
): MusicDatabase {
    return builder
        //.addMigrations(MIGRATIONS)
        //.fallbackToDestructiveMigrationOnDowngrade()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

