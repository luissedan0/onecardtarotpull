package com.luissedan0.onecardtarotpull.platform

/**
 * iOS stub: returns an empty string.
 *
 * The iOS [ImagePickerImpl] is a stub that always returns `null` bytes (Phase 6 / Phase 13),
 * so this function is never called in practice on iOS. A full implementation using
 * `NSFileManager` and `NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, ...)` will
 * be added when the iOS Swift bridge is completed in Phase 13.
 */
actual fun saveImageToAppStorage(bytes: ByteArray, fileName: String): String = ""
