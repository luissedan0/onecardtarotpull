package com.luissedan0.onecardtarotpull

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.luissedan0.onecardtarotpull.platform.AndroidImagePickerHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialise before Koin or any factory is invoked
        AppContextHolder.init(this)

        // Register the photo-picker launcher before the Activity moves past STARTED.
        // The callback bridges the Activity result into AndroidImagePickerHelper so that
        // ImagePickerImpl (injected by Koin) can invoke it without holding an Activity ref.
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
