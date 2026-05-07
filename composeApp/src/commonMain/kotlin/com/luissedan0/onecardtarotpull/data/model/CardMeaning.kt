package com.luissedan0.onecardtarotpull.data.model

import kotlinx.serialization.Serializable

/**
 * The meaning of a tarot card, as served by [CardMeaningRepository].
 *
 * Both [LocalCardMeaningRepository] (bundled JSON) and [RemoteCardMeaningRepository] (future API)
 * produce instances of this class, keeping the Details screen source-agnostic.
 *
 * @param cardId           Matches [TarotCard.id].
 * @param name             Full display name of the card (e.g. "The High Priestess").
 * @param keywords         Short upright descriptors (e.g. ["intuition", "mystery", "wisdom"]).
 * @param keywordsReversed Short reversed descriptors.
 * @param uprightMeaning   Interpretation when the card is drawn upright.
 * @param reversedMeaning  Interpretation when the card is drawn reversed.
 */
@Serializable
data class CardMeaning(
    val cardId: Int,
    val name: String,
    val keywords: List<String>,
    val keywordsReversed: List<String>,
    val uprightMeaning: String,
    val reversedMeaning: String
)
