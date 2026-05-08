package com.luissedan0.onecardtarotpull.platform

/**
 * Writes [bytes] to a fixed file in the app's private storage and returns the
 * absolute file-system path to the written file.
 *
 * Used by the Settings screen to persist a user-picked card-back image.
 * The returned path is stored in [SettingsDataStore] and observed by [HomeViewModel]
 * for display in [CardBackView].
 *
 * Platform implementations:
 * - **Android** (`androidMain`) — writes to `Context.filesDir/custom_card_back.jpg`
 * - **iOS** (`iosMain`) — stub returning `""` (image picker is deferred to Phase 13)
 *
 * The [fileName] parameter allows callers to vary the destination, though in practice
 * the app always uses `"custom_card_back.jpg"`.
 */
expect fun saveImageToAppStorage(bytes: ByteArray, fileName: String = "custom_card_back.jpg"): String
