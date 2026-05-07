package com.luissedan0.onecardtarotpull.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe Navigation Compose routes for the entire app.
 *
 * Three top-level destinations (bottom-nav tabs live *inside* [HomeScreen], not here):
 * - [Home]    — Scaffold with bottom nav (PullCard | Journal tabs)
 * - [Settings] — Navigated from the top-bar overflow menu
 * - [Details]  — Navigated from "Learn more" button or a Journal entry tap
 *
 * Each class / object is annotated with [@Serializable] so Navigation Compose can
 * serialise them into the back-stack argument bundle automatically.
 */
sealed interface AppRoutes {

    /** Main screen: contains the BottomNavigationBar with PullCard and Journal tabs. */
    @Serializable
    data object Home : AppRoutes

    /** Settings screen: auto-save toggle + custom card-back picker. */
    @Serializable
    data object Settings : AppRoutes

    /**
     * Card details screen.
     * @param cardId The [TarotCard.id] of the card to display.
     * @param isReversed Whether the card was pulled in the reversed orientation.
     */
    @Serializable
    data class Details(val cardId: Int, val isReversed: Boolean) : AppRoutes
}
