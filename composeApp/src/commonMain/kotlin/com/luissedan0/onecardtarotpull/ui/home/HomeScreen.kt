package com.luissedan0.onecardtarotpull.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.luissedan0.onecardtarotpull.ui.navigation.AppRoutes
import com.luissedan0.onecardtarotpull.ui.navigation.BottomNavItem
import com.luissedan0.onecardtarotpull.ui.navigation.BottomNavigationBar

/**
 * Host screen for the two in-screen tabs (PullCard / Journal).
 *
 * Scaffold layout:
 * - topBar    → [HomeTopBar]: title reflects selected tab; menu opens overflow
 * - bottomBar → [BottomNavigationBar]: switches between tabs
 * - content   → [PullCardTab] or [JournalTab] based on [selectedTab]
 *
 * Back-press behaviour:
 * - If Journal tab is active, pressing back returns to PullCard tab.
 * - If PullCard is already active, the system handles back (exits the app).
 *
 * TODO Phase 10 & 11: Replace placeholder content boxes with real tab composables
 *   and wire HomeViewModel / JournalViewModel via koinViewModel().
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var selectedTab by rememberSaveable { mutableStateOf<BottomNavItem>(BottomNavItem.PullCard) }
    val snackbarHostState = remember { SnackbarHostState() }

    // TODO Phase 14 (Polish): Add BackHandler so Android's hardware back-press
    //   returns to PullCard when Journal is active, rather than exiting the app.
    //   Use expect/actual: androidMain wraps androidx.activity.compose.BackHandler,
    //   iosMain is a no-op (iOS has no root back gesture at the tab level).

    Scaffold(
        topBar = {
            HomeTopBar(
                title = selectedTab.label,
                onSettingsClick = { navController.navigate(AppRoutes.Settings) }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                BottomNavItem.PullCard -> {
                    // TODO Phase 10: PullCardTab(navController, snackbarHostState, koinViewModel())
                    Text("Pull a Card — coming in Phase 10")
                }
                BottomNavItem.Journal -> {
                    // TODO Phase 11: JournalTab(navController, koinViewModel())
                    Text("Journal — coming in Phase 11")
                }
            }
        }
    }
}

/**
 * Top app bar for [HomeScreen].
 *
 * [title] switches between "Pull a Card" and "Journal" as tabs change.
 * The trailing icon opens the overflow menu (How to use / Settings).
 *
 * TODO Phase 10A: Replace stub with full HomeTopBar including DropdownMenu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    title: String,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
        }
    )
}
