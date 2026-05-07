package com.luissedan0.onecardtarotpull.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val DATASTORE_FILE_NAME = "settings.preferences_pb"

/**
 * Platform-specific absolute path for the DataStore preferences file.
 * Android returns a path inside `filesDir`; iOS returns a path inside the Documents directory.
 */
expect fun createDataStorePath(): String

/**
 * Creates and returns the app [DataStore<Preferences>] instance.
 * Should be called only once — inject the result as a singleton via Koin.
 */
fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { createDataStorePath().toPath() }
    )
