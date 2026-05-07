package com.luissedan0.onecardtarotpull

import android.content.Context

/**
 * Holds the application [Context] for use in platform-specific factory functions
 * ([DatabaseFactory], [DataStoreFactory]) that cannot receive the context via DI at
 * the time they are called.
 *
 * Initialized once in [TarotApp.onCreate] before Koin starts, so that the lazy
 * singleton factory ([createTarotDatabase] / [createDataStore]) always has a valid
 * context when they are first resolved.
 */
object AppContextHolder {
    private lateinit var _context: Context

    /** Application context. Safe to call after [init] has been invoked. */
    val context: Context get() = _context

    /** Must be called in [TarotApp.onCreate] before [startKoin] is invoked. */
    fun init(context: Context) {
        _context = context.applicationContext
    }
}
