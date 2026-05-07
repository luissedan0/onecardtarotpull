package com.luissedan0.onecardtarotpull.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.luissedan0.onecardtarotpull.ui.details.DetailsScreen
import com.luissedan0.onecardtarotpull.ui.home.HomeScreen
import com.luissedan0.onecardtarotpull.ui.settings.SettingsScreen

/**
 * Top-level Navigation Compose host for the entire app.
 *
 * Three destinations:
 * - [AppRoutes.Home]    — start destination; hosts the two bottom-nav tabs
 * - [AppRoutes.Settings] — navigated from [HomeTopBar]'s overflow menu
 * - [AppRoutes.Details]  — navigated from "Learn more" or a Journal entry tap;
 *                          carries [AppRoutes.Details.cardId] and [AppRoutes.Details.isReversed]
 *                          as type-safe route parameters
 *
 * Uses Navigation Compose KMP type-safe APIs ([composable]<reified T>, [toRoute]).
 * Route serialisation is handled automatically by the `@Serializable` annotations
 * on each [AppRoutes] subclass.
 *
 * @param modifier Applied to the underlying [NavHost].
 * @param navController Defaults to a new [rememberNavController]; override in tests / previews.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home,
        modifier = modifier
    ) {
        // ── Home ─────────────────────────────────────────────────────────────
        composable<AppRoutes.Home> {
            HomeScreen(navController = navController)
        }

        // ── Settings ──────────────────────────────────────────────────────────
        composable<AppRoutes.Settings> {
            SettingsScreen(navController = navController)
        }

        // ── Details ───────────────────────────────────────────────────────────
        // toRoute<AppRoutes.Details>() deserialises cardId + isReversed from the
        // back-stack entry that was created by navController.navigate(Details(…)).
        composable<AppRoutes.Details> { backStackEntry ->
            val route: AppRoutes.Details = backStackEntry.toRoute()
            DetailsScreen(
                navController = navController,
                cardId = route.cardId,
                isReversed = route.isReversed
            )
        }
    }
}
