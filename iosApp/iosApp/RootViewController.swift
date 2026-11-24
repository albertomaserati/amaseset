import UIKit
import Shared

/// Root of the app: hosts the Compose UI and lets it choose the status bar style
/// (light/dark icons follow the in-app theme and the run screen background).
final class RootViewController: UIViewController {
    private let compose = MainViewControllerKt.MainViewController()

    override func viewDidLoad() {
        super.viewDidLoad()
        addChild(compose)
        compose.view.frame = view.bounds
        compose.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.addSubview(compose.view)
        compose.didMove(toParent: self)
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        UIStatusBarStyle(rawValue: Int(MainViewControllerKt.preferredStatusBarStyle())) ?? .default
    }
}
