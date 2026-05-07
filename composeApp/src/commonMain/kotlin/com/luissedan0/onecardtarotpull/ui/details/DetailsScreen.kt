package com.luissedan0.onecardtarotpull.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

/**
 * Details screen for a specific tarot card.
 *
 * @param cardId The [TarotCard.id] of the card whose meaning is displayed.
 * @param isReversed Whether the card was pulled reversed (shown in title + card visual).
 *
 * TODO Phase 12: Full implementation
 * - Card name as title (with "(Reversed)" suffix when applicable)
 * - Rectangle with card name — future: artwork, rotated 180° if reversed
 * - Keywords (upright + reversed) and meaning text from CardMeaningRepository
 * - DetailsViewModel via koinViewModel()
 *
 * Toolbar: back arrow + "Details" title.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    cardId: Int,
    isReversed: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
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
    ) { _ ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Card $cardId${if (isReversed) " (Reversed)" else ""} — Details coming in Phase 12")
        }
    }
}

@Preview
@Composable
private fun DetailsScreenPreview() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("Details Preview")
    }
}
