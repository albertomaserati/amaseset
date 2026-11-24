package com.amasesoft.amaseset.ui.run

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import com.amasesoft.amaseset.domain.PhaseType
import com.amasesoft.amaseset.theme.PhaseColors
import com.amasesoft.amaseset.theme.desaturated

/** Background style of the run screen. */
enum class RunBackground {
    /** The whole screen takes the phase color; text, ring and controls in the phase "ink". */
    FullColor,

    /** The theme background lightly tinted with the phase; ring and phase label in the phase color. */
    Dark,
}

/** Single switch for the run screen background (see [RunBackground]). */
internal val RUN_BACKGROUND = RunBackground.FullColor

/** How much a paused screen loses its color. */
private const val PAUSED_DESATURATION = 0.7f

/** Tint of the theme background in the [RunBackground.Dark] style. */
private const val DARK_TINT = 0.14f
private const val DARK_TINT_PAUSED = 0.06f

/** Resolved colors of the run screen for one phase/status. */
@Immutable
data class RunColors(
    val background: Color,
    /** Text, icons and controls. */
    val ink: Color,
    /** Ring, progress bar and phase label. */
    val accent: Color,
    /** Unfilled part of the ring and of the progress bar. */
    val track: Color,
) {
    /** Dark text means a light background: the status bar needs dark icons. */
    val isLightBackground: Boolean get() = ink.luminance() < 0.5f
}

/**
 * Pure color choice of the run screen. [phase] is the current step (null before the first one);
 * the finished screen uses the brand fill [PhaseColors.done].
 */
fun runColors(
    style: RunBackground,
    phase: PhaseType?,
    status: RunStatus,
    phaseColors: PhaseColors,
    themeBackground: Color,
    themeOnBackground: Color,
    themePrimary: Color,
): RunColors {
    val finished = status == RunStatus.Finished
    val paused = status == RunStatus.Paused
    val phaseColor = phaseColors.of(phase ?: PhaseType.Prepare)
    return when (style) {
        RunBackground.FullColor -> {
            val fill = when {
                finished -> phaseColors.done
                paused -> phaseColor.desaturated(PAUSED_DESATURATION)
                else -> phaseColor
            }
            val ink = if (finished) phaseColors.onDone else phaseColors.ink
            RunColors(background = fill, ink = ink, accent = ink, track = ink.copy(alpha = 0.25f))
        }

        RunBackground.Dark -> {
            val accent = when {
                finished -> themePrimary
                paused -> phaseColor.desaturated(PAUSED_DESATURATION)
                else -> phaseColor
            }
            RunColors(
                background = lerp(themeBackground, accent, if (paused) DARK_TINT_PAUSED else DARK_TINT),
                ink = themeOnBackground,
                accent = accent,
                track = accent.copy(alpha = 0.2f),
            )
        }
    }
}
