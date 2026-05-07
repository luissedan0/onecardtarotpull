package com.luissedan0.onecardtarotpull.platform

/**
 * Platform-agnostic contract for picking an image from the device gallery.
 *
 * The [onResult] callback receives the selected image as a raw [ByteArray],
 * or `null` if the user cancelled or an error occurred.
 *
 * Platform implementations:
 * - Android → [ImagePickerImpl] via ActivityResultContracts.PickVisualMedia
 *             (launcher registered in MainActivity; callback stored in AndroidImagePickerHelper)
 * - iOS    → [ImagePickerImpl] via PHPickerViewController presented from root UIViewController
 *
 * Note: On Android, [pickImage] must be called after MainActivity.onCreate() has completed,
 * as the ActivityResultLauncher is registered there.
 */
interface ImagePicker {
    fun pickImage(onResult: (ByteArray?) -> Unit)
}
