package me.knighthat.database

import android.database.SQLException
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * Define class as a table.
 *
 * Where [T] is record's type.
 */
interface SqlTable<T> {

    /**
     * @return name of the table this [androidx.room.Dao] represents.
     */
    val tableName: String
        get() = ""

    /**
     * Attempt to write [record] into database.
     *
     * ### Standalone use
     *
     * When error occurs and [SQLException] is thrown,
     * the process is cancel and passes exception to caller.
     *
     * ### Transaction use
     *
     * When error occurs and [SQLException] is thrown,
     * **the entire transaction rolls back** and passes exception to caller.
     *
     * > Note: Use this if inserting record is crucial for
     * > the transaction to continue.
     *
     * @param record data intended to insert in to database
     * @return ROWID of this new record, throws exception when fail
     * @throws SQLException when there's a conflict
     */
    @Insert
    @Throws(SQLException::class)
    fun insert( record: T ): Long

    /**
     * Attempt to write the list of [T] to database.
     *
     * ### Standalone use
     *
     * When **1** element fails, the entire list is
     * considered failed, database rolls back its operation,
     * and passes exception to caller.
     *
     * ### Transaction use
     *
     * When **1** element fails, the entire list is
     * considered failed, **the entire transaction rolls back**
     * and passes exception to caller.
     *
     * @param records list of [T] to insert to database
     * @return list of ROWID successfully inserted
     */
    @Insert
    @Throws(SQLException::class)
    fun insert( records: List<T> ): List<Long>

    /**
     * Attempt to write [record] into database.
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
     * @param record data intended to insert in to database
     * @return ROWID of this new record, -1 if error occurs
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore( record: T ): Long

    /**
     * Attempt to write the list of [T] to database.
     *
     * Return value of failed element is `-1`.
     *
     * ### Standalone use
     *
     * When an element fails to insert, operation ignores
     * it and moves on to next element.
     *
     * ### Transaction use
     *
     * When an element fails to insert, operation ignores
     * it and moves on to next element.
     * Transaction continues even when one or several elements
     * have failed to insert.
     *
     * @param records list of [T] to insert to database
     * @return list of ROWID, -1 for each failed element
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore( records: List<T> ): List<Long>

    /**
     * Attempt to write [record] into database.
     *
     * ### Standalone use
     *
     * When error occurs and [android.database.SQLException] is thrown,
     * data inside database will be replaced by provided [record].
     *
     * ### Transaction use
     *
     * When error occurs and [android.database.SQLException] is thrown,
     * data inside database will be replaced by provided [record]
     * and transaction continues.
     *
     * @param record data intended to insert in to database
     * @return ROWID of inserted record, or ROWID of replaced record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace( record: T ): Long

    /**
     * Attempt to write the list of [T] to database.
     *
     * ### Standalone use
     *
     * When an element fails to insert, it overrides existing
     * data with provided one.
     *
     * ### Transaction use
     *
     * When an element fails to insert, it overrides existing
     * data with provided one and transaction continues.
     *
     * @param records list of [T] to insert to database
     * @return list of ROWID of successfully inserted [T] or replaced [T]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace( records: List<T> ): List<Long>

    /**
     * Attempt to write [record] into database.
     *
     * If [record] exist (determined by its primary key),
     * existing record's columns will be replaced
     * by provided [record]' data.
     *
     * @param record data intended to insert in to database
     * @return ROWID of successfully modified record
     */
    @Upsert
    fun upsert( record: T ): Long

    /**
     * Attempt to write the list of [T] to database.
     *
     * If record exist (determined by its primary key),
     * existing record's columns will be replaced
     * by provided data.
     *
     * @param records list of [T] to insert to database
     * @return list of ROWID of successfully modified [T]
     */
    @Upsert
    fun upsert( records: List<T> ): List<Long>

