package com.luissedan0.onecardtarotpull.data.local

import com.luissedan0.onecardtarotpull.AppContextHolder
import java.io.File

actual fun createDataStorePath(): String =
    File(AppContextHolder.context.filesDir, DATASTORE_FILE_NAME).absolutePath
