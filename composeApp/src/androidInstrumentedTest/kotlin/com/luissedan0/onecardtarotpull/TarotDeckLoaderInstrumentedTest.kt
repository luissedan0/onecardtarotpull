package com.luissedan0.onecardtarotpull

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.luissedan0.onecardtarotpull.data.local.TarotDeckLoader
import com.luissedan0.onecardtarotpull.data.model.TarotSuit
import com.luissedan0.onecardtarotpull.data.repository.LocalCardMeaningRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Instrumented tests for [TarotDeckLoader] and [LocalCardMeaningRepository] — 15.3 & 15.4.
 *
 * These run on an Android device/emulator because [Res.readBytes] requires the full
 * Compose Resources runtime that is only initialised inside an Android app context.
 *
 * Run with: ./gradlew :composeApp:connectedAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class TarotDeckLoaderInstrumentedTest {

    @After
    fun clearCache() {
        TarotDeckLoader.clearCache()
    }

    // ── 15.3 — TarotDeckLoader ────────────────────────────────────────────────

    @Test
    fun deck_has_exactly_78_cards() = runTest {
        val deck = TarotDeckLoader.load()
        assertEquals(78, deck.size, "Expected exactly 78 cards in the deck")
    }

    @Test
    fun deck_has_22_major_arcana_cards() = runTest {
        val deck = TarotDeckLoader.load()
        assertEquals(22, deck.count { it.isMajorArcana }, "Expected 22 Major Arcana")
    }

    @Test
    fun deck_has_56_minor_arcana_cards() = runTest {
        val deck = TarotDeckLoader.load()
        assertEquals(56, deck.count { !it.isMajorArcana }, "Expected 56 Minor Arcana")
    }

    @Test
    fun each_suit_has_exactly_14_cards() = runTest {
        val deck = TarotDeckLoader.load()
        TarotSuit.entries.forEach { suit ->
            assertEquals(
                14,
                deck.count { it.suit == suit },
                "Suit ${suit.displayName} must have exactly 14 cards"
            )
        }
    }

    @Test
    fun all_card_ids_are_unique() = runTest {
        val deck = TarotDeckLoader.load()
        val ids = deck.map { it.id }
        assertEquals(ids.size, ids.distinct().size, "All card IDs must be unique")
    }

    @Test
    fun card_ids_span_0_to_77() = runTest {
        val deck = TarotDeckLoader.load()
        val ids = deck.map { it.id }
        assertEquals(0, ids.min(), "Minimum card ID should be 0")
        assertEquals(77, ids.max(), "Maximum card ID should be 77")
    }

    @Test
    fun all_cards_have_non_empty_meanings() = runTest {
        val deck = TarotDeckLoader.load()
        deck.forEach { card ->
            assertTrue(card.meaningUpright.isNotBlank(), "${card.name} has blank upright meaning")
            assertTrue(card.meaningReversed.isNotBlank(), "${card.name} has blank reversed meaning")
        }
    }

    @Test
    fun all_cards_have_non_empty_keywords() = runTest {
        val deck = TarotDeckLoader.load()
        deck.forEach { card ->
            assertTrue(card.keywords.isNotEmpty(), "${card.name} has no keywords")
            assertTrue(card.keywordsReversed.isNotEmpty(), "${card.name} has no keywordsReversed")
        }
    }

    @Test
    fun consecutive_loads_return_cached_result() = runTest {
        val first = TarotDeckLoader.load()
        val second = TarotDeckLoader.load()
        // Reference equality — same list object from cache
        assertTrue(first === second, "Second load should return the cached list instance")
    }

    @Test
    fun clear_cache_forces_fresh_parse_on_next_load() = runTest {
        val first = TarotDeckLoader.load()
        TarotDeckLoader.clearCache()
        val second = TarotDeckLoader.load()
        // Different object, but same content
        assertEquals(first.size, second.size)
        assertEquals(first.map { it.id }.sorted(), second.map { it.id }.sorted())
    }

    // ── 15.4 — LocalCardMeaningRepository (full 78-card round-trip) ───────────

    @Test
    fun every_card_id_resolves_to_a_non_null_meaning() = runTest {
        val deck = TarotDeckLoader.load()
        // Default deckProvider uses TarotDeckLoader.load() — cache already warm from above
        val repo = LocalCardMeaningRepository()
        deck.forEach { card ->
            val meaning = repo.getCardMeaning(card.id)
            assertNotNull(meaning, "getCardMeaning(${card.id}) returned null for '${card.name}'")
        }
    }

    @Test
    fun card_meaning_fields_are_non_blank_for_every_card() = runTest {
        val deck = TarotDeckLoader.load()
        val repo = LocalCardMeaningRepository()
        deck.forEach { card ->
            val meaning = repo.getCardMeaning(card.id) ?: return@forEach
            assertTrue(meaning.name.isNotBlank(), "name blank for card ${card.id}")
            assertTrue(meaning.uprightMeaning.isNotBlank(), "upright blank for card ${card.id}")
            assertTrue(meaning.reversedMeaning.isNotBlank(), "reversed blank for card ${card.id}")
            assertTrue(meaning.keywords.isNotEmpty(), "keywords empty for card ${card.id}")
            assertTrue(meaning.keywordsReversed.isNotEmpty(), "keywordsReversed empty for card ${card.id}")
        }
    }
}
