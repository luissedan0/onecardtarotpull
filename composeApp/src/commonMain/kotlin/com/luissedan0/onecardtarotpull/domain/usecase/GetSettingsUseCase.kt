package com.luissedan0.onecardtarotpull.domain.usecase

import com.luissedan0.onecardtarotpull.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Exposes the app's settings as observable [Flow]s.
 * ViewModels collect these directly via [androidx.lifecycle.viewModelScope].
 */
class GetSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    /** Emits `true` when the "automatically save pulled cards" setting is on. */
    val autoSaveEnabled: Flow<Boolean>
        get() = settingsRepository.autoSaveEnabled

    /** Emits the custom card-back image path, or `null` if default. */
    val customCardBackPath: Flow<String?>
        get() = settingsRepository.customCardBackPath
}
