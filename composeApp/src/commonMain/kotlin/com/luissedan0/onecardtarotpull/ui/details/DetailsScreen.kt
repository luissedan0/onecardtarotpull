package com.luissedan0.onecardtarotpull.ui.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.luissedan0.onecardtarotpull.data.model.CardMeaning
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme
import com.luissedan0.onecardtarotpull.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

// ── Card visual dimensions (same ratio as PullCardTab) ────────────────────────
private val DetailCardWidth  = 180.dp
private val DetailCardHeight = 315.dp  // ≈ 1 : 1.75 tarot ratio
private val DetailCardCorner = 12.dp

// ─── DetailsScreen ────────────────────────────────────────────────────────────

/**
 * Full card details screen reached from "Learn more" ([HomeScreen]) or a Journal
 * entry tap ([JournalScreen]).
 *
 * ### Layout (scrollable Column)
 * ```
 * ┌──────────────────────────────────────────────┐
 * │  ← Details                        (TopAppBar) │
 * ├──────────────────────────────────────────────┤
 * │  "The Fool (Reversed)"            (heading)   │
 * │                                               │
 * │  ┌────────────────────────────────────────┐   │
 * │  │  THE FOOL  (rotated 180° if reversed)  │   │  ← CardPlaceholder (13.2)
 * │  └────────────────────────────────────────┘   │
 * │                                               │
 * │  Keywords:                                    │
 * │  ┌──────────┐ ┌──────────┐ ┌──────────┐     │  ← Keyword chips (user req.)
 * │  │ intuition│ │  mystery │ │  wisdom  │     │
 * │  └──────────┘ └──────────┘ └──────────┘     │
 * │                                               │
 * │  ─────────────────────────────────────────    │
 * │  Meaning text (upright or reversed)           │  ← MeaningText (13.2)
 * └──────────────────────────────────────────────┘
 * ```
 *
 * The [DetailsViewModel] is injected via Koin with [cardId] and [isReversed] as
 * dynamic parameters — see checklist item 13.3.
 *
 * @param navController Used to pop the back-stack when the back arrow is tapped.
 * @param cardId        The [TarotCard.id] whose details to display.
 * @param isReversed    Whether the card was pulled in the reversed orientation.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    cardId: Int,
    isReversed: Boolean
) {
    // Inject DetailsViewModel scoped to this NavBackStackEntry (Koin forwards the
    // typed nav args via parametersOf so the VM does not need SavedStateHandle).
    val viewModel: DetailsViewModel = koinViewModel(
        parameters = { parametersOf(cardId, isReversed) }
    )
    val cardMeaning by viewModel.cardMeaning.collectAsStateWithLifecycle()

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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                // ── Loading ──────────────────────────────────────────────────
                cardMeaning == null -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // ── Content ───────────────────────────────────────────────────
                else -> {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn()
                    ) {
                        DetailsContent(
                            cardMeaning = cardMeaning!!,
                            isReversed = isReversed
                        )
                    }
                }
            }
        }
    }
}

// ─── Details content ──────────────────────────────────────────────────────────

/**
 * Scrollable column with all card details once [CardMeaning] has loaded.
 *
 * Section order:
 * 1. Card name heading (with "(Reversed)" suffix)
 * 2. [CardPlaceholder] — styled card rectangle, rotated 180° when reversed
 * 3. [KeywordChips] — FlowRow of [SuggestionChip]s (upright or reversed keywords)
 * 4. Horizontal divider
 * 5. [MeaningText] — full meaning paragraph
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailsContent(
    cardMeaning: CardMeaning,
    isReversed: Boolean,
    modifier: Modifier = Modifier
) {
    val displayName = if (isReversed) "${cardMeaning.name} (Reversed)" else cardMeaning.name
    val keywords    = if (isReversed) cardMeaning.keywordsReversed else cardMeaning.keywords
    val meaningText = if (isReversed) cardMeaning.reversedMeaning else cardMeaning.uprightMeaning

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. Card name heading ─────────────────────────────────────────────────
        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // 2. Card placeholder rectangle (future: real artwork) ────────────────
        CardPlaceholder(
            cardName = cardMeaning.name,
            isReversed = isReversed
        )

        Spacer(Modifier.height(20.dp))

        // 3. Keyword chips (between card box and meaning — per user request) ───
        KeywordSection(keywords = keywords)

        Spacer(Modifier.height(16.dp))

        // 4. Divider ──────────────────────────────────────────────────────────
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(Modifier.height(16.dp))

        // 5. Meaning text ──────────────────────────────────────────────────────
        MeaningText(
            meaning = meaningText,
            isReversed = isReversed
        )

        Spacer(Modifier.height(32.dp))
    }
}

// ─── Card placeholder (13.2) ──────────────────────────────────────────────────

/**
 * A styled card-shaped rectangle that serves as a placeholder until artwork is added.
 *
 * Shows the [cardName] centred inside the rectangle.
 * When [isReversed] is `true`, the entire box (including text) is rotated 180° via
 * [Modifier.graphicsLayer], giving a visual upside-down card effect (13.2 spec).
 *
 * Standard tarot aspect ratio ≈ 1 : 1.75.
 */
