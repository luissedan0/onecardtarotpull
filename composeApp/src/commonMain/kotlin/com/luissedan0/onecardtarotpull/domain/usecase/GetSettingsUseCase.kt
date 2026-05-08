package com.luissedan0.onecardtarotpull.domain.usecase

import com.luissedan0.onecardtarotpull.data.repository.SettingsRepository
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    /**
     * Emits the active [AppColorTheme], defaulting to [AppColorTheme.Mystical]
     * when nothing has been stored yet.
     *
     * Maps [SettingsRepository.colorThemeName] using [AppColorTheme.fromName].
     */
    val colorTheme: Flow<AppColorTheme>
        get() = settingsRepository.colorThemeName.map { AppColorTheme.fromName(it) }
}
