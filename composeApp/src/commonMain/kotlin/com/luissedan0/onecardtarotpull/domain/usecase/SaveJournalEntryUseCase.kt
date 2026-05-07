package com.luissedan0.onecardtarotpull.domain.usecase

import com.luissedan0.onecardtarotpull.data.model.PulledCard
import com.luissedan0.onecardtarotpull.data.repository.JournalRepository

/**
 * Persists a [PulledCard] as a journal entry with the current timestamp.
 * Returns [Result.success] on success or [Result.failure] if the storage layer throws.
 */
class SaveJournalEntryUseCase(
    private val journalRepository: JournalRepository
) {
    suspend operator fun invoke(pulledCard: PulledCard): Result<Unit> =
        journalRepository.saveEntry(pulledCard)
}
