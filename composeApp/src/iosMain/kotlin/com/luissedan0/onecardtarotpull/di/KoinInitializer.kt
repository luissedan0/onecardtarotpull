package com.luissedan0.onecardtarotpull.di

import org.koin.core.context.startKoin

/**
 * Bootstraps Koin for the iOS target.
 *
 * Called once from [com.luissedan0.onecardtarotpull.MainViewController] before
 * the first Compose frame is rendered. A guard flag prevents double-initialisation
 * if the view controller is ever re-created (e.g. in SwiftUI previews).
 */
object KoinInitializer {
    private var started = false

    fun init() {
        if (started) return
        started = true

        startKoin {
            modules(appModule, iosModule)
        }
    }
}
