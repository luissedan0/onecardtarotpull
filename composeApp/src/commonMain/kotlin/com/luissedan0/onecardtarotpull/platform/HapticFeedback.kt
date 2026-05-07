package com.luissedan0.onecardtarotpull.platform

/**
 * Platform-agnostic contract for haptic feedback.
 *
 * Triggered on long-press of the card back to signal shuffle start.
 * Platform implementations:
 * - Android → [HapticFeedbackImpl] via VibrationEffect / Vibrator
 * - iOS    → [HapticFeedbackImpl] via UIImpactFeedbackGenerator(.heavy)
 */
interface HapticFeedback {
    fun performHeavyClick()
}
