package database

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.io.File

@Database(entities = [Song::class], version = 23, exportSchema = true)
@ConstructedBy(MusicDatabaseConstructor::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun getDao(): MusicDatabaseDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MusicDatabaseConstructor : RoomDatabaseConstructor<MusicDatabase>

@Dao
interface MusicDatabaseDao {
    @Insert
    suspend fun insert(item: Song)

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

