package com.amasesoft.amaseset.ui.run

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.amasesoft.amaseset.domain.PhaseType
import com.amasesoft.amaseset.domain.Step
import com.amasesoft.amaseset.domain.formatSeconds
import com.amasesoft.amaseset.platform.KeepScreenOn
import com.amasesoft.amaseset.platform.OverrideSystemBars
import com.amasesoft.amaseset.platform.rememberCuePlayer
import com.amasesoft.amaseset.theme.AppTextStyles
import com.amasesoft.amaseset.theme.AppThemeExtras
import com.amasesoft.amaseset.theme.CountdownTextStyle
import com.amasesoft.amaseset.theme.TabularNumbers
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.amasesoft.amaseset.resources.Res
import com.amasesoft.amaseset.resources.action_cancel
import com.amasesoft.amaseset.resources.action_close
import com.amasesoft.amaseset.resources.action_pause
import com.amasesoft.amaseset.resources.action_restart
import com.amasesoft.amaseset.resources.action_resume
import com.amasesoft.amaseset.resources.action_skip
import com.amasesoft.amaseset.resources.action_stop
import com.amasesoft.amaseset.resources.ic_arrow_back
import com.amasesoft.amaseset.resources.ic_pause
import com.amasesoft.amaseset.resources.ic_play
import com.amasesoft.amaseset.resources.ic_replay
import com.amasesoft.amaseset.resources.ic_skip_next
import com.amasesoft.amaseset.resources.ic_stop
import com.amasesoft.amaseset.resources.phase_prepare
import com.amasesoft.amaseset.resources.phase_rest
import com.amasesoft.amaseset.resources.phase_set_rest
import com.amasesoft.amaseset.resources.phase_work
import com.amasesoft.amaseset.resources.quick_workout
import com.amasesoft.amaseset.resources.run_done
import com.amasesoft.amaseset.resources.run_last_activity
import com.amasesoft.amaseset.resources.run_next
import com.amasesoft.amaseset.resources.run_paused
import com.amasesoft.amaseset.resources.run_progress
import com.amasesoft.amaseset.resources.run_stop_message
import com.amasesoft.amaseset.resources.run_stop_title
import com.amasesoft.amaseset.resources.run_total_remaining

@Composable
fun RunScreen(
    viewModel: RunViewModel,
    soundEnabled: Boolean,
    tenSecondWarning: Boolean,
    onClose: () -> Unit,
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    val cuePlayer = rememberCuePlayer()
    val haptics = LocalHapticFeedback.current
    val currentSoundEnabled by rememberUpdatedState(soundEnabled)
    val currentTenSecondWarning by rememberUpdatedState(tenSecondWarning)
    val currentOnClose by rememberUpdatedState(onClose)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                RunEffect.CountdownCue -> {
                    if (currentSoundEnabled) cuePlayer.countdown()
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }

                RunEffect.TenSecondsCue -> if (currentTenSecondWarning) {
                    if (currentSoundEnabled) cuePlayer.tenSecondsLeft()
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                is RunEffect.PhaseStartCue -> {
                    if (currentSoundEnabled) cuePlayer.phaseStart()
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                RunEffect.FinishedCue -> {
                    if (currentSoundEnabled) cuePlayer.finished()
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                RunEffect.Close -> currentOnClose()
            }
        }
    }

    // Background policy (both platforms): pause when the screen is no longer visible (app in
    // background, screen off, another destination on top). Resuming is always manual, so the
    // user never misses part of a workout and no beeps play from the background.
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.onIntent(RunIntent.Pause)
    }

    RunContent(stateHolder = uiState, onIntent = viewModel::onIntent)
}

/**
 * The state changes ~20 times per second; to keep recomposition at ~1 per second, children
 * read it through [derivedStateOf] and the progress indicators read it in the draw phase.
 */
