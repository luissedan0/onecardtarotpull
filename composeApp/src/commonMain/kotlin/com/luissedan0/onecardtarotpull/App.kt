package com.luissedan0.onecardtarotpull

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.luissedan0.onecardtarotpull.ui.navigation.AppNavHost

/**
 * Root composable for the OneCardTarotPull app.
 *
 * Wraps the entire UI in [MaterialTheme] (swapped for [AppTheme] in Phase 9)
 * and delegates all navigation to [AppNavHost].
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost()
        }
    }
}
