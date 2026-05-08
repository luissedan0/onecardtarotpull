package com.luissedan0.onecardtarotpull.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luissedan0.onecardtarotpull.domain.usecase.GetSettingsUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.PullCardUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.SaveJournalEntryUseCase
import com.luissedan0.onecardtarotpull.platform.HapticFeedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for [HomeScreen] and the [PullCardTab].
 *
 * Manages the [CardState] machine, auto-save observation, haptic feedback, and
 * one-shot snackbar events. All business logic is delegated to the use-case layer.
 *
 * Injected by Koin — see `AppModule.kt`:
 * ```kotlin
 * viewModel { HomeViewModel(get(), get(), get(), get()) }
 * ```
 *
 * @param pullCardUseCase         Draws a random [PulledCard] from the 78-card deck.
 * @param saveJournalEntryUseCase Persists the current card to the Room journal table.
 * @param getSettingsUseCase      Observes [autoSaveEnabled] and [customCardBackPath].
 * @param hapticFeedback          Platform haptic feedback (vibrate on long-press start).
 */
class HomeViewModel(
    private val pullCardUseCase: PullCardUseCase,
    private val saveJournalEntryUseCase: SaveJournalEntryUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val hapticFeedback: HapticFeedback
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Observe settings and propagate into HomeUiState reactively.
        viewModelScope.launch {
            combine(
                getSettingsUseCase.autoSaveEnabled,
                getSettingsUseCase.customCardBackPath
            ) { autoSave, cardBackPath ->
                autoSave to cardBackPath
            }.collect { (autoSave, cardBackPath) ->
                _uiState.update { it.copy(autoSaveEnabled = autoSave, customCardBackPath = cardBackPath) }
            }
        }
    }

    // ─── Long-press gesture handlers ────────────────────────────────────────

    /**
     * Called when the long-press threshold is crossed on the card back.
     * Emits haptic feedback and transitions to [CardState.Shuffling].
     */
    fun onLongPressStart() {
        hapticFeedback.performHeavyClick()
        _uiState.update { it.copy(cardState = CardState.Shuffling) }
    }

    /**
     * Called when the finger is released after a successful long press.
     * Pulls a random card, transitions to [CardState.Revealed], and auto-saves
     * if the setting is enabled.
     */
    fun onLongPressEnd() {
        viewModelScope.launch {
            val pulled = pullCardUseCase()
            _uiState.update { it.copy(cardState = CardState.Revealed(pulled)) }

            // Auto-save: write the entry silently; errors are swallowed here —
            // the user can always tap "Save to journal" manually.
            if (_uiState.value.autoSaveEnabled) {
                saveJournalEntryUseCase(pulled)
            }
        }
    }

    /**
     * Called when the user long-presses the revealed card face.
     * Flips the card back and resets to [CardState.Idle].
     */
    fun onCardLongPress() {
        _uiState.update { it.copy(cardState = CardState.Idle) }
    }

    // ─── Manual journal save ─────────────────────────────────────────────────

    /**
     * Saves the currently revealed card to the journal.
     * Emits [SnackbarEvent.SavedToJournal] on success or [SnackbarEvent.SaveError] on failure.
     *
     * No-op if the current state is not [CardState.Revealed].
     */
    fun saveToJournal() {
        val state = _uiState.value.cardState
        if (state !is CardState.Revealed) return

        viewModelScope.launch {
            val result = saveJournalEntryUseCase(state.card)
            val event = if (result.isSuccess) {
                SnackbarEvent.SavedToJournal
            } else {
                SnackbarEvent.SaveError
            }
            _uiState.update { it.copy(snackbarEvent = event) }
        }
    }

    /** Clears the pending [SnackbarEvent] after it has been shown. */
    fun consumeSnackbarEvent() {
        _uiState.update { it.copy(snackbarEvent = null) }
    }
}