@Composable
private fun RunContent(
    stateHolder: State<RunUiState>,
    onIntent: (RunIntent) -> Unit,
) {
    val status by remember { derivedStateOf { stateHolder.value.status } }
    val currentStep by remember { derivedStateOf { stateHolder.value.currentStep } }
    val nextStep by remember { derivedStateOf { stateHolder.value.nextStep } }
    val title by remember { derivedStateOf { stateHolder.value.title } }
    var showStopDialog by remember { mutableStateOf(false) }

    KeepScreenOn(enabled = status == RunStatus.Running)

    val requestStop = {
        if (status == RunStatus.Finished) onIntent(RunIntent.Stop) else showStopDialog = true
    }
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = status != RunStatus.Finished,
        onBackCompleted = { showStopDialog = true },
    )

    // Colors: a pure choice (see runColors), animated between phases and on pause.
    val phaseColors = AppThemeExtras.phaseColors
    val scheme = MaterialTheme.colorScheme
    val phaseType = currentStep?.type
    val target = remember(status, phaseType, phaseColors, scheme) {
        runColors(RUN_BACKGROUND, phaseType, status, phaseColors, scheme.background, scheme.onBackground, scheme.primary)
    }
    val colorSpec = tween<Color>(500)
    val colors = RunColors(
        background = animateColorAsState(target.background, colorSpec, label = "runBackground").value,
        ink = animateColorAsState(target.ink, colorSpec, label = "runInk").value,
        accent = animateColorAsState(target.accent, colorSpec, label = "runAccent").value,
        track = animateColorAsState(target.track, colorSpec, label = "runTrack").value,
    )
    // Dark status bar icons on a light phase color, light icons otherwise.
    OverrideSystemBars(darkBackground = !target.isLightBackground)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RunTopBar(
            title = title ?: stringResource(Res.string.quick_workout),
            stateHolder = stateHolder,
            showTotal = status != RunStatus.Finished,
            ink = colors.ink,
            onClose = requestStop,
        )

        val totalSets = remember(stateHolder) { stateHolder.value.totalSets }
        val totalCycles = remember(stateHolder) { stateHolder.value.totalCycles }

        val ring: @Composable (Modifier) -> Unit = { modifier ->
            // The ring is a square sized from the space actually available (the shorter
            // side, with a margin), so it never overflows on short screens / landscape.
            BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
                val side = (minOf(maxWidth, maxHeight) * 0.92f).coerceIn(0.dp, MaxRingSize)
                ProgressRing(
                    progress = {
                        val s = stateHolder.value
                        if (s.status == RunStatus.Finished) 1f else 1f - s.stepProgress
                    },
                    color = colors.accent,
                    trackColor = colors.track,
                    modifier = Modifier.size(side),
                ) {
                    if (status == RunStatus.Finished) {
                        FinishedContent(ink = colors.ink, ringSize = side)
                    } else {
                        PhaseContent(
                            stateHolder = stateHolder,
                            step = currentStep,
                            colors = colors,
                            paused = status == RunStatus.Paused,
                            ringSize = side,
                        )
                    }
                }
            }
        }
        val progressLine: @Composable () -> Unit = {
            if (status != RunStatus.Finished) {
                ProgressLine(stateHolder, totalSets, totalCycles, colors.ink)
            }
        }
        val details: @Composable () -> Unit = {
            if (status != RunStatus.Finished) {
                NextUp(nextStep = nextStep, ink = colors.ink)
                Spacer(Modifier.height(10.dp))
                TotalProgressBar(stateHolder = stateHolder, colors = colors)
                Spacer(Modifier.height(24.dp))
                RunControls(
                    paused = status == RunStatus.Paused,
                    ink = colors.ink,
                    onStop = requestStop,
                    onTogglePause = { onIntent(RunIntent.TogglePause) },
                    onSkip = { onIntent(RunIntent.Skip) },
                )
            } else {
                FinishedControls(
                    colors = colors,
                    onRestart = { onIntent(RunIntent.Restart) },
                    onClose = { onIntent(RunIntent.Stop) },
                )
            }
        }

        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (maxWidth > maxHeight) {
                // Landscape: ring on the left, information and controls on the right.
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    ring(Modifier.weight(1f).fillMaxHeight())
                    Column(
                        // Safety net for very short screens / large font scale.
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        progressLine()
                        Spacer(Modifier.height(16.dp))
                        details()
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(4.dp))
                    progressLine()
                    ring(Modifier.weight(1f).fillMaxWidth())
                    details()
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text(stringResource(Res.string.run_stop_title)) },
            text = { Text(stringResource(Res.string.run_stop_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showStopDialog = false
                    onIntent(RunIntent.Stop)
                }) { Text(stringResource(Res.string.action_stop)) }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) { Text(stringResource(Res.string.action_cancel)) }
            },
        )
    }
}

/** Close, title of the workout and total time left. */
@Composable
private fun RunTopBar(
    title: String,
    stateHolder: State<RunUiState>,
    showTotal: Boolean,
    ink: Color,
    onClose: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClose, colors = IconButtonDefaults.iconButtonColors(contentColor = ink)) {
            Icon(painterResource(Res.drawable.ic_arrow_back), stringResource(Res.string.action_close))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(start = 4.dp),
        )
        if (showTotal) {
            val totalSeconds by remember { derivedStateOf { stateHolder.value.position.displayTotalSeconds } }
            val formatted = formatSeconds(totalSeconds)
            val spoken = stringResource(Res.string.run_total_remaining) + ", " + formatted
            Text(
                text = formatted,
                style = MaterialTheme.typography.headlineSmall.merge(TabularNumbers),
                color = ink,
                modifier = Modifier.padding(start = 12.dp).semantics { contentDescription = spoken },
            )
        }
    }
}

