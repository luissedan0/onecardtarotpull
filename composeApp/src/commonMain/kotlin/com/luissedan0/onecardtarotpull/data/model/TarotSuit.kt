package com.luissedan0.onecardtarotpull.data.model

import kotlinx.serialization.Serializable

/**
 * Represents one of the four suits of the Minor Arcana.
 */
@Serializable
enum class TarotSuit(val displayName: String) {
    WANDS("Wands"),
    CUPS("Cups"),
    SWORDS("Swords"),
    PENTACLES("Pentacles")
}
