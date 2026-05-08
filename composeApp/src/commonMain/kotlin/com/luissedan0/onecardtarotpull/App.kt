package com.luissedan0.onecardtarotpull

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luissedan0.onecardtarotpull.ui.navigation.AppNavHost
import com.luissedan0.onecardtarotpull.ui.settings.SettingsViewModel
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme
import com.luissedan0.onecardtarotpull.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * Root composable for the OneCardTarotPull app.
 *
 * Observes [SettingsViewModel.colorTheme] (backed by DataStore via Koin) so the
 * entire theme — Mystical or Inferno — responds to changes made in [SettingsScreen]
 * without requiring an app restart.
 *
 * ### Theme switching flow
 * ```
 * SettingsScreen → SettingsViewModel.setColorTheme(theme)
 *   → UpdateSettingsUseCase.setColorTheme()
 *   → SettingsRepository.setColorThemeName()
 *   → SettingsDataStore.setColorThemeName()       (DataStore write)
 *   → DataStore emits new value to all subscribers
 *   → App's SettingsViewModel.colorTheme emits    (may be a different VM instance)
 *   → AppTheme re-renders with new ColorScheme    (immediate, no restart)
 * ```
 *
 * Both the [App]-level and [SettingsScreen]-level [SettingsViewModel] instances
 * observe the same DataStore [kotlinx.coroutines.flow.Flow] so they remain in sync
 * even if Koin provides them as separate ViewModel instances.
 */
@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val colorTheme by settingsViewModel.colorTheme.collectAsStateWithLifecycle()
    AppTheme(colorTheme = colorTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost()
        }
    }
}
