package com.luissedan0.onecardtarotpull.ui.settings

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
 * Settings screen.
 *
 * TODO Phase 13: Full implementation
 * - "Automatically save pulled cards" checkbox (persisted via DataStore)
 * - "Set custom card back" — opens [ImagePicker] (Android: PickVisualMedia, iOS: Swift bridge)
 * - SettingsViewModel via koinViewModel()
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Settings — coming in Phase 13")
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("Settings Preview")
    }
}
