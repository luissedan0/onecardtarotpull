package com.luissedan0.onecardtarotpull.di

import com.luissedan0.onecardtarotpull.platform.HapticFeedback
import com.luissedan0.onecardtarotpull.platform.HapticFeedbackImpl
import com.luissedan0.onecardtarotpull.platform.ImagePicker
import com.luissedan0.onecardtarotpull.platform.ImagePickerImpl
import com.luissedan0.onecardtarotpull.platform.ShareHandler
import com.luissedan0.onecardtarotpull.platform.ShareHandlerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for Android-specific platform bindings.
 *
 * Provides the three platform-service interfaces declared in [com.luissedan0.onecardtarotpull.platform]
 * using Android implementations. [androidContext] is available because [TarotApp] calls
 * `startKoin { androidContext(this) }` before this module is resolved.
 */
val androidModule = module {

    /** Haptic feedback via [android.os.VibrationEffect] / [android.os.Vibrator]. */
    single<HapticFeedback> { HapticFeedbackImpl(androidContext()) }

    /**
     * Image picker backed by [androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia].
     * The [ActivityResultLauncher] is registered in [com.luissedan0.onecardtarotpull.MainActivity]
     * and bridged via [com.luissedan0.onecardtarotpull.platform.AndroidImagePickerHelper].
     */
    single<ImagePicker> { ImagePickerImpl() }

    /** Native share sheet via [android.content.Intent.ACTION_SEND] chooser. */
    single<ShareHandler> { ShareHandlerImpl(androidContext()) }
}
