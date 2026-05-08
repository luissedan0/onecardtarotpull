package com.luissedan0.onecardtarotpull.ui.home

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.luissedan0.onecardtarotpull.ui.theme.AppColorTheme
import com.luissedan0.onecardtarotpull.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview

// ─── Card dimensions ─────────────────────────────────────────────────────────
// Standard tarot ratio ≈ 1 : 1.75
private val CardWidth  = 200.dp
private val CardHeight = 350.dp
private val CardCorner = 12.dp

// ─── PullCardTab ─────────────────────────────────────────────────────────────

/**
 * The "Pull a Card" content shown inside [HomeScreen]'s Scaffold when the PullCard
 * bottom-nav tab is active.
 *
 * This composable is stateless — all state is driven by [uiState] and all user
 * actions flow upward through callbacks.
 *
 * ### Layout
 * ```
 * ┌────────────────────────────────────┐
 * │  (top padding / spacing)           │
 * │  ┌──────────────────────────────┐  │
 * │  │  FlippableCard               │  │
 * │  │   ├ CardBackView (Idle/Shuffle)│ │
 * │  │   └ CardFrontView (Revealed) │  │
 * │  └──────────────────────────────┘  │
 * │  (spacing)                         │
 * │  CardActionButtons (Revealed only) │
 * │  (bottom padding)                  │
 * └────────────────────────────────────┘
 * ```
 *
 * @param uiState          Observed from [HomeViewModel.uiState].
 * @param onLongPressStart Called when the long-press threshold is crossed on the card back.
 * @param onLongPressEnd   Called when the finger is released after a long press.
 * @param onCardLongPress  Called when the user long-presses the revealed card face.
 * @param onSaveToJournal  Triggers [HomeViewModel.saveToJournal].
 * @param onLearnMore      Navigates to the Details screen with the card id and orientation.
 * @param modifier         Applied to the root [Column].
 */
