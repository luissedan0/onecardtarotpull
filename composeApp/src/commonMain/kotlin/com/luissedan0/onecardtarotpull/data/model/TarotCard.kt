package com.luissedan0.onecardtarotpull.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a single card in a standard 78-card tarot deck.
 *
 * - Major Arcana (22 cards): [isMajorArcana] = true, [suit] = null, [number] 0–21.
 * - Minor Arcana (56 cards): [isMajorArcana] = false, [suit] is one of [TarotSuit],
 *   [number] 1–14 (1 = Ace, 11 = Page, 12 = Knight, 13 = Queen, 14 = King).
 *
 * Card meanings are stored here so they can be deserialized from the bundled JSON resource
 * (`tarot_deck.json`) and served by [LocalCardMeaningRepository].
 *
 * [keywords] and [keywordsReversed] are short comma-separated descriptors shown as a
 * quick-glance summary in the Details screen (sourced from labyrinthos.co).
 */
@Serializable
data class TarotCard(
    val id: Int,
    val name: String,
    val isMajorArcana: Boolean,
    val suit: TarotSuit? = null,
    val number: Int? = null,
    val keywords: List<String>,
    val keywordsReversed: List<String>,
    val meaningUpright: String,
    val meaningReversed: String
) {
    init {
        require(isMajorArcana || suit != null) {
            "Minor Arcana card (id=$id) must have a suit"
        }
        require(isMajorArcana || number != null) {
            "Minor Arcana card (id=$id) must have a number"
        }
        require(!isMajorArcana || (number != null && number in 0..21)) {
            "Major Arcana card (id=$id) must have a number in 0..21"
        }
        require(!isMajorArcana || suit == null) {
            "Major Arcana card (id=$id) must not have a suit"
        }
    }
}
