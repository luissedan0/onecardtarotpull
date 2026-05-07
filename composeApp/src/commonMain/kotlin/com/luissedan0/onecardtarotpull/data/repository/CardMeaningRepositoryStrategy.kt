package com.luissedan0.onecardtarotpull.data.repository

import com.luissedan0.onecardtarotpull.data.model.CardMeaning

/**
 * Selects between [LocalCardMeaningRepository] and [RemoteCardMeaningRepository] at runtime.
 *
 * - When [useRemote] is `false` (the default), the local bundled JSON is always used.
 * - When [useRemote] is `true`, the remote repository is tried first; if it returns `null`
 *   (e.g. no network, stub not yet implemented), the call falls back to local data.
 *
 * Flip [useRemote] via a Koin parameter or a build-config flag once the API is ready.
 */
class CardMeaningRepositoryStrategy(
    private val local: LocalCardMeaningRepository,
    private val remote: RemoteCardMeaningRepository,
    private val useRemote: Boolean = false
) : CardMeaningRepository {

    override suspend fun getCardMeaning(cardId: Int): CardMeaning? {
        if (!useRemote) return local.getCardMeaning(cardId)
        return remote.getCardMeaning(cardId) ?: local.getCardMeaning(cardId)
    }
}
