package com.luissedan0.onecardtarotpull.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.luissedan0.onecardtarotpull.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for journal entries.
 * All mutations are suspend functions; [getAll] returns a cold [Flow] that Room keeps
 * up-to-date whenever the underlying table changes.
 */
@Dao
interface JournalDao {

    /** Emits the full list whenever any row is inserted or deleted, newest first. */
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAll(): Flow<List<JournalEntry>>

    /** Inserts a new entry. IGNORE strategy is a safety net — IDs are auto-generated. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: JournalEntry)

    /** Deletes a single entry by its primary key. */
    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
