package com.luissedan0.onecardtarotpull.ui.home

import com.luissedan0.onecardtarotpull.data.model.PulledCard

/**
 * State machine for the pull-a-card interaction.
 *
 * Transitions:
 *  [Idle] ──(long press start)──▶ [Shuffling]
 *  [Shuffling] ──(finger lift)──▶ [Revealed]
 *  [Revealed] ──(long press on card)──▶ [Idle]
 */
sealed interface CardState {
    /** No card is in play. Shows the card back, ready for a pull. */
    data object Idle : CardState

    /** The user is holding down. The card back animates with a shuffle wobble. */
    data object Shuffling : CardState

    /**
     * A card has been drawn. [card] is shown (face-up) after the flip animation.
     * The "Learn more" and "Save to journal" action buttons become visible.
     */
    data class Revealed(val card: PulledCard) : CardState
}

/**
 * One-shot UI events emitted by [HomeViewModel] and consumed by [HomeScreen].
 * After consumption, the event is set to `null` in [HomeUiState] to prevent re-delivery.
 */
sealed interface SnackbarEvent {
    /** Entry was successfully written to the Room journal table. */
    data object SavedToJournal : SnackbarEvent

    /** Room write failed — a [Result.failure] was returned by [SaveJournalEntryUseCase]. */
    data object SaveError : SnackbarEvent
}

/**
 * Full UI state for [HomeScreen] / [PullCardTab].
 *
 * @param cardState         Current phase of the pull interaction.
 * @param snackbarEvent     Pending one-shot snackbar message, or `null` if none.
 * @param autoSaveEnabled   Whether pulled cards are automatically saved to the journal.
 * @param customCardBackPath File-system path for a custom card-back image, or `null` for the default.
 */
data class HomeUiState(
    val cardState: CardState = CardState.Idle,
    val snackbarEvent: SnackbarEvent? = null,
    val autoSaveEnabled: Boolean = false,
    val customCardBackPath: String? = null
)