@Composable
private fun CardPlaceholder(
    cardName: String,
    isReversed: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(DetailCardWidth)
            .height(DetailCardHeight)
            .graphicsLayer {
                rotationZ = if (isReversed) 180f else 0f
            }
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(DetailCardCorner)
            ),
        shape = RoundedCornerShape(DetailCardCorner),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = cardName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Keyword chips (user request: between card box and meaning text) ──────────

/**
 * A labelled section of [SuggestionChip]s laid out in a wrapping [FlowRow].
 *
 * Shows either the upright or reversed keywords depending on [isReversed] — the
 * caller (DetailsContent) already selects the correct [keywords] list.
 *
 * Each chip uses [SuggestionChipDefaults.suggestionChipColors] with
 * [MaterialTheme.colorScheme.primaryContainer] so chips inherit the active palette
 * (Mystical antique-gold tint or Inferno dark-crimson tint automatically).
 *
 * @param keywords The list of keyword strings to render (3–6 items typically).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KeywordSection(
    keywords: List<String>,
    modifier: Modifier = Modifier
) {
    if (keywords.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Keywords",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            keywords.forEach { keyword ->
                KeywordChip(keyword = keyword)
            }
        }
    }
}

/**
 * A single non-interactive keyword chip.
 *
 * Uses [SuggestionChip] — Material3's chip variant for read-only labels.
 * Colors are set explicitly so chips respect the active [AppColorTheme]:
 * - Container: `primaryContainer` (gold-brown for Mystical / dark-crimson for Inferno)
 * - Label: `onPrimaryContainer`
 * - Border: `primary` (antique gold / blood red)
 */
@Composable
private fun KeywordChip(keyword: String) {
    SuggestionChip(
        onClick = { },   // non-interactive — SuggestionChip requires an onClick lambda
        label = {
            Text(
                text = keyword,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    )
}

// ─── Meaning text (13.2) ──────────────────────────────────────────────────────

/**
 * The full-paragraph meaning text for the card.
 *
 * A small section label ("Upright Meaning" / "Reversed Meaning") precedes the
 * paragraph so the user knows which interpretation is displayed.
 */
@Composable
private fun MeaningText(
    meaning: String,
    isReversed: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (isReversed) "Reversed Meaning" else "Upright Meaning",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = meaning,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

private val previewCardMeaning = CardMeaning(
    cardId = 2,
    name = "The High Priestess",
    keywords = listOf("intuition", "mystery", "inner knowing", "divine feminine", "wisdom"),
    keywordsReversed = listOf("secrets", "repressed intuition", "withdrawal", "inner confusion"),
    uprightMeaning = "The High Priestess sits at the threshold of the conscious and subconscious " +
        "mind. She is the guardian of the unconscious. She is the guardian of the unconscious " +
        "and asks you to look within, to trust your inner voice, and to pay attention to the " +
        "subtle signs and synchronicities that guide your path.",
    reversedMeaning = "The High Priestess reversed suggests that you may be ignoring or " +
        "repressing your inner voice. You may be out of touch with your intuition, caught in " +
        "the noise of everyday life and unable to hear the quiet whispers of your higher self."
)

@Preview
@Composable
private fun DetailsContentUprightPreview() {
    AppTheme {
        DetailsContent(cardMeaning = previewCardMeaning, isReversed = false)
    }
}

@Preview
@Composable
private fun DetailsContentReversedPreview() {
    AppTheme(colorTheme = AppColorTheme.Inferno) {
        DetailsContent(cardMeaning = previewCardMeaning, isReversed = true)
    }
}

@Preview
@Composable
private fun KeywordSectionPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            KeywordSection(
                keywords = listOf("intuition", "mystery", "inner knowing", "divine feminine", "wisdom")
            )
        }
    }
}
