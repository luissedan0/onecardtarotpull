package com.luissedan0.onecardtarotpull.ui.journal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

/**
 * Journal screen — displays the sorted list of pulled cards.
 *
 * TODO Phase 11: Full implementation
 * - LazyColumn of JournalEntryRow items (date/time, card name, share icon)
 * - SwipeToDelete with red background animation
 * - Tap → navigate to Details
 * - JournalViewModel via koinViewModel()
 *
 * This composable is not currently wired into [AppNavHost] directly —
 * it is displayed as a tab inside [HomeScreen] via [BottomNavItem.Journal].
 */
@Composable
fun JournalScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Journal — coming in Phase 11")
    }
}

@Preview
@Composable
private fun JournalScreenPreview() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("Journal Preview")
    }
}
