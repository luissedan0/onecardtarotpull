package com.luissedan0.onecardtarotpull.data.model

/**
 * The result of a single tarot pull — a [TarotCard] plus whether it landed reversed.
 *
 * [displayName] is the human-readable label shown in the UI and persisted in the journal:
 * - Major Arcana upright  → "The Fool"
 * - Major Arcana reversed → "The Fool (Reversed)"
 * - Minor Arcana upright  → "Ace of Wands" / "3 of Cups" / "King of Pentacles"
 * - Minor Arcana reversed → "Ace of Wands (Reversed)"
 */
data class PulledCard(
    val card: TarotCard,
    val isReversed: Boolean
) {
    /** Human-readable card label used in the UI and journal entries. */
    val displayName: String get() {
        val baseName = if (card.isMajorArcana) {
            card.name
        } else {
            val numberLabel = when (card.number) {
                1    -> "Ace"
                11   -> "Page"
                12   -> "Knight"
                13   -> "Queen"
                14   -> "King"
                else -> card.number.toString()
            }
            "$numberLabel of ${card.suit!!.displayName}"
        }
        return if (isReversed) "$baseName (Reversed)" else baseName
    }

    /** Meaning text to show in the Details screen, accounting for orientation. */
    val meaning: String get() =
        if (isReversed) card.meaningReversed else card.meaningUpright
}
