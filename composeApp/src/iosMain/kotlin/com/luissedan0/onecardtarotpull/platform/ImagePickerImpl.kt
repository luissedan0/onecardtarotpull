package com.luissedan0.onecardtarotpull.platform

/**
 * iOS implementation of [ImagePicker].
 *
 * Delegates to [ImagePickerRegistry.invoker] — a `SwiftImagePickerInvoker` registered at
 * app startup in `iOSApp.swift`. The Swift invoker shows `PHPickerViewController` and
 * delivers the selected image's JPEG bytes back via [ImagePickerCallbackBridge.deliver].
 *
 * ## Bridge flow
 * ```
 * pickImage(onResult)
 *   ├── ImagePickerCallbackBridge.store(onResult)   // save callback before triggering
 *   └── ImagePickerRegistry.invoker.invoke()        // tell Swift to show the picker
 *         └── [UIKit] PHPickerViewController displayed
 *               └── user picks / cancels
 *                     └── ImagePickerCallbackBridge.deliver(bytes) / .cancel()
 *                           └── onResult(ByteArray?) called on main thread
 * ```
 *
 * See [ImagePickerBridge.kt] for the full bridge architecture.
 */
class ImagePickerImpl : ImagePicker {

    override fun pickImage(onResult: (ByteArray?) -> Unit) {
        val invoker = ImagePickerRegistry.invoker
        if (invoker != null) {
            // Store BEFORE invoking so there is no window where Swift delivers
            // a result before the callback is registered.
            ImagePickerCallbackBridge.store(onResult)
            invoker.invoke()
        } else {
            // Bridge not registered — degrade gracefully (should not happen in production).
            onResult(null)
        }
    }
}
