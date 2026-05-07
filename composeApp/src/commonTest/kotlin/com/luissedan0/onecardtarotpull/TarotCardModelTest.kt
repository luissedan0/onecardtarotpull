package com.luissedan0.onecardtarotpull

import com.luissedan0.onecardtarotpull.data.model.PulledCard
import com.luissedan0.onecardtarotpull.data.model.TarotCard
import com.luissedan0.onecardtarotpull.data.model.TarotSuit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TarotCardModelTest {

    // ── TarotCard invariant tests ────────────────────────────────────────────

    private fun majorCard(id: Int = 0, number: Int = 0) = TarotCard(
        id = id,
        name = "Test Major",
        isMajorArcana = true,
        suit = null,
        number = number,
        keywords = listOf("keyword"),
        keywordsReversed = listOf("reversed keyword"),
        meaningUpright = "upright meaning",
        meaningReversed = "reversed meaning"
    )

    private fun minorCard(
        id: Int = 22,
        suit: TarotSuit = TarotSuit.WANDS,
        number: Int = 1
    ) = TarotCard(
        id = id,
        name = "Test Minor",
        isMajorArcana = false,
        suit = suit,
        number = number,
        keywords = listOf("keyword"),
        keywordsReversed = listOf("reversed keyword"),
        meaningUpright = "upright meaning",
        meaningReversed = "reversed meaning"
    )

    @Test
    fun `major arcana card is valid with no suit`() {
        val card = majorCard(number = 0)
        assertTrue(card.isMajorArcana)
        assertNull(card.suit)
        assertEquals(0, card.number)
    }

    @Test
    fun `major arcana with suit throws`() {
        assertFailsWith<IllegalArgumentException> {
            TarotCard(
                id = 0, name = "Bad", isMajorArcana = true,
                suit = TarotSuit.WANDS, number = 0,
                keywords = emptyList(), keywordsReversed = emptyList(),
                meaningUpright = "", meaningReversed = ""
            )
        }
    }

    @Test
    fun `major arcana with number out of range throws`() {
        assertFailsWith<IllegalArgumentException> { majorCard(number = 22) }
        assertFailsWith<IllegalArgumentException> { majorCard(number = -1) }
    }

    @Test
    fun `minor arcana without suit throws`() {
        assertFailsWith<IllegalArgumentException> {
            TarotCard(
                id = 22, name = "Bad", isMajorArcana = false,
                suit = null, number = 1,
                keywords = emptyList(), keywordsReversed = emptyList(),
                meaningUpright = "", meaningReversed = ""
            )
        }
    }

    @Test
    fun `minor arcana without number throws`() {
        assertFailsWith<IllegalArgumentException> {
            TarotCard(
                id = 22, name = "Bad", isMajorArcana = false,
                suit = TarotSuit.CUPS, number = null,
                keywords = emptyList(), keywordsReversed = emptyList(),
                meaningUpright = "", meaningReversed = ""
            )
        }
    }

    @Test
    fun `all four suits are valid for minor arcana`() {
        TarotSuit.entries.forEachIndexed { i, suit ->
            val card = minorCard(id = 22 + i, suit = suit, number = 1)
            assertEquals(suit, card.suit)
            assertFalse(card.isMajorArcana)
        }
    }

    // ── PulledCard.displayName tests ─────────────────────────────────────────

    @Test
    fun `major arcana upright displayName equals card name`() {
        val pulled = PulledCard(card = majorCard(), isReversed = false)
        assertEquals("Test Major", pulled.displayName)
    }

    @Test
    fun `major arcana reversed displayName appends reversed`() {
        val pulled = PulledCard(card = majorCard(), isReversed = true)
        assertEquals("Test Major (Reversed)", pulled.displayName)
    }

    @Test
    fun `minor arcana Ace displayName`() {
        val pulled = PulledCard(card = minorCard(suit = TarotSuit.WANDS, number = 1), isReversed = false)
        assertEquals("Ace of Wands", pulled.displayName)
    }

    @Test
    fun `minor arcana numeric displayName`() {
        assertEquals("3 of Cups",     PulledCard(minorCard(suit = TarotSuit.CUPS,     number = 3),  false).displayName)
        assertEquals("7 of Swords",   PulledCard(minorCard(suit = TarotSuit.SWORDS,   number = 7),  false).displayName)
        assertEquals("10 of Pentacles", PulledCard(minorCard(suit = TarotSuit.PENTACLES, number = 10), false).displayName)
    }

    @Test
    fun `minor arcana court card displayNames`() {
        assertEquals("Page of Cups",    PulledCard(minorCard(suit = TarotSuit.CUPS, number = 11), false).displayName)
        assertEquals("Knight of Cups",  PulledCard(minorCard(suit = TarotSuit.CUPS, number = 12), false).displayName)
        assertEquals("Queen of Cups",   PulledCard(minorCard(suit = TarotSuit.CUPS, number = 13), false).displayName)
        assertEquals("King of Cups",    PulledCard(minorCard(suit = TarotSuit.CUPS, number = 14), false).displayName)
    }

    @Test
    fun `minor arcana reversed appends reversed`() {
        val pulled = PulledCard(card = minorCard(suit = TarotSuit.SWORDS, number = 12), isReversed = true)
        assertEquals("Knight of Swords (Reversed)", pulled.displayName)
    }

    // ── PulledCard.meaning tests ─────────────────────────────────────────────

    @Test
    fun `upright pulled card returns uprightMeaning`() {
        val pulled = PulledCard(card = majorCard(), isReversed = false)
        assertEquals("upright meaning", pulled.meaning)
    }

    @Test
    fun `reversed pulled card returns reversedMeaning`() {
        val pulled = PulledCard(card = majorCard(), isReversed = true)
        assertEquals("reversed meaning", pulled.meaning)
    }

    // ── TarotSuit display names ───────────────────────────────────────────────

    @Test
    fun `suit display names are correct`() {
        assertEquals("Wands",     TarotSuit.WANDS.displayName)
        assertEquals("Cups",      TarotSuit.CUPS.displayName)
        assertEquals("Swords",    TarotSuit.SWORDS.displayName)
        assertEquals("Pentacles", TarotSuit.PENTACLES.displayName)
    }
}
