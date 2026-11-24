package com.amasesoft.amaseset.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.amasesoft.amaseset.domain.PhaseType

// Colors aligned with the AM logo: teal primary, navy secondary, green tertiary.
private val LightColorScheme = lightColorScheme(
    primary = LogoTeal,
    onPrimary = Color.White,
    primaryContainer = Teal90,
    onPrimaryContainer = Teal10,
    secondary = LogoNavy,
    onSecondary = Color.White,
    secondaryContainer = Navy90,
    onSecondaryContainer = Navy10,
    tertiary = LogoGreen,
    onTertiary = Color.White,
    tertiaryContainer = Green90,
    onTertiaryContainer = Green10,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightBackground,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceContainerHighest,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    // Snackbars (e.g. the "Undo" action): brand-tinted instead of the baseline purple.
    inverseSurface = LightOnBackground,
    inverseOnSurface = LightSurfaceContainer,
    inversePrimary = Teal80,
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = Teal20,
    primaryContainer = LogoTeal,
    onPrimaryContainer = Teal90,
    secondary = Navy80,
    onSecondary = Navy20,
    secondaryContainer = Navy30,
    onSecondaryContainer = Navy90,
    tertiary = Green80,
    onTertiary = Green10,
    tertiaryContainer = Green30,
    onTertiaryContainer = Green90,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkBackground,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceContainerHighest,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkOnBackground,
    inverseOnSurface = DarkSurfaceContainer,
    inversePrimary = LogoTeal,
)

/**
 * 8 dp corners for cards, FAB, text fields, chips, menus and snackbars; dialogs (extraLarge) a
 * bit rounder. Buttons and segmented buttons default to fully rounded shapes in Material 3, so
 * they get [MaterialTheme.shapes].small explicitly where they are used.
 */
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(16.dp),
)

/**
 * Color of each workout phase, plus the "ink" for text and icons drawn *on* a phase color
 * (the run screen fills its background with it) and the brand fill of the finished screen.
 */
@Immutable
data class PhaseColors(
    val prepare: Color,
    val work: Color,
    val rest: Color,
    val setRest: Color,
    /** Text/icons on any phase color: dark on the light tones of the dark theme, white otherwise. */
    val ink: Color,
    /** Background of the finished screen (brand teal) and its ink. */
    val done: Color,
    val onDone: Color,
) {
    fun of(type: PhaseType): Color = when (type) {
        PhaseType.Prepare -> prepare
        PhaseType.Work -> work
        PhaseType.Rest -> rest
        PhaseType.SetRest -> setRest
    }
}

internal val LightPhaseColors = PhaseColors(
    prepare = Amber35,
    work = Coral35,
    rest = Green35,
    setRest = Navy40,
    ink = Color.White,
    done = LogoTeal,
    onDone = Color.White,
)
internal val DarkPhaseColors = PhaseColors(
    prepare = Amber80,
    work = Coral80,
    rest = Green80,
    setRest = Navy80,
    ink = Navy10,
    done = LogoTeal,
    onDone = Color.White,
)

val LocalPhaseColors = staticCompositionLocalOf { LightPhaseColors }

/** Theme extensions not covered by [MaterialTheme]. */
object AppThemeExtras {
    val phaseColors: PhaseColors
        @Composable get() = LocalPhaseColors.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val phaseColors = if (darkTheme) DarkPhaseColors else LightPhaseColors

    CompositionLocalProvider(
        LocalPhaseColors provides phaseColors,
        LocalCondensedFontFamily provides rememberCondensedFontFamily(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = AppShapes,
            content = content
        )
    }
}
