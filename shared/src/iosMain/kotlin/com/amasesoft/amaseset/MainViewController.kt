package com.amasesoft.amaseset

import androidx.compose.ui.window.ComposeUIViewController
import com.amasesoft.amaseset.platform.IosStatusBar
import com.amasesoft.amaseset.di.AppContainer
import com.amasesoft.amaseset.di.AppVersion
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDefault

/** Process-wide container: created once, on first use. */
private val appContainer: AppContainer by lazy { AppContainer(documentsDirectory(), appVersion()) }

@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(url?.path) { "Documents directory not available" }
}

private fun appVersion(): AppVersion {
    val info = NSBundle.mainBundle.infoDictionary
    return AppVersion(
        name = info?.get("CFBundleShortVersionString") as? String ?: "",
        build = info?.get("CFBundleVersion") as? String ?: "",
    )
}

fun MainViewController() = ComposeUIViewController { App(appContainer) }.also { IosStatusBar.controller = it }

/**
 * Status bar style wanted by the UI (in-app theme, run screen background), for the root view
 * controller of the iOS app (`RootViewController.preferredStatusBarStyle`).
 */
fun preferredStatusBarStyle(): UIStatusBarStyle = IosStatusBar.style ?: UIStatusBarStyleDefault
