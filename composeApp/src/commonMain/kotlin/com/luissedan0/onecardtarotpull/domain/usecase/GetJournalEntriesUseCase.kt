package com.luissedan0.onecardtarotpull.domain.usecase

import com.luissedan0.onecardtarotpull.data.model.JournalEntry
import com.luissedan0.onecardtarotpull.data.repository.JournalRepository
import kotlinx.coroutines.flow.Flow

/**
 * Returns a cold [Flow] of all journal entries, sorted newest-first.
 * The flow updates automatically whenever the underlying Room table changes.
 */
class GetJournalEntriesUseCase(
    private val journalRepository: JournalRepository
) {
    operator fun invoke(): Flow<List<JournalEntry>> =
        journalRepository.getEntries()
}
