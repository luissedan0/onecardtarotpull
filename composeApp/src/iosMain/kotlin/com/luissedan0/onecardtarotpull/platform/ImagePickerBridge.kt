package com.luissedan0.onecardtarotpull.platform

/**
 * Kotlin-side glue for the Swift → Kotlin image-picker bridge.
 *
 * ## Architecture
 *
 * Kotlin/Native 2.3.x cannot subclass `NSObject` from commonMain/iosMain in a class
 * hierarchy position (needed for ObjC delegate protocols like `PHPickerViewControllerDelegate`).
 * The solution is a two-object handshake between Kotlin and Swift:
 *
 * ### Setup (once at app start — `iOSApp.swift`)
 * ```
 * ImagePickerRegistry.shared.invoker = SwiftImagePickerInvoker()
 * ```
 *
 * ### Trigger (from `ImagePickerImpl.pickImage`)
 * ```
 * ImagePickerCallbackBridge.store(onResult)  // save the Kotlin callback
 * ImagePickerRegistry.invoker?.invoke()      // tell Swift to show the picker
 * ```
 *
 * ### Delivery (from `SwiftImagePickerInvoker` after the user picks / cancels)
 * ```swift
 * ImagePickerCallbackBridge.shared.deliver(bytes: jpegData)  // or .cancel()
 * ```
 *
 * `ByteArray?` maps to `NSData?` in the Kotlin/Native ObjC header, so Swift passes
 * a plain `Data?` value — the bridge handles the conversion automatically.
 */

// ─── Invoker interface ────────────────────────────────────────────────────────

/**
 * Parameterless trigger — Swift implements this to show `PHPickerViewController`.
 *
 * Kotlin/Native exports `fun interface` as an ObjC protocol, so Swift can write:
 * ```swift
 * class SwiftImagePickerInvoker: NSObject, ImagePickerInvoker {
 *     func invoke() { … }
 * }
 * ```
 */
fun interface ImagePickerInvoker {
    fun invoke()
}

// ─── Registry ─────────────────────────────────────────────────────────────────

/**
 * Holds the Swift-provided [ImagePickerInvoker].
 *
 * Registered in `iOSApp.swift` before the first Compose frame is drawn.
 * Swift access: `ImagePickerRegistry.shared.invoker = SwiftImagePickerInvoker()`
 */
object ImagePickerRegistry {
    var invoker: ImagePickerInvoker? = null
}

// ─── Callback bridge ──────────────────────────────────────────────────────────

/**
 * Stores the Kotlin callback while the picker is open and receives the result
 * from Swift once the user picks or cancels.
 *
 * Always called on the main thread (Swift dispatches_async to main before calling
 * into Kotlin), so no synchronisation is required.
 *
 * Swift access:
 * - `ImagePickerCallbackBridge.shared.deliver(bytes: jpegData)` — image selected
 * - `ImagePickerCallbackBridge.shared.cancel()` — user dismissed the picker
 */
object ImagePickerCallbackBridge {

    private var pending: ((ByteArray?) -> Unit)? = null

    /**
     * Called by [ImagePickerImpl] before invoking the Swift picker.
     * Stored BEFORE the trigger so a fast callback can never arrive with `pending == null`.
     */
    internal fun store(callback: (ByteArray?) -> Unit) {
        pending = callback
    }

    /**
     * Called from Swift with the JPEG bytes of the selected image.
     * `bytes` is `null` only when Coil or JPEG encoding fails.
     * In Swift: `deliver(bytes: Data?)` — NSData ↔ ByteArray bridge is automatic.
     */
    fun deliver(bytes: ByteArray?) {
        pending?.invoke(bytes)
        pending = null
    }

    /** Called from Swift when the user taps Cancel or closes the picker empty-handed. */
    fun cancel() = deliver(null)
}
