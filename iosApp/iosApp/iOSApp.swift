import UIKit

/// UIKit entry point. A SwiftUI `WindowGroup` would put a hosting controller at the root,
/// which does not ask the Compose UI for the status bar style. The scene is configured in
/// Info.plist (`UIApplicationSceneManifest` -> `SceneDelegate`).
@main
final class AppDelegate: UIResponder, UIApplicationDelegate {}

final class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = scene as? UIWindowScene else { return }
        let window = UIWindow(windowScene: windowScene)
        window.rootViewController = RootViewController()
        window.makeKeyAndVisible()
        self.window = window
    }
}
