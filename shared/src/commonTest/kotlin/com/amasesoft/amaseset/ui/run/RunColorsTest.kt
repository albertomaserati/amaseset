package com.amasesoft.amaseset.ui.run

import androidx.compose.ui.graphics.Color
import com.amasesoft.amaseset.data.RunBackground
import com.amasesoft.amaseset.domain.PhaseType
import com.amasesoft.amaseset.theme.DarkBackground
import com.amasesoft.amaseset.theme.DarkOnBackground
import com.amasesoft.amaseset.theme.DarkPhaseColors
import com.amasesoft.amaseset.theme.LightBackground
import com.amasesoft.amaseset.theme.LightOnBackground
import com.amasesoft.amaseset.theme.LightPhaseColors
import com.amasesoft.amaseset.theme.LogoTeal
import com.amasesoft.amaseset.theme.PhaseColors
import com.amasesoft.amaseset.theme.Teal80
import com.amasesoft.amaseset.theme.contrastRatio
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RunColorsTest {

    private data class Theme(val name: String, val phases: PhaseColors, val background: Color, val onBackground: Color, val primary: Color)

    private val light = Theme("light", LightPhaseColors, LightBackground, LightOnBackground, LogoTeal)
    private val dark = Theme("dark", DarkPhaseColors, DarkBackground, DarkOnBackground, Teal80)

    private fun Theme.colors(style: RunBackground, phase: PhaseType?, status: RunStatus) =
        runColors(style, phase, status, phases, background, onBackground, primary)

    private fun Float.r2() = (this * 100).toInt() / 100f

    @Test
    fun fullColorInkReachesSmallTextContrastOnEveryPhase() {
        for (theme in listOf(light, dark)) {
            for (phase in PhaseType.entries) {
                for (status in listOf(RunStatus.Running, RunStatus.Paused)) {
                    val c = theme.colors(RunBackground.FullColor, phase, status)
                    val ratio = contrastRatio(c.ink, c.background)
                    println("FullColor ${theme.name} $phase $status: ${ratio.r2()}:1")
                    assertTrue(ratio >= 4.5f, "${theme.name} $phase $status: $ratio")
                    assertEquals(c.ink, c.accent)
                }
            }
            val done = theme.colors(RunBackground.FullColor, PhaseType.Work, RunStatus.Finished)
            val ratio = contrastRatio(done.ink, done.background)
            println("FullColor ${theme.name} finished: ${ratio.r2()}:1")
            assertEquals(theme.phases.done, done.background)
            assertTrue(ratio >= 4.5f, "${theme.name} finished: $ratio")
        }
    }

    @Test
    fun inkIsDarkOnTheLightTonesOfTheDarkThemeAndWhiteInTheLightTheme() {
        val onDark = dark.colors(RunBackground.FullColor, PhaseType.Work, RunStatus.Running)
        assertTrue(onDark.isLightBackground) // light coral, dark ink -> dark status bar icons
        val onLight = light.colors(RunBackground.FullColor, PhaseType.Work, RunStatus.Running)
        assertEquals(Color.White, onLight.ink)
        assertFalse(onLight.isLightBackground)
        // The finished screen is the brand teal with white ink in both themes.
        assertFalse(dark.colors(RunBackground.FullColor, null, RunStatus.Finished).isLightBackground)
    }

    @Test
    fun pausedIsVisiblyDuller() {
        for (theme in listOf(light, dark)) {
            for (phase in PhaseType.entries) {
                val running = theme.colors(RunBackground.FullColor, phase, RunStatus.Running).background
                val paused = theme.colors(RunBackground.FullColor, phase, RunStatus.Paused).background
                assertTrue(saturation(paused) < saturation(running) * 0.6f, "${theme.name} $phase")
            }
        }
    }

    @Test
    fun darkStyleKeepsTheThemeInkAndAReadableAccent() {
        for (theme in listOf(light, dark)) {
            for (phase in PhaseType.entries) {
                val c = theme.colors(RunBackground.Dark, phase, RunStatus.Running)
                assertEquals(theme.onBackground, c.ink)
                val text = contrastRatio(c.ink, c.background)
                val accent = contrastRatio(c.accent, c.background)
                println("Dark ${theme.name} $phase: ink ${text.r2()}:1, accent ${accent.r2()}:1")
                assertTrue(text >= 4.5f, "${theme.name} $phase ink: $text")
                // The accent is used for the large phase label and the ring.
                assertTrue(accent >= 3f, "${theme.name} $phase accent: $accent")
            }
        }
    }

    @Test
    fun beforeTheFirstStepThePrepareColorIsUsed() {
        assertEquals(
            dark.colors(RunBackground.FullColor, PhaseType.Prepare, RunStatus.Running),
            dark.colors(RunBackground.FullColor, null, RunStatus.Running),
        )
    }

    /** HSV saturation, enough to compare a color with its duller version. */
    private fun saturation(c: Color): Float {
        val max = maxOf(c.red, c.green, c.blue)
        val min = minOf(c.red, c.green, c.blue)
        return if (max == 0f) 0f else abs(max - min) / max
    }
}
