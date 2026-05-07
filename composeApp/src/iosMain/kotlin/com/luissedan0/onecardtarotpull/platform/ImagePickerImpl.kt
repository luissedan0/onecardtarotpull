package com.luissedan0.onecardtarotpull.platform

/**
 * iOS stub implementation of [ImagePicker].
 *
 * ## Why a stub?
 * Implementing `PHPickerViewController` from Kotlin/Native 2.3.x requires subclassing
 * `NSObject` to create an Objective-C delegate, which in KN 2.3.x is restricted by the
 * `@BetaInteropApi` requirement AND by the compiler's refusal to resolve `platform.Foundation.NSObject`
 * in class hierarchy positions (see INTERACTIONS.md, Session 7 for full investigation notes).
 *
 * ## Recommended Phase 13 approach — Swift Bridge
 * 1. In `iosApp/iosApp/`, create `ImagePickerBridge.swift`:
 *    ```swift
 *    import SwiftUI, PhotosUI, shared
 *    @objc public class ImagePickerBridge : NSObject {
 *        @objc public static func pickImage(completion: @escaping (Data?) -> Void) {
 *            var config = PHPickerConfiguration()
 *            config.filter = .images
 *            // … present and forward bytes to `completion`
 *        }
 *    }
 *    ```
 * 2. In `MainViewController.kt`, call the bridge via `@ObjCName`-annotated KN interop.
 *
 * Until Phase 13 wires the bridge, this stub calls [onResult] with `null` so that the
 * Settings screen can gracefully skip image selection on iOS.
 */
class ImagePickerImpl : ImagePicker {

    override fun pickImage(onResult: (ByteArray?) -> Unit) {
        // TODO Phase 13: invoke Swift ImagePickerBridge instead of this stub.
        onResult(null)
    }
}
