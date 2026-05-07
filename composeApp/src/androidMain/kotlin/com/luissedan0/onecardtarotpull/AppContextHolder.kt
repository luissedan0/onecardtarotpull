package com.luissedan0.onecardtarotpull

import android.content.Context

/**
 * Holds the application [Context] for use in platform-specific factory functions
 * ([DatabaseFactory], [DataStoreFactory]) that cannot receive the context via DI at
 * the time they are called.
 *
 * Initialized once in [MainActivity.onCreate] before any factory is invoked.
 */
object AppContextHolder {
    private lateinit var _context: Context

    /** Application context. Safe to call after [init] has been invoked. */
    val context: Context get() = _context

    /** Must be called in [MainActivity.onCreate] before Koin is started. */
    fun init(context: Context) {
        _context = context.applicationContext
    }
}
