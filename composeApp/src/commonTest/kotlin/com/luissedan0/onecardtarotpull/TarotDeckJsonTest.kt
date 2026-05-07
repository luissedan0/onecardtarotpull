package com.luissedan0.onecardtarotpull

import com.luissedan0.onecardtarotpull.data.model.TarotCard
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests JSON parsing and structural invariants of the tarot deck using an embedded
 * fixture representing one Major Arcana and one Minor Arcana card.
 *
 * Full 78-card deck loading via [TarotDeckLoader] is covered by the Android
 * instrumented test `TarotDeckLoaderInstrumentedTest` (requires Compose Resources runtime).
 */
class TarotDeckJsonTest {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val fixtureJson = """
        [
          {
            "id": 0,
            "name": "The Fool",
            "isMajorArcana": true,
            "number": 0,
            "keywords": ["beginnings", "freedom", "innocence"],
            "keywordsReversed": ["reckless", "careless", "naive"],
            "meaningUpright": "The Fool represents new beginnings.",
            "meaningReversed": "The Fool reversed warns of recklessness."
          },
          {
            "id": 22,
            "name": "Ace of Wands",
            "isMajorArcana": false,
            "suit": "WANDS",
            "number": 1,
            "keywords": ["creation", "willpower", "inspiration"],
            "keywordsReversed": ["lack of energy", "delays", "boredom"],
            "meaningUpright": "The Ace of Wands represents new creative beginnings.",
            "meaningReversed": "The Ace of Wands reversed suggests delays."
          }
        ]
    """.trimIndent()

    private fun parseFixture(): List<TarotCard> =
        jsonParser.decodeFromString<List<TarotCard>>(fixtureJson)

    @Test
    fun `fixture parses to correct number of cards`() {
        assertEquals(2, parseFixture().size)
    }

    @Test
    fun `major arcana fields parse correctly`() {
        val fool = parseFixture().first { it.id == 0 }
        assertEquals("The Fool", fool.name)
        assertTrue(fool.isMajorArcana)
        assertEquals(0, fool.number)
        assertNull(fool.suit)
        assertEquals(listOf("beginnings", "freedom", "innocence"), fool.keywords)
        assertEquals(listOf("reckless", "careless", "naive"), fool.keywordsReversed)
        assertTrue(fool.meaningUpright.isNotBlank())
        assertTrue(fool.meaningReversed.isNotBlank())
    }

    @Test
    fun `minor arcana fields parse correctly`() {
        val ace = parseFixture().first { it.id == 22 }
        assertEquals("Ace of Wands", ace.name)
        assertTrue(!ace.isMajorArcana)
        assertEquals(1, ace.number)
        assertNotNull(ace.suit)
        assertEquals("WANDS", ace.suit!!.name)
        assertEquals(listOf("creation", "willpower", "inspiration"), ace.keywords)
        assertTrue(ace.meaningUpright.isNotBlank())
        assertTrue(ace.meaningReversed.isNotBlank())
    }

    @Test
    fun `all fixture card IDs are unique`() {
        val cards = parseFixture()
        val ids = cards.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `major arcana cards have no suit`() {
        val cards = parseFixture()
        cards.filter { it.isMajorArcana }.forEach { card ->
            assertNull(card.suit, "Major Arcana '${card.name}' must not have a suit")
        }
    }

    @Test
    fun `minor arcana cards all have a suit and number`() {
        val cards = parseFixture()
        cards.filter { !it.isMajorArcana }.forEach { card ->
            assertNotNull(card.suit, "'${card.name}' must have a suit")
            assertNotNull(card.number, "'${card.name}' must have a number")
        }
    }

    @Test
    fun `keywords and keywordsReversed are non-empty lists`() {
        val cards = parseFixture()
        cards.forEach { card ->
            assertTrue(card.keywords.isNotEmpty(), "'${card.name}' keywords must not be empty")
            assertTrue(card.keywordsReversed.isNotEmpty(), "'${card.name}' keywordsReversed must not be empty")
        }
    }
}
