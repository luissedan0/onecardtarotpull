package com.luissedan0.onecardtarotpull.di

import com.luissedan0.onecardtarotpull.platform.HapticFeedback
import com.luissedan0.onecardtarotpull.platform.HapticFeedbackImpl
import com.luissedan0.onecardtarotpull.platform.ImagePicker
import com.luissedan0.onecardtarotpull.platform.ImagePickerImpl
import com.luissedan0.onecardtarotpull.platform.ShareHandler
import com.luissedan0.onecardtarotpull.platform.ShareHandlerImpl
import org.koin.dsl.module

/**
 * Koin module for iOS-specific platform bindings.
 *
 * Provides the three platform-service interfaces declared in
 * [com.luissedan0.onecardtarotpull.platform] using iOS implementations.
 * No Android context is available or needed here.
 */
val iosModule = module {

    /** Haptic feedback via UIImpactFeedbackGenerator(.heavy). */
    single<HapticFeedback> { HapticFeedbackImpl() }

    /**
     * Image picker — currently a stub returning null.
     * Phase 13 will replace this with a Swift Bridge (ImagePickerBridge.swift)
     * that presents PHPickerViewController from the root UIViewController.
     */
    single<ImagePicker> { ImagePickerImpl() }

    /** Native share sheet via UIActivityViewController. */
    single<ShareHandler> { ShareHandlerImpl() }
}
