package com.luissedan0.onecardtarotpull.domain.usecase

import com.luissedan0.onecardtarotpull.data.repository.SettingsRepository
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme

/**
 * Mutates persisted settings.
 * All operations are suspend because they write to DataStore on the IO dispatcher.
 */
class UpdateSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    /** Enables or disables automatic saving of pulled cards to the journal. */
    suspend fun setAutoSaveEnabled(enabled: Boolean) =
        settingsRepository.setAutoSaveEnabled(enabled)

    /**
     * Sets a custom card-back image path.
     * Pass `null` to clear the selection and revert to the default text label.
     */
    suspend fun setCustomCardBackPath(path: String?) =
        settingsRepository.setCustomCardBackPath(path)

    /**
     * Persists the selected palette by its [AppColorTheme.name].
     * The change is observed by [GetSettingsUseCase.colorTheme] subscribers immediately.
     */
    suspend fun setColorTheme(theme: AppColorTheme) =
        settingsRepository.setColorThemeName(theme.name)
}
