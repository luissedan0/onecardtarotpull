package com.luissedan0.onecardtarotpull

import androidx.compose.ui.window.ComposeUIViewController
import com.luissedan0.onecardtarotpull.di.KoinInitializer

/**
 * iOS entry point for the Compose Multiplatform UI.
 *
 * Called by [iosApp/iosApp/ContentView.swift] which wraps it as a SwiftUI
 * [UIViewControllerRepresentable]. Koin is started here on the first call
 * (guarded by [KoinInitializer] to prevent double-initialisation).
 */
fun MainViewController() = ComposeUIViewController(
    configure = {
        // Initialise Koin before the first Compose frame.
        KoinInitializer.init()
    }
) {
    App()
}
