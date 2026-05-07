package com.luissedan0.onecardtarotpull.data.repository

import com.luissedan0.onecardtarotpull.data.model.CardMeaning

/**
 * Source-agnostic contract for retrieving card meaning data.
 *
 * Current implementations:
 * - [LocalCardMeaningRepository] — reads from the bundled `tarot_deck.json` resource.
 * - [RemoteCardMeaningRepository] — stub Ktor implementation (returns null until an API is chosen).
 *
 * The active implementation is selected by [CardMeaningRepositoryStrategy].
 */
interface CardMeaningRepository {
    /**
     * Returns the [CardMeaning] for the card with the given [cardId], or `null` if not found.
     * Suspend because both local (file I/O) and remote (network) operations are async.
     */
    suspend fun getCardMeaning(cardId: Int): CardMeaning?
}
