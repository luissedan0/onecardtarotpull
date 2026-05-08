package com.luissedan0.onecardtarotpull

import com.luissedan0.onecardtarotpull.data.model.TarotCard
import com.luissedan0.onecardtarotpull.data.model.TarotSuit
import com.luissedan0.onecardtarotpull.data.repository.TarotDeckRepository
import com.luissedan0.onecardtarotpull.domain.usecase.PullCardUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for [PullCardUseCase].
 *
 * Uses a [FakeTarotDeckRepository] so the tests are pure commonTest — no Compose
 * Resources runtime needed.
 */
class PullCardUseCaseTest {

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun makeCard(id: Int, major: Boolean) = TarotCard(
        id = id,
        name = if (major) "Major $id" else "Minor $id",
        isMajorArcana = major,
        suit = if (major) null else TarotSuit.WANDS,
        number = if (major) id else 1,
        keywords = listOf("keyword"),
        keywordsReversed = listOf("keyword reversed"),
        meaningUpright = "upright",
        meaningReversed = "reversed"
    )

    /** Fake deck: 22 major (0-21) + 56 minor (22-77). */
    private val fakeDeck: List<TarotCard> =
        (0..21).map { makeCard(it, major = true) } +
        (22..77).map { makeCard(it, major = false) }

    private val fakeRepo = object : TarotDeckRepository {
        override suspend fun getAll() = fakeDeck
        override suspend fun getById(id: Int) = fakeDeck.firstOrNull { it.id == id }
    }

    private val useCase = PullCardUseCase(fakeRepo)

    // ── 15.1 tests ────────────────────────────────────────────────────────────

    @Test
    fun `pulled card always belongs to the deck`() = runTest {
        repeat(50) {
            val pulled = useCase()
            assertTrue(
                fakeDeck.any { it.id == pulled.card.id },
                "card id ${pulled.card.id} not found in deck"
            )
        }
    }

    @Test
    fun `pulled card id is within valid range 0 to 77`() = runTest {
        repeat(50) {
            val pulled = useCase()
            assertTrue(pulled.card.id in 0..77, "id ${pulled.card.id} out of range")
        }
    }

    @Test
    fun `reversed distribution is roughly 50 percent over 1000 pulls`() = runTest {
        val n = 1_000
        var reversedCount = 0
        repeat(n) {
            if (useCase().isReversed) reversedCount++
        }
        // Expect between 35% and 65% — well outside the 3-sigma range for a fair coin
        assertTrue(
            reversedCount in 350..650,
            "reversedCount=$reversedCount out of $n — distribution looks wrong"
        )
    }

    @Test
    fun `each card can eventually be pulled from a single-card deck`() = runTest {
        val singleCardDeck = listOf(makeCard(0, major = true))
        val repo = object : TarotDeckRepository {
            override suspend fun getAll() = singleCardDeck
            override suspend fun getById(id: Int) = singleCardDeck.firstOrNull { it.id == id }
        }
        val result = PullCardUseCase(repo)()
        assertTrue(result.card.id == 0)
    }

    @Test
    fun `empty deck throws IllegalArgumentException`() = runTest {
        val emptyRepo = object : TarotDeckRepository {
            override suspend fun getAll() = emptyList<TarotCard>()
            override suspend fun getById(id: Int) = null
        }
        assertFailsWith<IllegalArgumentException> {
            PullCardUseCase(emptyRepo)()
        }
    }
}
