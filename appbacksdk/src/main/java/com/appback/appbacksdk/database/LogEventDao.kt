package com.appback.appbacksdk.database

import androidx.room.*
import com.appback.appbacksdk.poko.log.LogEvent

/**
 * Data Access Object that will hold the operations for the [LogEvent] entity on the database
 * @author - sapardo10
 * @since 0.0.1
 */
@Dao
interface LogEventDao {
    @Query("SELECT * FROM log_event")
    fun getAll(): List<LogEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(logEvents: LogEvent)

    @Delete
    fun delete(logEvent: LogEvent)
}