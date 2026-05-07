package com.luissedan0.onecardtarotpull.data.repository

import com.luissedan0.onecardtarotpull.data.model.JournalEntry
import com.luissedan0.onecardtarotpull.data.model.PulledCard
import kotlinx.coroutines.flow.Flow

/**
 * Contract for all journal-entry persistence operations.
 * Implementations may delegate to Room, an in-memory store (tests), or a remote backend.
 */
interface JournalRepository {
    /** Cold flow of all journal entries, ordered newest-first. Updates on every change. */
    fun getEntries(): Flow<List<JournalEntry>>

    /**
     * Persists a pulled card as a new journal entry, stamping the current timestamp.
     * Returns [Result.failure] if the underlying storage throws.
     */
    suspend fun saveEntry(pulledCard: PulledCard): Result<Unit>

    /**
     * Deletes the entry with the given [id].
     * Returns [Result.failure] if the entry does not exist or the storage throws.
     */
    suspend fun deleteEntry(id: Long): Result<Unit>
}
