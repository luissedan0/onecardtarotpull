package com.luissedan0.onecardtarotpull.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * Android implementation of [HapticFeedback].
 *
 * Uses [VibrationEffect] (API 26+) for a short heavy-click pulse, falling back to
 * the deprecated [Vibrator.vibrate(Long)] on API 24-25.
 *
 * Requires the `android.permission.VIBRATE` permission (declared in AndroidManifest).
 */
class HapticFeedbackImpl(private val context: Context) : HapticFeedback {

    @Suppress("DEPRECATION")
    override fun performHeavyClick() {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Vibrator::class.java) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // API 29+: use the predefined EFFECT_HEAVY_CLICK pattern
            vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26-28: one-shot pulse, 40ms, full amplitude
            vibrator.vibrate(
                VibrationEffect.createOneShot(40L, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            // API 24-25 fallback — deprecated but functional
            vibrator.vibrate(40L)
        }
    }
}
