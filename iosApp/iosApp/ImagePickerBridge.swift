import Foundation
import PhotosUI
import UIKit
import ComposeApp

// MARK: - SwiftImagePickerInvoker

/// Swift implementation of the Kotlin `ImagePickerInvoker` protocol.
///
/// Registered at app startup via:
/// ```swift
/// ImagePickerRegistry.shared.invoker = SwiftImagePickerInvoker()
/// ```
///
/// When Kotlin calls `invoke()`, this class presents a `PHPickerViewController`
/// from the current key window's root view controller.  After the user picks or
/// cancels, the result is delivered back to Kotlin via:
/// - `ImagePickerCallbackBridge.shared.deliver(bytes:)` — image selected
/// - `ImagePickerCallbackBridge.shared.cancel()` — picker dismissed empty-handed
///
/// The Kotlin/Native ObjC header maps `ByteArray?` to `ComposeAppKotlinByteArray?`,
/// so we convert `Data` manually using `KotlinByteArray(size:)` + `set(index:value:)`.
@objc final class SwiftImagePickerInvoker: NSObject, ImagePickerInvoker {

    // MARK: ImagePickerInvoker

    func invoke() {
        // Always present UI on the main thread.
        DispatchQueue.main.async { [weak self] in
            self?.presentPicker()
        }
    }

    // MARK: Private

    private func presentPicker() {
        var config = PHPickerConfiguration(photoLibrary: .shared())
        config.selectionLimit = 1
        config.filter = .images

        let picker = PHPickerViewController(configuration: config)
        picker.delegate = self

        guard let rootVC = Self.rootViewController() else {
            ImagePickerCallbackBridge.shared.cancel()
            return
        }
        rootVC.present(picker, animated: true)
    }

    /// Walks the key-window hierarchy to find the topmost presented view controller,
    /// which is the correct target for `.present(_:animated:)`.
    private static func rootViewController() -> UIViewController? {
        let keyWindow = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }

        var root = keyWindow?.rootViewController
        while let presented = root?.presentedViewController {
            root = presented
        }
        return root
    }

    // MARK: - Data → KotlinByteArray conversion

    /// Converts `Data` to the `ComposeAppKotlinByteArray` that Kotlin expects.
    ///
    /// Kotlin/Native maps `ByteArray` to `KotlinByteArray` in ObjC/Swift.
    /// `KotlinByteArray` is indexed by `Int32` and holds signed `Int8` values.
    private static func toKotlinByteArray(_ data: Data) -> KotlinByteArray {
        let kotlinArray = KotlinByteArray(size: Int32(data.count))
        data.withUnsafeBytes { rawBuffer in
            for (index, byte) in rawBuffer.enumerated() {
                kotlinArray.set(index: Int32(index), value: Int8(bitPattern: byte))
            }
        }
        return kotlinArray
    }
}

// MARK: - PHPickerViewControllerDelegate

extension SwiftImagePickerInvoker: PHPickerViewControllerDelegate {

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)

        guard let itemProvider = results.first?.itemProvider,
              itemProvider.canLoadObject(ofClass: UIImage.self) else {
            // User cancelled or selected a non-image item.
            ImagePickerCallbackBridge.shared.cancel()
            return
        }

        itemProvider.loadObject(ofClass: UIImage.self) { object, _ in
            guard let image = object as? UIImage,
                  let jpegData = image.jpegData(compressionQuality: 0.85) else {
                DispatchQueue.main.async {
                    ImagePickerCallbackBridge.shared.cancel()
                }
                return
            }

            // Deliver JPEG bytes to Kotlin on the main thread.
            // The ObjC header signature is `deliverBytes:(KotlinByteArray?)`,
            // so we must convert Data → KotlinByteArray manually.
            let kotlinBytes = Self.toKotlinByteArray(jpegData)
            DispatchQueue.main.async {
                ImagePickerCallbackBridge.shared.deliver(bytes: kotlinBytes)
            }
        }
    }
}
