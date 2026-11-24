package com.amasesoft.amaseset.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Aligns the system bar icons with the in-app background (which may differ from the system
 * theme): light icons when [darkTheme] (a dark background), dark icons otherwise.
 * Android: window insets controller. iOS: application status bar style.
 */
@Composable
expect fun SystemBarsAppearance(darkTheme: Boolean)

/**
 * Lets a screen with its own background (the run screen on a phase color) decide the system
 * bar icons while it is shown; `null` = follow the app theme. Provided by `App`.
 */
@Stable
class SystemBarsOverride {
    var darkBackground: Boolean? by mutableStateOf(null)
}

val LocalSystemBarsOverride = staticCompositionLocalOf { SystemBarsOverride() }

/** While in composition, the system bar icons follow [darkBackground] instead of the theme. */
@Composable
fun OverrideSystemBars(darkBackground: Boolean) {
    val override = LocalSystemBarsOverride.current
    DisposableEffect(override, darkBackground) {
        override.darkBackground = darkBackground
        onDispose { override.darkBackground = null }
    }
}
