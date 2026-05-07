package com.luissedan0.onecardtarotpull.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Platform-specific builder for [TarotDatabase].
 *
 * Each platform provides a [RoomDatabase.Builder] pre-configured with the correct
 * database file path and context (Android requires a [Context]; iOS uses the filesystem).
 * The common [createTarotDatabase] function applies the shared driver and coroutine config.
 */
expect fun getDatabaseBuilder(): RoomDatabase.Builder<TarotDatabase>

/**
 * Creates and returns the fully configured [TarotDatabase] instance.
 * Should be called only once — inject the result as a singleton via Koin.
 */
fun createTarotDatabase(): TarotDatabase =
    getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
