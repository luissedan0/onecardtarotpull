package com.luissedan0.onecardtarotpull.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme
import com.luissedan0.onecardtarotpull.ui.theme.AppTheme

/**
 * Settings screen stub — will be fully implemented in Phase 13.
 *
 * ### Phase 13 full implementation
 * Wire to `SettingsViewModel` (via `koinViewModel()`) and add:
 *
 * - **Auto-save toggle** — `Switch` + description label. Persisted via
 *   `SettingsDataStore.setAutoSaveEnabled(Boolean)`.
 *
 * - **Custom card back** — `Button` that opens [ImagePicker]. Persisted via
 *   `SettingsDataStore.setCustomCardBackPath(String?)`.
 *
 * ### Phase 12 — Theme selector (from user request, Phase 9 session)
 * Add a section titled "Theme" with two selectable options:
 *
 * ```kotlin
 * AppColorTheme.entries.forEach { theme ->
 *     ThemeOptionRow(
 *         theme = theme,
 *         isSelected = (theme == selectedTheme),
 *         onSelect = { viewModel.setColorTheme(theme) }
 *     )
 * }
 * ```
 *
 * `ThemeOptionRow` shows:
 * - Theme name ([AppColorTheme.displayName])
 * - Two small color swatches (primary + background) for visual preview
 * - A `RadioButton` aligned to the right
 *
 * The selection is persisted via `SettingsDataStore.setColorThemeName(theme.name)`
 * and observed in `App` as a [StateFlow] from `SettingsViewModel.colorTheme`.
 *
 * Toolbar: back arrow + "Settings" title.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚙ Settings — coming in Phase 13",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Theme selector → Phase 12\nAuto-save toggle → Phase 13\nCard back picker → Phase 13",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenMysticalPreview() {
    AppTheme(AppColorTheme.Mystical) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Settings — Mystical")
        }
    }
}

@Preview
@Composable
private fun SettingsScreenInfernoPreview() {
    AppTheme(AppColorTheme.Inferno) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Settings — Inferno")
        }
    }
}