/** "CYCLE 1 / 8 · SET 1 / 1" in condensed capitals. */
@Composable
private fun ProgressLine(stateHolder: State<RunUiState>, totalSets: Int, totalCycles: Int, ink: Color) {
    val setNumber by remember { derivedStateOf { stateHolder.value.currentStep?.setNumber ?: 0 } }
    val cycleNumber by remember { derivedStateOf { stateHolder.value.currentStep?.cycleNumber ?: 0 } }
    val text = stringResource(Res.string.run_progress, cycleNumber, totalCycles, setNumber, totalSets)
    Text(
        text = text.uppercase(),
        style = AppTextStyles.condensedLabel,
        color = ink,
        textAlign = TextAlign.Center,
        modifier = Modifier.semantics { contentDescription = text },
    )
}

/** Circular progress drawn with [Canvas]; [progress] is read in the draw phase only. */
@Composable
private fun ProgressRing(
    progress: () -> Float,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.minDimension * 0.05f
            val inset = strokeWidth / 2
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth),
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress().coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        content()
    }
}

@Composable
private fun PhaseContent(
    stateHolder: State<RunUiState>,
    step: Step?,
    colors: RunColors,
    paused: Boolean,
    ringSize: Dp,
) {
    val seconds by remember { derivedStateOf { stateHolder.value.position.displaySecondsInStep } }
    val phaseLabel = step?.let { phaseLabel(it.type) }.orEmpty()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(ringSize * RingContentPadding),
    ) {
        AnimatedContent(
            targetState = phaseLabel,
            transitionSpec = { (fadeIn(tween(250)) + scaleIn(initialScale = 0.8f)).togetherWith(fadeOut(tween(150))) },
            label = "phaseLabel",
        ) { label ->
            Text(
                text = label.uppercase(),
                style = AppTextStyles.condensedLabel.copy(fontSize = 30.sp, lineHeight = 34.sp).fitRing(ringSize, 0.085f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    contentDescription = label
                    heading()
                    liveRegion = LiveRegionMode.Polite
                },
            )
        }
        // Rest steps carry the name of the cycle just done: show it only while working
        // (during rests the "Next" line already announces the upcoming exercise).
        val cycleName = if (step?.type == PhaseType.Work) step.cycleName else ""
        if (cycleName.isNotEmpty()) {
            Text(
                text = cycleName,
                style = MaterialTheme.typography.titleMedium.fitRing(ringSize, 0.06f),
                color = colors.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        val text = if (seconds >= 60) formatSeconds(seconds) else seconds.toString()
        // Countdown digits scale with the ring (independent of the user font scale, which
        // would otherwise push them out of the circle); m:ss gets a smaller size.
        val style = countdownStyle(ringSize, longText = text.length > 3)
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                (slideInVertically(tween(200)) { -it / 3 } + fadeIn(tween(200)))
                    .togetherWith(slideOutVertically(tween(200)) { it / 3 } + fadeOut(tween(150)))
            },
            label = "countdown",
        ) { value ->
            Text(
                text = value,
                style = style,
                color = colors.ink,
                modifier = Modifier.semantics { contentDescription = value },
            )
        }
        AnimatedVisibility(visible = paused) {
            val pausedText = stringResource(Res.string.run_paused)
            Text(
                text = pausedText.uppercase(),
                style = AppTextStyles.condensedLabel.fitRing(ringSize, 0.06f),
                color = colors.ink,
                modifier = Modifier.semantics {
                    contentDescription = pausedText
                    liveRegion = LiveRegionMode.Polite
                },
            )
        }
    }
}

@Composable
private fun FinishedContent(ink: Color, ringSize: Dp) {
    val done = stringResource(Res.string.run_done)
    Text(
        text = done.uppercase(),
        style = CountdownTextStyle.copy(letterSpacing = 2.sp).fitRing(ringSize, 0.2f),
        color = ink,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier
            .padding(ringSize * RingContentPadding)
            .semantics {
                contentDescription = done
                heading()
                liveRegion = LiveRegionMode.Polite
            },
    )
}

/** "NEXT · REST 0:10" above the progress bar. */
@Composable
private fun NextUp(nextStep: Step?, ink: Color) {
    val text = if (nextStep == null) {
        stringResource(Res.string.run_last_activity)
    } else {
        stringResource(Res.string.run_next, stepLabel(nextStep), formatSeconds(nextStep.durationSeconds))
    }
    Text(
        text = text.uppercase(),
        style = AppTextStyles.condensedLabel,
        color = ink,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = text },
    )
}

