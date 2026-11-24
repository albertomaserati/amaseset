package com.amasesoft.amaseset.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import kotlin.math.pow

/** WCAG 2 contrast ratio between two opaque colors (1..21). */
fun contrastRatio(a: Color, b: Color): Float {
    val la = a.luminance()
    val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * Moves this color towards the gray of the same relative luminance by [amount] (0..1): it looks
 * visibly duller, while text contrast against it stays about the same.
 */
fun Color.desaturated(amount: Float): Color {
    val l = luminance().toDouble()
    // Inverse of the sRGB transfer function: the gray level with luminance l.
    val g = if (l <= 0.0031308) 12.92 * l else 1.055 * l.pow(1 / 2.4) - 0.055
    val gray = Color(g.toFloat(), g.toFloat(), g.toFloat(), alpha)
    return lerp(this, gray, amount.coerceIn(0f, 1f))
}
