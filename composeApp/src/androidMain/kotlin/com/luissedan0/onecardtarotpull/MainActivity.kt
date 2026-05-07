package com.luissedan0.onecardtarotpull

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.luissedan0.onecardtarotpull.platform.AndroidImagePickerHelper

/**
 * Single Activity host for the Compose Multiplatform UI.
 *
 * Koin DI is started in [TarotApp.onCreate] (runs before this Activity).
 * This class is only responsible for:
 * 1. Registering the photo-picker [ActivityResultLauncher] (must happen before STARTED).
 * 2. Hosting the Compose content tree.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Register the PickVisualMedia launcher before the Activity moves past STARTED.
        // Bridges the system photo picker result to AndroidImagePickerHelper so that
        // ImagePickerImpl (Koin-injected) can trigger a pick without holding an Activity ref.
        val imagePickerLauncher = registerForActivityResult(PickVisualMedia()) { uri ->
            AndroidImagePickerHelper.onResult(uri, applicationContext)
        }
        AndroidImagePickerHelper.registerLauncher(imagePickerLauncher)

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
