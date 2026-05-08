package com.luissedan0.onecardtarotpull.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.luissedan0.onecardtarotpull.platform.ImagePicker
import com.luissedan0.onecardtarotpull.platform.saveImageToAppStorage
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ─── SettingsScreen ───────────────────────────────────────────────────────────

/**
 * Full Settings screen with three sections:
 * 1. **Auto-save** — Checkbox toggle; persisted via [SettingsViewModel.setAutoSave].
 * 2. **Custom card back** — Button to open [ImagePicker]; optional thumbnail + clear button.
 * 3. **Theme** — [ThemeOptionRow] for each [AppColorTheme] with two color swatches and a RadioButton.
 *
 * Injects [SettingsViewModel] and [ImagePicker] via Koin.
 * The image-pick callback uses [rememberCoroutineScope] to write bytes off the main thread
 * with [saveImageToAppStorage], then stores the resulting path via [SettingsViewModel.setCustomCardBack].
 *
 * @param navController Used to pop the back-stack when the back arrow is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: SettingsViewModel = koinViewModel()
    val imagePicker: ImagePicker = koinInject()
    val scope = rememberCoroutineScope()

    val autoSaveEnabled by viewModel.autoSaveEnabled.collectAsStateWithLifecycle()
    val cardBackPath by viewModel.customCardBackPath.collectAsStateWithLifecycle()
    val colorTheme by viewModel.colorTheme.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
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
                .verticalScroll(rememberScrollState())
        ) {

            // ── Section: Auto-Save (12.2 / 12.1) ─────────────────────────────
            SettingsSectionHeader(title = "Behaviour")

            AutoSaveRow(
                checked = autoSaveEnabled,
                onCheckedChange = viewModel::setAutoSave
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(Modifier.height(8.dp))

            // ── Section: Custom Card Back (12.2, 12.3) ────────────────────────
            SettingsSectionHeader(title = "Card Back")

            CustomCardBackRow(
                cardBackPath = cardBackPath,
                onPickImage = {
                    // pickImage delivers bytes on the main thread; writing to disk
                    // is dispatched to IO to avoid blocking the UI.
                    imagePicker.pickImage { bytes ->
                        if (bytes != null) {
                            scope.launch {
                                val path = withContext(Dispatchers.Default) {
                                    saveImageToAppStorage(bytes)
                                }
                                if (path.isNotEmpty()) {
                                    viewModel.setCustomCardBack(path)
                                }
                            }
                        }
                    }
                },
                onClearImage = { viewModel.setCustomCardBack(null) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(Modifier.height(8.dp))

            // ���─ Section: Theme (Phase 9 request, Phase 12 implementation) ────
            SettingsSectionHeader(title = "Theme")

            AppColorTheme.entries.forEach { theme ->
                ThemeOptionRow(
                    theme = theme,
                    isSelected = theme == colorTheme,
                    onSelect = { viewModel.setColorTheme(theme) }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Section header ──────────────────────────────────────────────────────────

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

// ─── Auto-Save row (12.2) ─────────────────────────────────────────────────────

/**
 * A settings row with a [Checkbox] on the left and a two-line label on the right.
 * Tapping anywhere on the row toggles the checkbox.
 *
 * Layout:
 * ```
 * ☑  Automatically save pulled cards
 *    Each draw is added to your journal automatically.
 * ```
 */
@Composable
private fun AutoSaveRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Automatically save pulled cards",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Each draw is added to your journal automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Custom card back row (12.2, 12.3) ───────────────────────────────────────

/**
 * Row for picking and clearing a custom card-back image.
 *
 * Layout:
 * ```
 * Set custom back of card     [thumbnail?] [Pick image] [✕]?
 * ```
 *
 * - If [cardBackPath] is not null, shows a 48×48 rounded thumbnail via Coil.
 * - The clear [IconButton] (✕) appears only when an image is set.
 */
@Composable
private fun CustomCardBackRow(
    cardBackPath: String?,
    onPickImage: () -> Unit,
    onClearImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Set custom back of card",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (cardBackPath == null) {
                Text(
                    text = "Using default card back.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Thumbnail (if a custom back is set)
        if (cardBackPath != null) {
            AsyncImage(
                model = cardBackPath,
                contentDescription = "Current card back preview",
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.small
                    ),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
        }

        // Pick image button
        OutlinedButton(onClick = onPickImage) {
            Text(if (cardBackPath == null) "Pick image" else "Change")
        }

        // Clear button (visible only when a custom image exists)
        if (cardBackPath != null) {
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onClearImage) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Remove custom card back",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ─── Theme option row (Phase 9 spec, Phase 12 implementation) ────────────────

/**
 * A single theme-selection row in the "Theme" settings section.
 *
 * ### Layout
 * ```
 * ● ●  Mystical                    ○
 * ● ●  Inferno                     ●
 * ```
 * - Two filled [CircleShape] swatches: `primary` colour then `background` colour.
 * - Theme display name.
 * - [RadioButton] on the far right.
 *
 * @param theme      The palette this row represents.
 * @param isSelected Whether this row is currently active.
 * @param onSelect   Called when the row is tapped.
 */
@Composable
private fun ThemeOptionRow(
    theme: AppColorTheme,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = theme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Primary colour swatch
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(scheme.primary)
        )
        // Background colour swatch with outline so a near-black circle is visible
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(scheme.background)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
        )
        // Theme name
        Text(
            text = theme.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        // Selection indicator
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
    }
}
