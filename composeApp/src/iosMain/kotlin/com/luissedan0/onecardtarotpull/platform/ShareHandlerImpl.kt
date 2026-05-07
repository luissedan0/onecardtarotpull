package com.luissedan0.onecardtarotpull.platform

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

/**
 * iOS implementation of [ShareHandler].
 *
 * Presents a [UIActivityViewController] from the app's root [UIViewController],
 * which renders the standard iOS share sheet with all installed share targets.
 *
 * If no root view controller is available the call is a no-op.
 */
class ShareHandlerImpl : ShareHandler {

    override fun share(text: String) {
        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        val controller = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        rootVC.presentViewController(controller, animated = true, completion = null)
    }
}
