package com.luissedan0.onecardtarotpull.domain.usecase

import com.luissedan0.onecardtarotpull.data.model.CardMeaning
import com.luissedan0.onecardtarotpull.data.repository.CardMeaningRepository

/**
 * Retrieves the [CardMeaning] for a given card ID.
 * Delegates source selection (local vs remote) to [CardMeaningRepository].
 * Returns `null` if no data is available for the given ID.
 */
class GetCardMeaningUseCase(
    private val cardMeaningRepository: CardMeaningRepository
) {
    suspend operator fun invoke(cardId: Int): CardMeaning? =
        cardMeaningRepository.getCardMeaning(cardId)
}
