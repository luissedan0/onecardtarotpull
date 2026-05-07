package com.luissedan0.onecardtarotpull.platform

/**
 * Android implementation of [ImagePicker].
 *
 * Delegates to [AndroidImagePickerHelper], which holds the [ActivityResultLauncher]
 * registered in [com.luissedan0.onecardtarotpull.MainActivity].
 */
class ImagePickerImpl : ImagePicker {

    override fun pickImage(onResult: (ByteArray?) -> Unit) {
        AndroidImagePickerHelper.launch(onResult)
    }
}
