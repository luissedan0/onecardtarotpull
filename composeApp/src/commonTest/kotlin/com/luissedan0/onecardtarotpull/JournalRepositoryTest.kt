package com.luissedan0.onecardtarotpull

import com.luissedan0.onecardtarotpull.data.local.JournalDao
import com.luissedan0.onecardtarotpull.data.model.JournalEntry
import com.luissedan0.onecardtarotpull.data.model.PulledCard
import com.luissedan0.onecardtarotpull.data.model.TarotCard
import com.luissedan0.onecardtarotpull.data.model.TarotSuit
import com.luissedan0.onecardtarotpull.data.repository.JournalRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [JournalRepositoryImpl] — 15.2.
 *
 * Uses [FakeJournalDao] (in-memory MutableList) to avoid Room and the Compose Resources
 * runtime entirely, keeping these tests in commonTest.
 */
class JournalRepositoryTest {

    // ── Fake DAO ──────────────────────────────────────────────────────────────

    /**
     * In-memory DAO backed by a MutableList.
     * [_entries] is a [MutableStateFlow] so [getAll] reflects mutations immediately.
     */
    private class FakeJournalDao : JournalDao {
        private val store = mutableListOf<JournalEntry>()
        private val flow = MutableStateFlow<List<JournalEntry>>(emptyList())
        private var nextId = 1L

        override fun getAll(): Flow<List<JournalEntry>> = flow

        override suspend fun insert(entry: JournalEntry) {
            val withId = entry.copy(id = nextId++)
            store.add(withId)
            flow.value = store.toList()
        }

        override suspend fun deleteById(id: Long) {
            store.removeAll { it.id == id }
            flow.value = store.toList()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun makeMinorCard(id: Int = 22) = TarotCard(
        id = id,
        name = "Ace of Wands",
        isMajorArcana = false,
        suit = TarotSuit.WANDS,
        number = 1,
        keywords = listOf("creation"),
        keywordsReversed = listOf("blocks"),
        meaningUpright = "new beginnings",
        meaningReversed = "delays"
    )

    private fun makePulledCard(cardId: Int = 22, reversed: Boolean = false) =
        PulledCard(card = makeMinorCard(cardId), isReversed = reversed)

    // ── 15.2 tests ────────────────────────────────────────────────────────────

    @Test
    fun `getEntries emits empty list initially`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())
        val entries = repo.getEntries().first()
        assertTrue(entries.isEmpty())
    }

    @Test
    fun `saveEntry inserts and getEntries emits the new entry`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())
        val result = repo.saveEntry(makePulledCard())

        assertTrue(result.isSuccess)
        val entries = repo.getEntries().first()
        assertEquals(1, entries.size)
        assertEquals("Ace of Wands", entries.first().cardDisplayName)
        assertEquals(false, entries.first().isReversed)
    }

    @Test
    fun `saveEntry with reversed card stores isReversed = true`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())
        repo.saveEntry(makePulledCard(reversed = true))

        val entry = repo.getEntries().first().first()
        assertTrue(entry.isReversed)
    }

    @Test
    fun `saveEntry stores the cardId from the pulled card`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())
        repo.saveEntry(makePulledCard(cardId = 42))

        val entry = repo.getEntries().first().first()
        assertEquals(42, entry.cardId)
    }

    @Test
    fun `saveEntry persists a non-zero timestamp`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())
        repo.saveEntry(makePulledCard())

        val entry = repo.getEntries().first().first()
        assertTrue(entry.timestamp > 0L, "timestamp must be a positive epoch millis value")
    }

    @Test
    fun `deleteEntry removes the entry`() = runTest {
        val dao = FakeJournalDao()
        val repo = JournalRepositoryImpl(dao)

        repo.saveEntry(makePulledCard())
        val entryId = repo.getEntries().first().first().id

        val deleteResult = repo.deleteEntry(entryId)
        assertTrue(deleteResult.isSuccess)
        assertTrue(repo.getEntries().first().isEmpty())
    }

    @Test
    fun `deleteEntry on non-existent id succeeds silently`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())
        val result = repo.deleteEntry(999L)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `insert-fetch-delete round trip with multiple entries`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())

        repo.saveEntry(makePulledCard(cardId = 0))
        repo.saveEntry(makePulledCard(cardId = 5))
        repo.saveEntry(makePulledCard(cardId = 10))

        val before = repo.getEntries().first()
        assertEquals(3, before.size)

        val idToDelete = before[1].id
        repo.deleteEntry(idToDelete)

        val after = repo.getEntries().first()
        assertEquals(2, after.size)
        assertTrue(after.none { it.id == idToDelete })
    }

    @Test
    fun `displayName snapshot in journal entry matches the pulled card`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())
        val pulled = makePulledCard(reversed = false)
        repo.saveEntry(pulled)

        val entry = repo.getEntries().first().first()
        assertEquals(pulled.displayName, entry.cardDisplayName)
    }

    @Test
    fun `reversed pulled card snapshot contains Reversed in the displayName`() = runTest {
        val repo = JournalRepositoryImpl(FakeJournalDao())
        repo.saveEntry(makePulledCard(reversed = true))

        val entry = repo.getEntries().first().first()
        assertNotNull(entry.cardDisplayName.contains("Reversed", ignoreCase = true))
    }
}
