package com.appback.appbacksdk.database


import androidx.lifecycle.LiveData
import androidx.room.*
import com.appback.appbacksdk.poko.toggle.Toggle

/**
 * Data Access Object that will hold the operations for the [Toggle] entity on the database
 * @author - sapardo10
 * @since 0.0.1
 */
@Dao
interface ToggleDao {
    @Query("SELECT * FROM toggle")
    fun getAll(): List<Toggle>

    @Query("SELECT * FROM toggle where `key` like '%' || :router || '%'")
    fun getAllByRouter(router: String): List<Toggle>

    @Query("SELECT * FROM toggle where  `key` = :key LIMIT 1")
    suspend fun findToggleAsync(key: String): Toggle

    @Query("SELECT * FROM toggle where `key` = :key LIMIT 1")
    fun findToogle(key: String): LiveData<Toggle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(translations: List<Toggle>)

    @Delete
    fun delete(toggle: Toggle)
}