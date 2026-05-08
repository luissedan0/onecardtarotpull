package com.luissedan0.onecardtarotpull.platform

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create

/**
 * iOS implementation of [saveImageToAppStorage].
 *
 * Writes [bytes] to `<Documents>/<fileName>` using [NSFileManager].
 * The Documents directory is user-visible, included in iCloud backups, and persists
 * across app updates — the right choice for a user-selected card-back image.
 *
 * @return The absolute file path on success, or an empty string if writing fails.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun saveImageToAppStorage(bytes: ByteArray, fileName: String): String {
    // Resolve the Documents directory.
    val paths = NSSearchPathForDirectoriesInDomains(
        directory  = NSDocumentDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true
    )
    val documentsDir = paths.firstOrNull() as? String ?: return ""
    val filePath = "$documentsDir/$fileName"

    // Convert ByteArray → NSData via a pinned pointer (zero-copy).
    val nsData: NSData = bytes.usePinned { pinned ->
        NSData.create(
            bytes  = pinned.addressOf(0),
            length = bytes.size.toULong()
        )
    }

    // Atomically write to disk (temp file + rename — safe against partial writes).
    return if (NSFileManager.defaultManager.createFileAtPath(filePath, nsData, null)) {
        filePath
    } else {
        ""
    }
}
