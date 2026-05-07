package com.luissedan0.onecardtarotpull.platform

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

/**
 * iOS implementation of [HapticFeedback].
 *
 * Uses [UIImpactFeedbackGenerator] with the `.heavy` style, which produces a
 * strong physical tap sensation — appropriate for the long-press shuffle trigger.
 */
class HapticFeedbackImpl : HapticFeedback {

    override fun performHeavyClick() {
        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
        generator.prepare()
        generator.impactOccurred()
    }
}
