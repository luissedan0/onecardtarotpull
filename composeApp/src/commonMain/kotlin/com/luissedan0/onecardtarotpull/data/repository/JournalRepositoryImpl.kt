package com.luissedan0.onecardtarotpull.data.repository

import com.luissedan0.onecardtarotpull.data.local.JournalDao
import com.luissedan0.onecardtarotpull.data.model.JournalEntry
import com.luissedan0.onecardtarotpull.data.model.PulledCard
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow

class JournalRepositoryImpl(
    private val dao: JournalDao
) : JournalRepository {

    override fun getEntries(): Flow<List<JournalEntry>> = dao.getAll()

    override suspend fun saveEntry(pulledCard: PulledCard): Result<Unit> = runCatching {
        dao.insert(
            JournalEntry(
                cardId = pulledCard.card.id,
                cardDisplayName = pulledCard.displayName,
                isReversed = pulledCard.isReversed,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    override suspend fun deleteEntry(id: Long): Result<Unit> = runCatching {
        dao.deleteById(id)
    }
}
