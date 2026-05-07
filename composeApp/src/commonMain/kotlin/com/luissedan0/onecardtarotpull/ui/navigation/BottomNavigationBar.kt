package com.luissedan0.onecardtarotpull.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Material3 [NavigationBar] hosting the two in-screen tabs ([BottomNavItem.PullCard]
 * and [BottomNavItem.Journal]).
 *
 * This bar lives in [HomeScreen]'s [androidx.compose.material3.Scaffold] `bottomBar` slot.
 * Tab switching changes local UI state inside HomeScreen; the [NavController] is only
 * used when navigating away (to Settings or Details).
 *
 * @param selectedItem Currently active tab.
 * @param onItemSelected Callback invoked when the user taps a different tab.
 * @param modifier Optional [Modifier] for the underlying [NavigationBar].
 */
@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        BottomNavItem.items.forEach { item ->
            val isSelected = item == selectedItem
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) },
                alwaysShowLabel = true
            )
        }
    }
}

// ─── Preview ────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    BottomNavigationBar(
        selectedItem = BottomNavItem.PullCard,
        onItemSelected = {}
    )
}
