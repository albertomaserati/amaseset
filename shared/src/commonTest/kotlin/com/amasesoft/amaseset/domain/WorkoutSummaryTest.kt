package com.amasesoft.amaseset.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class WorkoutSummaryTest {

    @Test
    fun uniformShowsSetsCyclesWorkAndRest() {
        assertEquals("1 × 8 · 20/10", WorkoutConfig.Default.compactSummary("custom"))
        val hiit = WorkoutConfig(sets = 2, plan = CyclePlan.Uniform(workSeconds = 40, restSeconds = 20, count = 6))
        assertEquals("2 × 6 · 40/20", hiit.compactSummary("custom"))
    }

    @Test
    fun durationsFromOneMinuteUseMinutesAndSeconds() {
        val config = WorkoutConfig(sets = 3, plan = CyclePlan.Uniform(workSeconds = 90, restSeconds = 60, count = 4))
        assertEquals("3 × 4 · 1:30/1:00", config.compactSummary("custom"))
        val noRest = WorkoutConfig(plan = CyclePlan.Uniform(workSeconds = 59, restSeconds = 0, count = 1))
        assertEquals("1 × 1 · 59/0", noRest.compactSummary("custom"))
    }

    @Test
    fun advancedUsesTheCustomLabel() {
        val config = WorkoutConfig(
            sets = 2,
            plan = CyclePlan.Custom(
                listOf(Cycle("Squat", 30, 15), Cycle("Plank", 45, 15), Cycle("Burpees", 20, 0)),
            ),
        )
        assertEquals("2 × 3 · personalizzato", config.compactSummary("personalizzato"))
    }

    @Test
    fun compactSecondsSwitchesToMinutesAtSixty() {
        assertEquals("0", compactSeconds(0))
        assertEquals("59", compactSeconds(59))
        assertEquals("1:00", compactSeconds(60))
        assertEquals("1:00:00", compactSeconds(3600))
    }
}