    /**
     * Attempt to replace a record's data with provided [record].
     *
     * ### Standalone use
     *
     * When error occurs and [SQLException] is thrown,
     * the process is cancel and passes exception to caller.
     *
     * ### Transaction use
     *
     * When error occurs and [SQLException] is thrown,
     * **the entire transaction rolls back** and passes exception to caller.
     *
     *
     * @param record intended to update
     * @return number of rows affected by the this operation
     * @throws SQLException when there's a conflict
     */
    @Update
    @Throws(SQLException::class)
    fun update( record: T ): Int

    /**
     * Attempt to replace each record's data with the one provided in [records].
     *
     * ### Standalone use
     *
     * When **1** element fails, the entire list is
     * considered failed, database rolls back its operation,
     * and passes exception to caller.
     *
     * ### Transaction use
     *
     * When **1** element fails, the entire list is
     * considered failed, **the entire transaction rolls back**
     * and passes exception to caller.
     *
     *
     * @param records list of [T] to update
     * @return number of rows affected by the this operation
     * @throws SQLException when there's a conflict
     */
    @Update
    @Throws(SQLException::class)
    fun update( records: List<T> ): Int

    /**
     * Attempt to replace a record's data with provided [record].
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
     * @param record intended to update
     * @return number of rows affected by the this operation
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateIgnore( record: T ): Int

    /**
     * Attempt to replace each record's data with the one provided in records.
     *
     * ### Standalone use
     *
     * When an element fails to insert, operation ignores
     * it and moves on to next element.
     *
     * ### Transaction use
     *
     * When an element fails to insert, operation ignores
     * it and moves on to next element.
     * Transaction continues even when one or several elements
     * have failed to insert.
     *
     * @param records list of [T] to update
     * @return number of rows affected by the this operation
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateIgnore( records: List<T> ): Int

    /**
     * Attempt to replace a record's data with provided [record].
     *
     * ### Standalone use
     *
     * When error occurs and [android.database.SQLException] is thrown,
     * data inside database will be replaced by provided [record].
     *
     * ### Transaction use
     *
     * When error occurs and [android.database.SQLException] is thrown,
     * data inside database will be replaced by provided [record]
     * and transaction continues.
     *
     * @param record intended to update
     * @return number of rows affected by the this operation
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateReplace( record: T ): Int

    /**
     * Attempt to replace each record's data with the one provided in records.
     *
     * ### Standalone use
     *
     * When an element fails to insert, it overrides existing
     * data with provided one.
     *
     * ### Transaction use
     *
     * When an element fails to insert, it overrides existing
     * data with provided one and transaction continues.
     *
     * @param records list of [T] to update
     * @return number of rows affected by the this operation
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateReplace( records: List<T> ): Int

    /**
     * Attempt to remove a record from database.
     *
     * @param record data intended to delete from database
     * @return number of rows affected by the this operation
     */
    @Delete
    fun delete( record: T ): Int

    /**
     * Attempt to remove records from database.
     *
     * @param records list of [T] to delete from database
     * @return number of rows affected by the this operation
     */
    @Delete
    fun delete( records: List<T> ): Int

    /**
     * @param query SQLite compatible command to execute
     * @return number of rows this command affected
     */
    @RawQuery
    fun rawReturnsEffected( query: SupportSQLiteQuery ): Long

    /**
     * @return number of rows this table has
     */
    fun countAll(): Long {
        val query = SimpleSQLiteQuery( "SELECT COUNT(*) FROM $tableName" )
        return rawReturnsEffected( query )
    }

    /**
     * @return number of rows match [condition]
     */
    fun count( condition: String ): Long {
        val query = SimpleSQLiteQuery( "SELECT COUNT(*) FROM $tableName WHERE $condition" )
        return rawReturnsEffected( query )
    }

    /**
     * Wipe the table clean.
     *
     * @return number of rows deleted
     */
    fun deleteAll(): Long {
        val query = SimpleSQLiteQuery( "DELETE FROM $tableName" )
        return rawReturnsEffected( query )
    }

    /**
     * Clear records match [condition]
     *
     * @return number of rows deleted
     */
    fun delete(condition: String ): Long {
        val query = SimpleSQLiteQuery( "DELETE FROM $tableName WHERE $condition" )
        return rawReturnsEffected( query )
    }
}