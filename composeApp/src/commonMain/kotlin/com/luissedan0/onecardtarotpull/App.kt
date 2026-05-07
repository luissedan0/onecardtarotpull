package com.luissedan0.onecardtarotpull

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.luissedan0.onecardtarotpull.ui.navigation.AppNavHost
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme
import com.luissedan0.onecardtarotpull.ui.theme.AppTheme

/**
 * Root composable for the OneCardTarotPull app.
 *
 * Delegates theming to [AppTheme] (Cinzel + Nunito fonts, Mystical/Inferno palettes)
 * and all navigation to [AppNavHost].
 *
 * ### Phase 12 — Theme switching
 * Replace the default [colorTheme] with an observed value from [SettingsViewModel]:
 * ```kotlin
 * val colorTheme by settingsViewModel.colorTheme.collectAsStateWithLifecycle()
 * App(colorTheme = colorTheme)
 * ```
 * The call site in [MainActivity] / [MainViewController] will provide the ViewModel.
 *
 * @param colorTheme Active palette. Defaults to [AppColorTheme.Mystical].
 */
@Composable
fun App(colorTheme: AppColorTheme = AppColorTheme.Mystical) {
    AppTheme(colorTheme = colorTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost()
        }
    }
}

@Preview
@Composable
private fun AppMysticalPreview() {
    App(colorTheme = AppColorTheme.Mystical)
}

@Preview
@Composable
private fun AppInfernoPreview() {
    App(colorTheme = AppColorTheme.Inferno)
}
