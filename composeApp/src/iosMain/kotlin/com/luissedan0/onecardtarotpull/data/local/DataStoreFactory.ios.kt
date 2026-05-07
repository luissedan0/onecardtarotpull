package com.luissedan0.onecardtarotpull.data.local

import platform.Foundation.NSHomeDirectory

actual fun createDataStorePath(): String =
    NSHomeDirectory() + "/Documents/$DATASTORE_FILE_NAME"