/** Continuous bar of the whole workout; progress read in the draw phase. */
@Composable
private fun TotalProgressBar(stateHolder: State<RunUiState>, colors: RunColors) {
    LinearProgressIndicator(
        progress = { stateHolder.value.totalProgress },
        color = colors.accent,
        trackColor = colors.track,
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {},
        modifier = Modifier.fillMaxWidth().height(6.dp),
    )
}

@Composable
private fun RunControls(
    paused: Boolean,
    ink: Color,
    onStop: () -> Unit,
    onTogglePause: () -> Unit,
    onSkip: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LabeledIconButton(
            icon = Res.drawable.ic_stop,
            label = stringResource(Res.string.action_stop),
            ink = ink,
            onClick = onStop,
        )
        // The main control: bigger, in an outlined circle.
        val pauseLabel = stringResource(if (paused) Res.string.action_resume else Res.string.action_pause)
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .border(2.dp, ink, CircleShape)
                .clickable(role = Role.Button, onClick = onTogglePause)
                .semantics { contentDescription = pauseLabel },
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(targetState = paused, label = "pauseIcon") { isPaused ->
                Icon(
                    painter = painterResource(if (isPaused) Res.drawable.ic_play else Res.drawable.ic_pause),
                    contentDescription = null,
                    tint = ink,
                    modifier = Modifier.size(44.dp),
                )
            }
        }
        LabeledIconButton(
            icon = Res.drawable.ic_skip_next,
            label = stringResource(Res.string.action_skip),
            ink = ink,
            onClick = onSkip,
        )
    }
}

/** Icon with a small label under it, no container; at least 64×64 dp to tap. */
@Composable
private fun LabeledIconButton(icon: DrawableResource, label: String, ink: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .sizeIn(minWidth = 72.dp, minHeight = 64.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = ink, modifier = Modifier.size(30.dp))
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = ink,
            maxLines = 1,
        )
    }
}

@Composable
private fun FinishedControls(colors: RunColors, onRestart: () -> Unit, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Labels stay on one line and shrink if needed (narrow phones, large font scale).
        OutlinedButton(
            onClick = onRestart,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.5.dp, colors.ink),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.ink),
            modifier = Modifier.weight(1f).heightIn(min = 56.dp),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(
                painterResource(Res.drawable.ic_replay),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            SingleLineButtonLabel(stringResource(Res.string.action_restart))
        }
        Button(
            onClick = onClose,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.background),
            modifier = Modifier.weight(1f).heightIn(min = 56.dp),
        ) {
            SingleLineButtonLabel(stringResource(Res.string.action_close))
        }
    }
}

@Composable
private fun SingleLineButtonLabel(text: String) {
    Text(
        text = text,
        maxLines = 1,
        softWrap = false,
        autoSize = TextAutoSize.StepBased(
            minFontSize = 11.sp,
            maxFontSize = MaterialTheme.typography.labelLarge.fontSize,
        ),
    )
}

/** Name of a step for the user: the exercise name for named work, the phase otherwise. */
@Composable
internal fun stepLabel(step: Step): String =
    if (step.type == PhaseType.Work && step.cycleName.isNotEmpty()) step.cycleName else phaseLabel(step.type)

@Composable
internal fun phaseLabel(type: PhaseType): String = stringResource(
    when (type) {
        PhaseType.Prepare -> Res.string.phase_prepare
        PhaseType.Work -> Res.string.phase_work
        PhaseType.Rest -> Res.string.phase_rest
        PhaseType.SetRest -> Res.string.phase_set_rest
    },
)

private val MaxRingSize = 360.dp

/** Inner padding of the ring content, as a fraction of the ring side (clears the stroke). */
private const val RingContentPadding = 0.12f

/**
 * Caps a text style so that its font size is at most [fraction] of the ring side: on a big
 * ring the theme size is kept, on a small one the text shrinks with the ring.
 */
@Composable
private fun TextStyle.fitRing(ringSize: Dp, fraction: Float): TextStyle {
    val maxSize = with(LocalDensity.current) { (ringSize * fraction).toSp() }
    return if (fontSize.isSp && fontSize.value > maxSize.value) {
        copy(fontSize = maxSize, lineHeight = maxSize * 1.2f)
    } else {
        this
    }
}

/** Countdown digits sized from the ring side: about 42% for "20", 30% for "1:30" (condensed digits). */
@Composable
private fun countdownStyle(ringSize: Dp, longText: Boolean): TextStyle {
    val size = with(LocalDensity.current) { (ringSize * if (longText) 0.30f else 0.42f).toSp() }
    return CountdownTextStyle.copy(fontSize = size, lineHeight = size)
}
