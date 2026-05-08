package com.luissedan0.onecardtarotpull.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luissedan0.onecardtarotpull.domain.usecase.GetSettingsUseCase
import com.luissedan0.onecardtarotpull.domain.usecase.UpdateSettingsUseCase
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for [SettingsScreen] and [App] (which observes [colorTheme] to recompose the
 * global [com.luissedan0.onecardtarotpull.ui.theme.AppTheme]).
 *
 * Because both [App] and [SettingsScreen] call `koinViewModel<SettingsViewModel>()` at
 * different hierarchy levels (Activity vs NavBackStackEntry), they may hold **separate
 * instances**. This is safe because both observe the same [DataStore][androidx.datastore.core.DataStore]
 * via the shared [GetSettingsUseCase] — DataStore propagates mutations to all subscribers
 * immediately via its [kotlinx.coroutines.flow.Flow], so they stay in sync.
 *
 * Injected by Koin — see `AppModule.kt`:
 * ```kotlin
 * viewModel { SettingsViewModel(get(), get()) }
 * ```
 *
 * @param getSettingsUseCase    Provides observable [Flow]s for all persisted settings.
 * @param updateSettingsUseCase Provides suspend mutators for all persisted settings.
 */
class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    /**
     * Whether the app automatically saves pulled cards to the journal.
     * Defaults to `false` (disabled by design, user must opt in).
     */
    val autoSaveEnabled: StateFlow<Boolean> = getSettingsUseCase.autoSaveEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    /**
     * Absolute path to the custom card-back image, or `null` when using the default.
     * Observed by [HomeViewModel] to pass into [CardBackView] via [HomeUiState].
     */
    val customCardBackPath: StateFlow<String?> = getSettingsUseCase.customCardBackPath
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /**
     * The active UI palette.
     * Defaults to [AppColorTheme.Mystical] on first launch.
     *
     * Observed in [App] to drive the root [com.luissedan0.onecardtarotpull.ui.theme.AppTheme]
     * so theme changes propagate instantly to all screens.
     */
    val colorTheme: StateFlow<AppColorTheme> = getSettingsUseCase.colorTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppColorTheme.Mystical
        )

    // ─── Mutators ────────────────────────────────────────────────────────────

    /**
     * Enables or disables automatic saving of pulled cards.
     * Launches a coroutine to write to [DataStore][androidx.datastore.core.DataStore].
     */
    fun setAutoSave(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.setAutoSaveEnabled(enabled)
        }
    }

    /**
     * Sets or clears the custom card-back image path.
     * Pass `null` to revert to the default card-back text label.
     */
    fun setCustomCardBack(path: String?) {
        viewModelScope.launch {
            updateSettingsUseCase.setCustomCardBackPath(path)
        }
    }

    /**
     * Persists the selected theme.
     * DataStore emits the update to all [colorTheme] subscribers (including [App])
     * within the same coroutine scope, causing the global theme to switch immediately.
     */
    fun setColorTheme(theme: AppColorTheme) {
        viewModelScope.launch {
            updateSettingsUseCase.setColorTheme(theme)
        }
    }
}