@Composable
fun PullCardTab(
    uiState: HomeUiState,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit,
    onCardLongPress: () -> Unit,
    onSaveToJournal: () -> Unit,
    onLearnMore: (cardId: Int, isReversed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardState = uiState.cardState
    val pulledCard = (cardState as? CardState.Revealed)?.card

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Instruction hint when idle
        if (cardState is CardState.Idle) {
            Text(
                text = "Long-press the card to draw",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }

        // ── Flippable card ────────────────────────────────────────────────────
        FlippableCard(
            cardState = cardState,
            onLongPressStart = onLongPressStart,
            onLongPressEnd = onLongPressEnd,
            onCardLongPress = onCardLongPress,
            customCardBackPath = uiState.customCardBackPath,
            pulledCard = pulledCard
        )

        // ── Action buttons — visible after card is revealed ───────────────────
        Spacer(Modifier.height(32.dp))
        if (pulledCard != null) {
            CardActionButtons(
                onLearnMore = { onLearnMore(pulledCard.card.id, pulledCard.isReversed) },
                onSaveToJournal = onSaveToJournal
            )
        } else {
            // Reserve the space so the card stays vertically centred
            Spacer(Modifier.height(48.dp))
        }
    }
}

// ─── FlippableCard ────────────────────────────────────────────────────────────

/**
 * Orchestrates the two-phase card flip and the shuffle wobble animation.
 *
 * ### Card flip (10B.5)
 * `rotationY` is animated from 0° → 180° over 600 ms (FastOutSlowIn).
 * - `rotation ≤ 90°` → the **back** face is rendered (camera sees the surface correctly).
 * - `rotation > 90°` → the **front** face is rendered, un-mirrored with a 180° counter-rotation.
 * `cameraDistance` is set to 12× density to prevent perspective distortion at wide angles.
 *
 * ### Shuffle wobble (10B.4)
 * An [InfiniteTransition] drives:
 * - `rotationZ` oscillating ±6° at 250 ms period
 * - `translationY` oscillating ±5 dp at 350 ms period
 * These are applied only when `cardState == Shuffling` and the flip hasn't started yet.
 */
@Composable
private fun FlippableCard(
    cardState: CardState,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit,
    onCardLongPress: () -> Unit,
    customCardBackPath: String?,
    pulledCard: com.luissedan0.onecardtarotpull.data.model.PulledCard?
) {
    // ── Flip rotation ─────────────────────────────────────────────────────────
    val isRevealed = cardState is CardState.Revealed
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    // ── Shuffle wobble ────────────────────────────────────────────────────────
    val isShuffling = cardState is CardState.Shuffling
    val infiniteTransition = rememberInfiniteTransition(label = "shuffle")
    val shuffleRotation by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 250, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shuffle_rot"
    )
    val shuffleTranslationY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shuffle_ty"
    )

    Box(
        modifier = Modifier
            .width(CardWidth)
            .height(CardHeight)
            // Shuffle wobble wrapper — only applied when shuffling and card is face-down
            .graphicsLayer {
                if (isShuffling && rotation < 90f) {
                    rotationZ = shuffleRotation
                    translationY = shuffleTranslationY
                }
            }
            // Flip rotation wrapper
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        if (rotation <= 90f) {
            CardBackView(
                isShuffling = isShuffling,
                customBackPath = customCardBackPath,
                onLongPressStart = onLongPressStart,
                onLongPressEnd = onLongPressEnd,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Un-mirror the front face: the flip already rotated 180°, so we counter-rotate.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                CardFrontView(
                    pulledCard = pulledCard,
                    onCardLongPress = onCardLongPress,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// ─── CardBackView (10B.3) ─────────────────────────────────────────────────────

/**
 * The face-down side of the card.
 *
 * Displays either:
 * - The custom back image at [customBackPath] (loaded with Coil3 [AsyncImage])
 * - Or the default styled [Surface] with a "Hold to pull" label
 *
 * Long-press detection uses [awaitEachGesture] to capture BOTH the threshold crossing
 * ([onLongPressStart]) and the subsequent finger release ([onLongPressEnd]).
 *
 * @param isShuffling     Whether the shuffle wobble animation is active (visual cue).
 * @param customBackPath  Optional file-system path to a custom back image.
 * @param onLongPressStart Fired when long-press threshold is crossed (haptic in ViewModel).
 * @param onLongPressEnd   Fired on finger lift after a long press (triggers card pull).
 */
@Composable
private fun CardBackView(
    isShuffling: Boolean,
    customBackPath: String?,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(CardCorner),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isShuffling) 8.dp else 2.dp,
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                shape = RoundedCornerShape(CardCorner)
            )
            .pointerInput(Unit) {
                val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    // withTimeoutOrNull returns null when the timeout fires first
                    val lifted = withTimeoutOrNull(longPressTimeout) {
                        waitForUpOrCancellation()
                    }
                    if (lifted == null) {
                        // Threshold crossed — start shuffle
                        onLongPressStart()
                        // Wait for the finger to lift
                        waitForUpOrCancellation()
                        onLongPressEnd()
                    }
                    // Quick tap: do nothing
                }
            }
    ) {
        if (customBackPath != null) {
            // Coil3 async image from a file-system path
            coil3.compose.AsyncImage(
                model = customBackPath,
                contentDescription = "Custom card back",
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            DefaultCardBackContent(isShuffling = isShuffling)
        }
    }
}

@Composable
private fun DefaultCardBackContent(isShuffling: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                text = if (isShuffling) "Shuffling…" else "Hold to pull",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── CardFrontView (10B.6) ────────────────────────────────────────────────────

/**
 * The face-up side of the card, shown after the flip animation completes.
 *
 * Displays the [PulledCard.displayName] centered on a [Surface].
 * Long-press calls [onCardLongPress] which resets to [CardState.Idle] in the ViewModel.
 *
 * @param pulledCard      The drawn card, or `null` if the flip animation is still in progress.
 * @param onCardLongPress Resets the card to the back and returns to [CardState.Idle].
 */
@Composable
private fun CardFrontView(
    pulledCard: com.luissedan0.onecardtarotpull.data.model.PulledCard?,
    onCardLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(CardCorner),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(CardCorner)
            )
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onCardLongPress() })
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (pulledCard != null) {
                    Text(
                        text = pulledCard.displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    if (pulledCard.isReversed) {
                        Text(
                            text = "Reversed",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Long-press to return",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun CardBackIdlePreview() {
    AppTheme {
        Box(modifier = Modifier.width(CardWidth).height(CardHeight)) {
            CardBackView(
                isShuffling = false,
                customBackPath = null,
                onLongPressStart = {},
                onLongPressEnd = {}
            )
        }
    }
}

@Preview
@Composable
private fun CardBackShufflingPreview() {
    AppTheme {
        Box(modifier = Modifier.width(CardWidth).height(CardHeight)) {
            CardBackView(
                isShuffling = true,
                customBackPath = null,
                onLongPressStart = {},
                onLongPressEnd = {}
            )
        }
    }
}

@Preview
@Composable
private fun CardFrontPreview() {
    val card = com.luissedan0.onecardtarotpull.data.model.TarotCard(
        id = 0,
        name = "The Fool",
        isMajorArcana = true,
        suit = null,
        number = 0,
        keywords = listOf("beginnings", "freedom"),
        keywordsReversed = listOf("reckless", "careless"),
        meaningUpright = "New beginnings.",
        meaningReversed = "Recklessness."
    )
    val pulled = com.luissedan0.onecardtarotpull.data.model.PulledCard(
        card = card,
        isReversed = false
    )
    AppTheme {
        Box(modifier = Modifier.width(CardWidth).height(CardHeight)) {
            CardFrontView(pulledCard = pulled, onCardLongPress = {})
        }
    }
}

@Preview
@Composable
private fun CardFrontReversedPreview() {
    val card = com.luissedan0.onecardtarotpull.data.model.TarotCard(
        id = 36,
        name = "Five of Cups",
        isMajorArcana = false,
        suit = com.luissedan0.onecardtarotpull.data.model.TarotSuit.CUPS,
        number = 5,
        keywords = listOf("loss", "grief"),
        keywordsReversed = listOf("acceptance", "moving on"),
        meaningUpright = "Loss and sorrow.",
        meaningReversed = "Moving on."
    )
    val pulled = com.luissedan0.onecardtarotpull.data.model.PulledCard(
        card = card,
        isReversed = true
    )
    AppTheme(colorTheme = AppColorTheme.Inferno) {
        Box(modifier = Modifier.width(CardWidth).height(CardHeight)) {
            CardFrontView(pulledCard = pulled, onCardLongPress = {})
        }
    }
}

// ─── CardActionButtons (10B.7) ────────────────────────────────────────────────

/**
 * "Learn more" (filled [Button]) and "Save to journal" ([OutlinedButton]) row,
 * shown below the card once it is revealed.
 *
 * @param onLearnMore     Navigates to [AppRoutes.Details] with the card's id + orientation.
 * @param onSaveToJournal Calls [HomeViewModel.saveToJournal]; the ViewModel emits the
 *                        snackbar event on completion.
 */
@Composable
private fun CardActionButtons(
    onLearnMore: () -> Unit,
    onSaveToJournal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onLearnMore,
            modifier = Modifier.fillMaxWidth(0.75f)
        ) {
            Text("Learn more")
        }
        OutlinedButton(
            onClick = onSaveToJournal,
            modifier = Modifier.fillMaxWidth(0.75f)
        ) {
            Text("Save to journal")
        }
    }
}
