package com.luissedan0.onecardtarotpull

import com.luissedan0.onecardtarotpull.data.model.TarotCard
import com.luissedan0.onecardtarotpull.data.model.TarotSuit
import com.luissedan0.onecardtarotpull.data.repository.LocalCardMeaningRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [LocalCardMeaningRepository] — 15.4.
 *
 * The injectable [deckProvider] lambda is overridden with a hand-crafted fixture deck,
 * keeping these tests in commonTest without the Compose Resources runtime.
 *
 * The full 78-card round-trip test (all card IDs resolve) runs in
 * [TarotDeckLoaderInstrumentedTest] where the runtime is available.
 */
class LocalCardMeaningRepositoryTest {

    // ── Fixture deck ──────────────────────────────────────────────────────────

    private val fixtureDeck = listOf(
        TarotCard(
            id = 0,
            name = "The Fool",
            isMajorArcana = true,
            suit = null,
            number = 0,
            keywords = listOf("beginnings", "freedom", "innocence"),
            keywordsReversed = listOf("reckless", "careless", "naive"),
            meaningUpright = "New beginnings, spontaneity.",
            meaningReversed = "Recklessness, risk."
        ),
        TarotCard(
            id = 22,
            name = "Ace of Wands",
            isMajorArcana = false,
            suit = TarotSuit.WANDS,
            number = 1,
            keywords = listOf("creation", "willpower", "inspiration"),
            keywordsReversed = listOf("delays", "boredom", "lack of energy"),
            meaningUpright = "Creative spark, new projects.",
            meaningReversed = "Blocked creativity, delays."
        ),
        TarotCard(
            id = 36,
            name = "Five of Cups",
            isMajorArcana = false,
            suit = TarotSuit.CUPS,
            number = 5,
            keywords = listOf("loss", "grief", "regret"),
            keywordsReversed = listOf("acceptance", "moving on", "healing"),
            meaningUpright = "Loss, sorrow, disappointment.",
            meaningReversed = "Moving on, acceptance."
        )
    )

    private fun repo() = LocalCardMeaningRepository(deckProvider = { fixtureDeck })

    // ── 15.4 tests ────────────────────────────────────────────────────────────

    @Test
    fun `getCardMeaning returns non-null for existing card id`() = runTest {
        val meaning = repo().getCardMeaning(0)
        assertNotNull(meaning)
    }

    @Test
    fun `getCardMeaning returns null for unknown card id`() = runTest {
        val meaning = repo().getCardMeaning(999)
        assertNull(meaning)
    }

    @Test
    fun `CardMeaning cardId matches requested id`() = runTest {
        val meaning = repo().getCardMeaning(22)
        assertNotNull(meaning)
        assertEquals(22, meaning.cardId)
    }

    @Test
    fun `CardMeaning name matches card name`() = runTest {
        val meaning = repo().getCardMeaning(0)
        assertNotNull(meaning)
        assertEquals("The Fool", meaning.name)
    }

    @Test
    fun `CardMeaning keywords match card keywords`() = runTest {
        val meaning = repo().getCardMeaning(0)
        assertNotNull(meaning)
        assertEquals(listOf("beginnings", "freedom", "innocence"), meaning.keywords)
    }

    @Test
    fun `CardMeaning keywordsReversed match card keywordsReversed`() = runTest {
        val meaning = repo().getCardMeaning(22)
        assertNotNull(meaning)
        assertEquals(listOf("delays", "boredom", "lack of energy"), meaning.keywordsReversed)
    }

    @Test
    fun `CardMeaning uprightMeaning is non-blank`() = runTest {
        val meaning = repo().getCardMeaning(36)
        assertNotNull(meaning)
        assertTrue(meaning.uprightMeaning.isNotBlank())
    }

    @Test
    fun `CardMeaning reversedMeaning is non-blank`() = runTest {
        val meaning = repo().getCardMeaning(36)
        assertNotNull(meaning)
        assertTrue(meaning.reversedMeaning.isNotBlank())
    }

    @Test
    fun `all fixture card IDs resolve to non-null meanings`() = runTest {
        val repo = repo()
        fixtureDeck.forEach { card ->
            assertNotNull(
                repo.getCardMeaning(card.id),
                "getCardMeaning(${card.id}) must not return null for '${card.name}'"
            )
        }
    }

    @Test
    fun `uprightMeaning and reversedMeaning differ`() = runTest {
        val meaning = repo().getCardMeaning(0)
        assertNotNull(meaning)
        assertTrue(
            meaning.uprightMeaning != meaning.reversedMeaning,
            "upright and reversed meanings should not be identical"
        )
    }
}
