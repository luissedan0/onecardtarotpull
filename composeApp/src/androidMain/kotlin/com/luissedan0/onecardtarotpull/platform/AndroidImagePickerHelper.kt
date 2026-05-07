package com.luissedan0.onecardtarotpull.platform

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia

/**
 * Singleton bridge that decouples [ImagePickerImpl] from the Activity lifecycle.
 *
 * [MainActivity] calls [registerLauncher] during `onCreate` to supply the
 * [ActivityResultLauncher] produced by `registerForActivityResult(PickVisualMedia())`.
 * When the user picks (or cancels) the picker, [onResult] is called with the chosen URI,
 * which is then read into bytes and forwarded to [pendingCallback].
 */
object AndroidImagePickerHelper {

    @Volatile
    var pendingCallback: ((ByteArray?) -> Unit)? = null

    private var launcher: ActivityResultLauncher<PickVisualMediaRequest>? = null

    fun registerLauncher(launcher: ActivityResultLauncher<PickVisualMediaRequest>) {
        this.launcher = launcher
    }

    /** Called by the ActivityResultLauncher callback in MainActivity. */
    fun onResult(uri: Uri?, context: Context) {
        val bytes = uri?.let { safeUri ->
            runCatching {
                context.contentResolver.openInputStream(safeUri)?.use { it.readBytes() }
            }.getOrNull()
        }
        pendingCallback?.invoke(bytes)
        pendingCallback = null
    }

    /** Launches the system photo picker; stores [callback] to be invoked on result. */
    fun launch(callback: (ByteArray?) -> Unit) {
        pendingCallback = callback
        launcher?.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            ?: callback(null) // no launcher registered yet — fail gracefully
    }
}
