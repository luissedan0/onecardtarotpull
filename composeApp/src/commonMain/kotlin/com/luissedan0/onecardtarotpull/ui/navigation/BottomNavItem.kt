package com.luissedan0.onecardtarotpull.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Describes the two tabs inside [HomeScreen]'s bottom navigation bar.
 *
 * Tabs switch UI content *within* HomeScreen and are NOT separate NavGraph destinations.
 * The NavController is only used when the user navigates away from HomeScreen
 * (e.g. to Settings or Details).
 *
 * Icons are from the Material Icons core set (no extended dependency needed):
 * - PullCard → Star / StarBorder (outline)
 * - Journal  → List (same filled/outlined look — core set lacks an outlined list)
 */
sealed class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    /** The "Pull a Card" tab: shows the card-back and pull interaction. */
    data object PullCard : BottomNavItem(
        label = "Pull a Card",
        icon = Icons.Outlined.Star,
        selectedIcon = Icons.Filled.Star
    )

    /** The "Journal" tab: shows the saved pull history. */
    data object Journal : BottomNavItem(
        label = "Journal",
        icon = Icons.AutoMirrored.Outlined.List,
        selectedIcon = Icons.AutoMirrored.Filled.List
    )

    companion object {
        /** Ordered list used to build the NavigationBar items. */
        val items: List<BottomNavItem> = listOf(PullCard, Journal)

        /**
         * [Saver] that persists the active [BottomNavItem] as its ordinal [Int].
         *
         * `Int` is natively Bundle-serializable so this satisfies [rememberSaveable]
         * on both Android (Bundle-backed) and iOS (in-memory) without any platform
         * imports or `@Parcelize` annotations.
         *
         * Usage:
         * ```kotlin
         * var selectedTab by rememberSaveable(stateSaver = BottomNavItem.Saver) {
         *     mutableStateOf(BottomNavItem.PullCard)
         * }
         * ```
         */
        val Saver: Saver<BottomNavItem, Int> = Saver(
            save    = { items.indexOf(it).coerceAtLeast(0) },
            restore = { index -> items.getOrElse(index) { PullCard } }
        )
    }
}
