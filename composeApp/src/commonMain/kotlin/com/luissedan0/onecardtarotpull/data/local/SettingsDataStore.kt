package com.luissedan0.onecardtarotpull.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

/**
 * Typed wrapper around [DataStore<Preferences>] that exposes the app's persisted settings
 * as observable [Flow]s and provides suspend functions for mutations.
 *
 * Inject as a singleton — see `AppModule.kt` (Phase 7).
 */
class SettingsDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val KEY_AUTO_SAVE = booleanPreferencesKey("auto_save_enabled")
        private val KEY_CARD_BACK_PATH = stringPreferencesKey("custom_card_back_path")

        /**
         * Stores the [AppColorTheme.name] of the selected palette.
         * Absent = use default (Mystical). Parsed back via [AppColorTheme.fromName].
         *
         * Added in Phase 9; wired to UI in Phase 12 (Settings theme selector).
         */
        private val KEY_COLOR_THEME = stringPreferencesKey("color_theme")
    }

    /**
     * Emits `true` when the "automatically save pulled cards" setting is enabled.
     * Defaults to `false` (disabled by default as per spec).
     */
    val autoSaveEnabled: Flow<Boolean> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[KEY_AUTO_SAVE] ?: false }

    /**
     * Emits the absolute file-system path of the custom card back image,
     * or `null` if none has been set.
     */
    val customCardBackPath: Flow<String?> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[KEY_CARD_BACK_PATH] }

    /** Persists the auto-save preference. */
    suspend fun setAutoSaveEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_AUTO_SAVE] = enabled }
    }

    /**
     * Persists the custom card back path.
     * Pass `null` to clear the custom image and revert to the default text label.
     */
    suspend fun setCustomCardBackPath(path: String?) {
        dataStore.edit { prefs ->
            if (path != null) {
                prefs[KEY_CARD_BACK_PATH] = path
            } else {
                prefs.remove(KEY_CARD_BACK_PATH)
            }
        }
    }

    /**
     * Emits the stored [AppColorTheme] name, or `null` when none has been persisted
     * (caller should default to [AppColorTheme.Mystical] via [AppColorTheme.fromName]).
     *
     * Wired to [SettingsViewModel] and [App] in Phase 12.
     */
    val colorThemeName: Flow<String?> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[KEY_COLOR_THEME] }

    /** Persists the selected palette by its [AppColorTheme.name]. */
    suspend fun setColorThemeName(name: String) {
        dataStore.edit { prefs -> prefs[KEY_COLOR_THEME] = name }
    }
}
