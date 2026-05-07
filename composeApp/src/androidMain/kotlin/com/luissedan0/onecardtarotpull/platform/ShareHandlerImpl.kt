package com.luissedan0.onecardtarotpull.platform

import android.content.Context
import android.content.Intent

/**
 * Android implementation of [ShareHandler].
 *
 * Fires a standard `ACTION_SEND` chooser intent from the application context.
 * `FLAG_ACTIVITY_NEW_TASK` is required when starting an Activity from a non-Activity context.
 */
class ShareHandlerImpl(private val context: Context) : ShareHandler {

    override fun share(text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(sendIntent, null)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
