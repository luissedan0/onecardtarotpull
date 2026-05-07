package com.luissedan0.onecardtarotpull.data.repository

import com.luissedan0.onecardtarotpull.data.model.CardMeaning

/**
 * Stub implementation that will call a remote API once an endpoint is chosen.
 *
 * Currently returns `null` for all requests so the app gracefully falls back to
 * [LocalCardMeaningRepository] via [CardMeaningRepositoryStrategy].
 *
 * TODO (Phase 4.5): Replace with Ktor HttpClient call when an API is selected.
 *   Suggested endpoint shape: GET /cards/{id} → { cardId, name, keywords, ... }
 */
class RemoteCardMeaningRepository : CardMeaningRepository {

    override suspend fun getCardMeaning(cardId: Int): CardMeaning? {
        // Not yet implemented — return null so the strategy falls back to local data.
        return null
    }
}
