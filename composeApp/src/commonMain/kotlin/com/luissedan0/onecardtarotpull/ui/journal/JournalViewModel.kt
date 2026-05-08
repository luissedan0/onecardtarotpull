package com.luissedan0.onecardtarotpull.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luissedan0.onecardtarotpull.data.model.JournalEntry
import com.luissedan0.onecardtarotpull.domain.usecase.DeleteJournalEntryUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.GetJournalEntriesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Journal tab inside [HomeScreen].
 *
 * Exposes the Room-backed journal list as a [StateFlow] and handles deletions.
 * No UI state beyond the list is needed — swipe state is local to each row composable.
 *
 * Injected by Koin — see `AppModule.kt`:
 * ```kotlin
 * viewModel { JournalViewModel(get(), get()) }
 * ```
 *
 * @param getJournalEntriesUseCase  Returns a [Flow] of all entries (newest-first) from Room.
 * @param deleteJournalEntryUseCase Deletes an entry by its primary key.
 */
class JournalViewModel(
    getJournalEntriesUseCase: GetJournalEntriesUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase
) : ViewModel() {

    /**
     * Live list of journal entries.
     *
     * Uses [SharingStarted.WhileSubscribed] with a 5-second grace period so the
     * Room query is stopped quickly when the tab is in the background, but
     * resubscribes instantly on tab-switch without emitting a stale empty list.
     */
    val entries: StateFlow<List<JournalEntry>> = getJournalEntriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /**
     * Deletes the entry with [id] from the Room database.
     * Called from the swipe-to-delete confirmation in [JournalScreen].
     * Errors are silently swallowed — the Flow will not update if Room throws,
     * which effectively bounces the swipe back.
     */
    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            deleteJournalEntryUseCase(id)
        }
    }
}
