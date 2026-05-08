@file:Suppress("DEPRECATION")  // kotlinx.datetime.Instant → kotlin.time.Instant; migrate when kotlinx-datetime 0.7 releases

package com.luissedan0.onecardtarotpull.ui.journal

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.luissedan0.onecardtarotpull.data.model.JournalEntry
import com.luissedan0.onecardtarotpull.platform.ShareHandler
import com.luissedan0.onecardtarotpull.ui.navigation.AppRoutes
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ─── JournalScreen ────────────────────────────────────────────────────────────

/**
 * Journal tab content — shown inside [HomeScreen]'s Scaffold when the Journal
 * bottom-nav item is active.
 *
 * Injects [JournalViewModel] and [ShareHandler] via Koin.
 * Displays a [LazyColumn] of [JournalEntryRow]s with swipe-to-delete.
 * Shows an empty-state message when no entries exist.
 *
 * @param navController  Used to navigate to [AppRoutes.Details] on row tap.
 * @param modifier        Applied to the root container; typically includes `innerPadding`.
 */
@Composable
fun JournalScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: JournalViewModel = koinViewModel()
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val shareHandler: ShareHandler = koinInject()

    if (entries.isEmpty()) {
        EmptyJournalMessage(modifier = modifier)
    } else {
        JournalList(
            entries = entries,
            onDelete = viewModel::deleteEntry,
            onShare = { entry ->
                shareHandler.share(buildShareText(entry))
            },
            onTapEntry = { entry ->
                navController.navigate(AppRoutes.Details(entry.cardId, entry.isReversed))
            },
            modifier = modifier
        )
    }
}

// ─── Empty state (11.2) ───────────────────────────────────────────────────────

@Composable
private fun EmptyJournalMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "✦",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Your journal is empty",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Pull a card and save it to see your readings here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

// ─── Journal list (11.2) ──────────────────────────────────────────────────────

/**
 * [LazyColumn] rendering one [SwipeToDeleteRow] per [JournalEntry].
 *
 * Each item is keyed by its database id so [Modifier.animateItem] can track
 * placements across insertions and deletions (11.5).
 */
@Composable
private fun JournalList(
    entries: List<JournalEntry>,
    onDelete: (Long) -> Unit,
    onShare: (JournalEntry) -> Unit,
    onTapEntry: (JournalEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            items = entries,
            key = { it.id }
        ) { entry ->
            SwipeToDeleteRow(
                entry = entry,
                onDelete = { onDelete(entry.id) },
                onShare = { onShare(entry) },
                onTap = { onTapEntry(entry) },
                modifier = Modifier.animateItem()        // 11.5
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp
            )
        }
    }
}

// ─── Swipe-to-delete row (11.4) ───────────────────────────────────────────────

/**
 * Wraps [JournalEntryRow] in [SwipeToDismissBox] to enable swipe-left to delete.
 *
 * ### Behaviour
 * - Only **EndToStart** (left swipe) is enabled; StartToEnd is disabled.
 * - The background reveals a red [MaterialTheme.colorScheme.error] box with a
 *   trash [Icon] and "Delete" label, animated via [animateColorAsState].
 * - On full dismiss: [onDelete] is called; Room updates [JournalViewModel.entries],
 *   which causes the item to leave the composition naturally (keyed `LazyColumn`).
 *
 * @param entry    The entry to display and potentially delete.
 * @param onDelete Called when the swipe reaches the dismiss threshold.
 * @param onShare  Called when the share icon is tapped.
 * @param onTap    Called when the row body is tapped (navigate to Details).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteRow(
    entry: JournalEntry,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Use the non-deprecated, no-callback constructor.
    // Deletion is triggered via LaunchedEffect when the swipe settles.
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            DeleteBackground(
                isDismissing = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
            )
        },
        modifier = modifier
    ) {
        JournalEntryRow(
            entry = entry,
            onShare = onShare,
            onTap = onTap
        )
    }
}

/**
 * Red background revealed behind the row during a swipe-left gesture.
 *
 * [animateColorAsState] drives the transition from transparent to
 * [MaterialTheme.colorScheme.error] as the drag begins, giving an "expanding red"
 * visual effect even without direct access to the swipe fraction.
 */
