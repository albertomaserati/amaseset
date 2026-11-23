package com.amasesoft.amaseset.domain

/**
 * Compact one-line description of a configuration: `"1 × 8 · 20/10"`, i.e.
 * sets × cycles · work/rest. Advanced workouts have no single work/rest pair, so
 * [customLabel] (e.g. "custom") takes its place: `"2 × 5 · custom"`.
 */
fun WorkoutConfig.compactSummary(customLabel: String): String {
    val times = when (val p = plan) {
        is CyclePlan.Uniform -> "${compactSeconds(p.workSeconds)}/${compactSeconds(p.restSeconds)}"
        is CyclePlan.Custom -> customLabel
    }
    return "$sets × ${plan.count} · $times"
}

/** Seconds as a bare number below one minute ("20"), as [formatSeconds] from one minute on ("1:30"). */
fun compactSeconds(seconds: Int): String =
    if (seconds in 0 until 60) seconds.toString() else formatSeconds(seconds)
