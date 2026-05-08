package com.luissedan0.onecardtarotpull.platform

import com.luissedan0.onecardtarotpull.AppContextHolder
import java.io.File

/**
 * Android actual: writes [bytes] to `filesDir/[fileName]` and returns the absolute path.
 *
 * [AppContextHolder.context] is available because [com.luissedan0.onecardtarotpull.TarotApp]
 * initialises it before Koin starts (and therefore before the first image pick can occur).
 */
actual fun saveImageToAppStorage(bytes: ByteArray, fileName: String): String {
    val file = File(AppContextHolder.context.filesDir, fileName)
    file.writeBytes(bytes)
    return file.absolutePath
}
