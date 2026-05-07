package com.luissedan0.onecardtarotpull.domain.usecase

import com.luissedan0.onecardtarotpull.data.model.PulledCard
import com.luissedan0.onecardtarotpull.data.repository.TarotDeckRepository
import kotlin.random.Random

/**
 * Draws one random card from the full 78-card deck.
 * Each card has a 50% chance of being reversed.
 *
 * This is the core domain action of the app.
 */
class PullCardUseCase(
    private val deckRepository: TarotDeckRepository
) {
    suspend operator fun invoke(): PulledCard {
        val deck = deckRepository.getAll()
        require(deck.isNotEmpty()) { "Tarot deck must not be empty" }
        return PulledCard(
            card = deck.random(Random.Default),
            isReversed = Random.nextBoolean()
        )
    }
}
