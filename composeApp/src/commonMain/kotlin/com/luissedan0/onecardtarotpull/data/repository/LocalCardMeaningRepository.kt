package com.luissedan0.onecardtarotpull.data.repository

import com.luissedan0.onecardtarotpull.data.local.TarotDeckLoader
import com.luissedan0.onecardtarotpull.data.model.CardMeaning

/**
 * Reads card meanings from the bundled `tarot_deck.json` resource via [TarotDeckLoader].
 *
 * The loader caches the parsed deck in memory after the first call, so repeated lookups
 * in the same app session are O(n) list scans on the in-memory list — acceptable for 78 cards.
 */
class LocalCardMeaningRepository : CardMeaningRepository {

    override suspend fun getCardMeaning(cardId: Int): CardMeaning? {
        val card = TarotDeckLoader.load().firstOrNull { it.id == cardId } ?: return null
        return CardMeaning(
            cardId = card.id,
            name = card.name,
            keywords = card.keywords,
            keywordsReversed = card.keywordsReversed,
            uprightMeaning = card.meaningUpright,
            reversedMeaning = card.meaningReversed
        )
    }
}