@Composable
private fun DeleteBackground(isDismissing: Boolean) {
    val bgColor by animateColorAsState(
        targetValue = if (isDismissing) MaterialTheme.colorScheme.error
                      else MaterialTheme.colorScheme.background,
        animationSpec = tween(durationMillis = 200),
        label = "delete_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isDismissing) MaterialTheme.colorScheme.onError
                      else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "delete_content"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(end = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        if (isDismissing) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor
                )
            }
        }
    }
}

// ─── Journal entry row (11.3) ─────────────────────────────────────────────────

/**
 * A single row in the journal list.
 *
 * ### Layout
 * ```
 * ┌──────────────────────────────────────────┐
 * │ [Card Display Name]          [Share icon] │
 * │ [May 7, 2026 · 7:30 PM]                  │
 * └──────────────────────────────────────────┘
 * ```
 *
 * Tapping the row body navigates to [AppRoutes.Details].
 * The share icon calls [ShareHandler.share] with a formatted text.
 *
 * @param entry   The [JournalEntry] to display.
 * @param onShare Called when the share icon is tapped.
 * @param onTap   Called when the row body is tapped.
 */
@Composable
private fun JournalEntryRow(
    entry: JournalEntry,
    onShare: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onTap)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: card name + timestamp
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.cardDisplayName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = entry.timestamp.toFormattedDateTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Right: share icon button
        IconButton(onClick = onShare) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share ${entry.cardDisplayName}",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── Timestamp formatting (kotlinx-datetime 0.6.x) ───────────────────────────

/**
 * Converts a Unix epoch milliseconds timestamp to a human-readable local date/time string.
 *
 * Output format: `"May 7, 2026 · 7:30 PM"`
 *
 * Uses [kotlinx.datetime] for platform-agnostic local time conversion.
 * The `hour12 / AM-PM` logic is manual since `kotlinx-datetime` 0.6.x does not
 * yet expose a `format()` / `DateTimeFormat` API for non-ISO patterns.
 */
@Suppress("DEPRECATION")   // kotlinx.datetime.Instant is deprecated → kotlin.time.Instant; migrate when 0.7 releases
private fun Long.toFormattedDateTime(): String {
    @Suppress("DEPRECATION")
    val instant = Instant.fromEpochMilliseconds(this)
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val monthName = when (dt.monthNumber) {
        1  -> "Jan";  2 -> "Feb";  3 -> "Mar"
        4  -> "Apr";  5 -> "May";  6 -> "Jun"
        7  -> "Jul";  8 -> "Aug";  9 -> "Sep"
        10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
        else -> "?"
    }
    val hour12 = when {
        dt.hour == 0  -> 12
        dt.hour > 12  -> dt.hour - 12
        else           -> dt.hour
    }
    val amPm = if (dt.hour < 12) "AM" else "PM"
    val minute = dt.minute.toString().padStart(2, '0')

    @Suppress("DEPRECATION")
    return "$monthName ${dt.dayOfMonth}, ${dt.year} · $hour12:$minute $amPm"
}

// ─── Share text helper ────────────────────────────────────────────────────────

/**
 * Builds the text that is passed to [ShareHandler.share] for a given [JournalEntry].
 *
 * Format:
 * ```
 * My One Card Tarot Pull:
 * The High Priestess (Reversed)
 * Pulled on May 7, 2026 · 7:30 PM
 *
 * — OneCardTarotPull
 * ```
 */
private fun buildShareText(entry: JournalEntry): String = buildString {
    appendLine("My One Card Tarot Pull:")
    appendLine(entry.cardDisplayName)
    appendLine("Pulled on ${entry.timestamp.toFormattedDateTime()}")
    appendLine()
    append("— OneCardTarotPull")
}
