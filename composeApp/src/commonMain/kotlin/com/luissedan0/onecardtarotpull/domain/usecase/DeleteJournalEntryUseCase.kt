package com.luissedan0.onecardtarotpull.domain.usecase

import com.luissedan0.onecardtarotpull.data.repository.JournalRepository

/**
 * Deletes the journal entry with the given [id].
 * Returns [Result.failure] if the entry does not exist or the storage layer throws.
 */
class DeleteJournalEntryUseCase(
    private val journalRepository: JournalRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        journalRepository.deleteEntry(id)
}
