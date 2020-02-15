package com.appback.appbacksdk.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appback.appbacksdk.poko.log.LogEvent
import com.appback.appbacksdk.poko.toggle.Toggle
import com.appback.appbacksdk.poko.transalation.Translation

/**
 * Database that will hold all of the local info that can be stored
 * @author - sapardo10
 * @since 0.0.1
 */
@Database(entities = [Translation::class, Toggle::class, LogEvent::class], version = 1)
abstract class AppbackDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao
    abstract fun toggleDao(): ToggleDao
    abstract fun logEventDao(): LogEventDao
}