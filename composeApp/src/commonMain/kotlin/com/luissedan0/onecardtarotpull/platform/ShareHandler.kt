package com.luissedan0.onecardtarotpull.platform

/**
 * Platform-agnostic contract for sharing plain text via the native share sheet.
 *
 * Used by the Journal screen to share a pulled card's name and meaning.
 *
 * Platform implementations:
 * - Android → [ShareHandlerImpl] via Intent.ACTION_SEND chooser
 * - iOS    → [ShareHandlerImpl] via UIActivityViewController presented from root UIViewController
 */
interface ShareHandler {
    fun share(text: String)
}
