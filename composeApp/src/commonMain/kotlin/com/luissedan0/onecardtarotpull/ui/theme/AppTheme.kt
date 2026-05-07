package com.luissedan0.onecardtarotpull.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * [CompositionLocal] that provides the currently active [AppColorTheme] to any descendant
 * composable without prop drilling.
 *
 * Reads: `LocalAppColorTheme.current`
 * Set by: [AppTheme]
 *
 * Useful for composables that need to branch on the theme (e.g. card-back tint, illustration
 * color logic) without accessing [MaterialTheme.colorScheme] directly.
 */
val LocalAppColorTheme = staticCompositionLocalOf { AppColorTheme.Mystical }

/**
 * Root composable that applies the chosen visual theme to the entire app.
 *
 * Wraps [MaterialTheme] with:
 * - The appropriate [ColorScheme] from [AppColorTheme.colorScheme]
 * - Custom [Typography] built from Cinzel (display) + Nunito (body) via [appTypography]
 * - [LocalAppColorTheme] composition local so any descendant can query the active theme
 *
 * ### Theme switching (Phase 12)
 * In Phase 12 a [SettingsViewModel] will read [SettingsDataStore.colorThemeName] and convert
 * it to [AppColorTheme]. The root [App] composable is updated to accept [colorTheme] as a
 * parameter which flows down from the ViewModel observation.
 *
 * @param colorTheme   The active palette. Defaults to [AppColorTheme.Mystical].
 * @param content      The composable subtree to theme.
 */
@Composable
fun AppTheme(
    colorTheme: AppColorTheme = AppColorTheme.Mystical,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAppColorTheme provides colorTheme) {
        MaterialTheme(
            colorScheme = colorTheme.colorScheme,
            typography = appTypography(),
            content = content
        )
    }
}
