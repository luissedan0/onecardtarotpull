package com.luissedan0.onecardtarotpull.data.repository

import com.luissedan0.onecardtarotpull.data.local.SettingsDataStore
import kotlinx.coroutines.flow.Flow

/**
 * Contract for reading and writing user-facing settings.
 * Backed by [SettingsDataStore] (DataStore Preferences) in production.
 */
interface SettingsRepository {
    /** Emits `true` when auto-save is enabled. Defaults to `false`. */
    val autoSaveEnabled: Flow<Boolean>

    /** Emits the file-system path of a custom card-back image, or `null` if none is set. */
    val customCardBackPath: Flow<String?>

    /**
     * Emits the persisted [AppColorTheme] name string, or `null` when no theme has been
     * explicitly saved (callers should default to [AppColorTheme.Mystical]).
     * Wired to [GetSettingsUseCase.colorTheme] as a mapped [AppColorTheme] flow.
     */
    val colorThemeName: Flow<String?>

    suspend fun setAutoSaveEnabled(enabled: Boolean)
    suspend fun setCustomCardBackPath(path: String?)

    /** Persists the palette selection. The enum [name] is stored, not the [displayName]. */
    suspend fun setColorThemeName(name: String)
}

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override val autoSaveEnabled: Flow<Boolean>
        get() = dataStore.autoSaveEnabled

    override val customCardBackPath: Flow<String?>
        get() = dataStore.customCardBackPath

    override val colorThemeName: Flow<String?>
        get() = dataStore.colorThemeName

    override suspend fun setAutoSaveEnabled(enabled: Boolean) =
        dataStore.setAutoSaveEnabled(enabled)

    override suspend fun setCustomCardBackPath(path: String?) =
        dataStore.setCustomCardBackPath(path)

    override suspend fun setColorThemeName(name: String) =
        dataStore.setColorThemeName(name)
}
