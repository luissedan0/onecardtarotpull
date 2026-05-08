import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        // Register the Swift image-picker bridge BEFORE the first Compose frame is drawn
        // (and before Koin resolves ImagePickerImpl) so that any early call to pickImage()
        // finds the invoker already in place.
        ImagePickerRegistry.shared.invoker = SwiftImagePickerInvoker()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
