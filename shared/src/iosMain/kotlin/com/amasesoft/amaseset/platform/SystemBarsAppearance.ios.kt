package com.amasesoft.amaseset.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewController

/**
 * Status bar style requested by the Compose UI. The iOS app's root view controller (UIKit, not
 * a SwiftUI hosting controller, which ignores its children here) answers
 * `preferredStatusBarStyle` with it. Forcing the interface style instead would also change
 * `isSystemInDarkTheme`.
 */
internal object IosStatusBar {
    /** `null` until the UI decides: the system default. */
    var darkBackground: Boolean? = null
        private set

    var controller: UIViewController? = null

    val style: UIStatusBarStyle?
        get() = when (darkBackground) {
            true -> UIStatusBarStyleLightContent
            false -> UIStatusBarStyleDarkContent
            null -> null
        }

    fun update(darkBackground: Boolean) {
        if (this.darkBackground == darkBackground) return
        this.darkBackground = darkBackground
        // The root (parent) view controller decides the status bar appearance.
        val target = controller?.parentViewController ?: controller
        target?.setNeedsStatusBarAppearanceUpdate()
    }
}

@Composable
actual fun SystemBarsAppearance(darkTheme: Boolean) {
    SideEffect { IosStatusBar.update(darkTheme) }
}
