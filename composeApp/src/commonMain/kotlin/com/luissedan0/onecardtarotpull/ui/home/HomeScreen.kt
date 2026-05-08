package com.luissedan0.onecardtarotpull.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import com.luissedan0.onecardtarotpull.ui.journal.JournalScreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.luissedan0.onecardtarotpull.ui.navigation.AppRoutes
import com.luissedan0.onecardtarotpull.ui.navigation.BottomNavItem
import com.luissedan0.onecardtarotpull.ui.navigation.BottomNavigationBar
import org.koin.compose.viewmodel.koinViewModel

/**
 * Root screen composable that owns the Scaffold, bottom nav, and
 * orchestrates between the PullCard and Journal tabs.
 *
 * ### Responsibilities
 * - Injects [HomeViewModel] via Koin
 * - Observes [HomeUiState] and propagates to [PullCardTab]
 * - Handles one-shot [SnackbarEvent]s via [LaunchedEffect]
 * - Hosts [HomeTopBar] (title tracks active tab) and [BottomNavigationBar]
 *
 * TODO Phase 11: Replace the Journal tab placeholder with JournalTab(koinViewModel()).
 */
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Use the custom Saver so the active tab survives configuration changes and process death.
    // Storing a plain BottomNavItem would crash with IllegalArgumentException because the
    // sealed-class data objects are not Bundle-serializable by default.
    var selectedTab by rememberSaveable(stateSaver = BottomNavItem.Saver) {
        mutableStateOf(BottomNavItem.PullCard)
    }
    val snackbarHostState = remember { SnackbarHostState() }

    // Consume one-shot snackbar events from the ViewModel.
    LaunchedEffect(uiState.snackbarEvent) {
        val event = uiState.snackbarEvent ?: return@LaunchedEffect
        val message = when (event) {
            SnackbarEvent.SavedToJournal -> "Saved to journal ✓"
            SnackbarEvent.AutoSaved      -> "Auto-saved to journal ✓"   // Phase 14
            SnackbarEvent.SaveError      -> "Could not save — please try again"
        }
        snackbarHostState.showSnackbar(message)
        viewModel.consumeSnackbarEvent()
    }

    // TODO Phase 14 (Polish): BackHandler — when Journal is active, back → PullCard
    //   instead of exiting. Use expect/actual (androidMain wraps activity BackHandler,
    //   iosMain no-op).

    Scaffold(
        topBar = {
            HomeTopBar(
                title = selectedTab.label,
                onHowToUse = { /* passed to menu below */ },
                onSettings = { navController.navigate(AppRoutes.Settings) }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when (selectedTab) {
            BottomNavItem.PullCard -> PullCardTab(
                uiState = uiState,
                onLongPressStart = viewModel::onLongPressStart,
                onLongPressEnd = viewModel::onLongPressEnd,
                onCardLongPress = viewModel::onCardLongPress,
                onSaveToJournal = viewModel::saveToJournal,
                onLearnMore = { cardId, isReversed ->
                    navController.navigate(AppRoutes.Details(cardId, isReversed))
                },
                modifier = Modifier.padding(innerPadding)
            )
            BottomNavItem.Journal -> JournalScreen(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// ─── Top App Bar ─────────────────────────────────────────────────────────────

/**
 * Top app bar for [HomeScreen].
 *
 * The [title] switches between "Pull a Card" and "Journal" as the active tab changes.
 * The trailing three-dot icon opens [HomeMenu].
 *
 * @param title         Current tab label.
 * @param onHowToUse    Action for the "How to use" menu item.
 * @param onSettings    Action for the "Settings" menu item.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    title: String,
    onHowToUse: () -> Unit,
    onSettings: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showHowToUseDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }
            HomeMenu(
                expanded = showMenu,
                onDismiss = { showMenu = false },
                onHowToUse = {
                    showMenu = false
                    showHowToUseDialog = true
                    onHowToUse()
                },
                onSettings = {
                    showMenu = false
                    onSettings()
                }
            )
        }
    )

    if (showHowToUseDialog) {
        HowToUseDialog(onDismiss = { showHowToUseDialog = false })
    }
}

// ─── Overflow Menu ────────────────────────────────────────────────────────────

/**
 * Material3 [DropdownMenu] with "How to use" and "Settings" items.
 *
 * @param expanded   Whether the menu is currently visible.
 * @param onDismiss  Called when the menu should close without action.
 * @param onHowToUse Called when "How to use" is tapped.
 * @param onSettings Called when "Settings" is tapped.
 */
@Composable
private fun HomeMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onHowToUse: () -> Unit,
    onSettings: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("How to use") },
            onClick = onHowToUse
        )
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = onSettings
        )
    }
}

// ─── How To Use Dialog ────────────────────────────────────────────────────────

/**
 * Simple [AlertDialog] explaining the pull interaction.
 *
 * TODO Phase 14 (Polish): Replace placeholder text with real instructions and
 *   consider a custom illustrated dialog using a ModalBottomSheet.
 *
 * @param onDismiss Called when the user taps "Close" or taps outside the dialog.
 */
@Composable
private fun HowToUseDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How to use") },
        text = {
            Text(
                text = "Long-press the card to begin shuffling the deck.\n\n" +
                       "Release to reveal your card.\n\n" +
                       "Long-press the revealed card to return it to the deck.\n\n" +
                       "Tap \"Learn more\" for the full card meaning, or " +
                       "\"Save to journal\" to keep a record of your pull.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
