package com.amasesoft.amaseset.ui.routines

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.amasesoft.amaseset.components.LoadErrorState
import com.amasesoft.amaseset.components.RowDivider
import com.amasesoft.amaseset.components.WeightFormat
import com.amasesoft.amaseset.components.WholeWordsText
import com.amasesoft.amaseset.components.rememberWeightFormat
import com.amasesoft.amaseset.domain.MAX_EXERCISES_PER_ROUTINE
import com.amasesoft.amaseset.domain.MAX_ROUTINES
import com.amasesoft.amaseset.domain.Progression
import com.amasesoft.amaseset.theme.AppTextStyles
import com.amasesoft.amaseset.theme.AppTheme
import com.amasesoft.amaseset.theme.CondensedFontFamily
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.amasesoft.amaseset.resources.Res
import com.amasesoft.amaseset.resources.a11y_apply
import com.amasesoft.amaseset.resources.a11y_move_down
import com.amasesoft.amaseset.resources.a11y_move_up
import com.amasesoft.amaseset.resources.a11y_no_weight
import com.amasesoft.amaseset.resources.a11y_progression
import com.amasesoft.amaseset.resources.a11y_progression_increase_state
import com.amasesoft.amaseset.resources.a11y_progression_repeat_state
import com.amasesoft.amaseset.resources.a11y_reps
import com.amasesoft.amaseset.resources.a11y_sets
import com.amasesoft.amaseset.resources.action_apply
import com.amasesoft.amaseset.resources.action_done
import com.amasesoft.amaseset.resources.action_reorder
import com.amasesoft.amaseset.resources.action_undo
import com.amasesoft.amaseset.resources.exercise_add
import com.amasesoft.amaseset.resources.exercise_limit_reached
import com.amasesoft.amaseset.resources.exercise_next_short
import com.amasesoft.amaseset.resources.exercise_sets_reps
import com.amasesoft.amaseset.resources.exercise_weight_updated
import com.amasesoft.amaseset.resources.ic_add
import com.amasesoft.amaseset.resources.ic_arrow_down
import com.amasesoft.amaseset.resources.ic_arrow_up
import com.amasesoft.amaseset.resources.ic_check
import com.amasesoft.amaseset.resources.ic_fitness
import com.amasesoft.amaseset.resources.ic_list
import com.amasesoft.amaseset.resources.ic_reorder
import com.amasesoft.amaseset.resources.ic_settings
import com.amasesoft.amaseset.resources.progression_increase_letter
import com.amasesoft.amaseset.resources.progression_repeat_letter
import com.amasesoft.amaseset.resources.routine_empty_message
import com.amasesoft.amaseset.resources.routine_empty_title
import com.amasesoft.amaseset.resources.routine_limit_reached
import com.amasesoft.amaseset.resources.routines_col_exercise
import com.amasesoft.amaseset.resources.routines_col_sets
import com.amasesoft.amaseset.resources.routines_empty_message
import com.amasesoft.amaseset.resources.routines_empty_title
import com.amasesoft.amaseset.resources.routines_load_error
import com.amasesoft.amaseset.resources.routines_manage
import com.amasesoft.amaseset.resources.routines_new
import com.amasesoft.amaseset.resources.routines_selector_label
import com.amasesoft.amaseset.resources.routines_title
import com.amasesoft.amaseset.resources.settings_title
import com.amasesoft.amaseset.resources.storage_error
import com.amasesoft.amaseset.resources.unit_kg

/** Up to this many routines the selector is a segmented row, then a dropdown. */
private const val SEGMENTED_MAX = 3

// Fixed widths of the number columns, so values line up from row to row.
private val SetsColumnWidth = 60.dp
private val WeightColumnWidth = 56.dp
private val ProgressionColumnWidth = 48.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    viewModel: RoutinesViewModel,
    onAddExercise: (routineId: Long) -> Unit,
    onEditExercise: (routineId: Long, exerciseId: Long) -> Unit,
    onManage: () -> Unit,
    onSettings: () -> Unit,
    bottomBar: @Composable () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val weightFormat = rememberWeightFormat()
    val currentWeightFormat by rememberUpdatedState(weightFormat)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                RoutinesEvent.StorageError -> snackbarHostState.showSnackbar(getString(Res.string.storage_error))
                RoutinesEvent.RoutineLimitReached ->
                    snackbarHostState.showSnackbar(getString(Res.string.routine_limit_reached, MAX_ROUTINES))

                RoutinesEvent.ExerciseLimitReached ->
                    snackbarHostState.showSnackbar(getString(Res.string.exercise_limit_reached, MAX_EXERCISES_PER_ROUTINE))

                is RoutinesEvent.IncreaseApplied -> {
                    // Launched so that further events are not queued behind this snackbar;
                    // a newer "Apply" replaces the previous message.
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val message = getString(
                        Res.string.exercise_weight_updated,
                        currentWeightFormat.withUnit(event.applied.newWeightHalfKg),
                    )
                    val undo = getString(Res.string.action_undo)
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(message, actionLabel = undo, duration = SnackbarDuration.Long)
                        if (result == SnackbarResult.ActionPerformed) viewModel.undoIncrease(event.applied)
                    }
                }
            }
        }
    }

    // System back leaves the reorder mode first.
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = state.isReordering,
        onBackCompleted = { viewModel.setReordering(false) },
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.routines_title)) },
                actions = {
                    if (state.isReordering) {
                        // Explicit, labelled way out of the reorder mode.
                        TextButton(onClick = { viewModel.setReordering(false) }) {
                            Icon(painterResource(Res.drawable.ic_check), contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(Res.string.action_done))
                        }
                    } else {
                        if (state.isListAvailable && state.exercises.size > 1) {
                            IconButton(onClick = { viewModel.setReordering(true) }) {
                                Icon(painterResource(Res.drawable.ic_reorder), stringResource(Res.string.action_reorder))
                            }
                        }
                        if (state.isListAvailable && state.routines.isNotEmpty()) {
                            IconButton(onClick = onManage) {
                                Icon(painterResource(Res.drawable.ic_list), stringResource(Res.string.routines_manage))
                            }
                        }
                        IconButton(onClick = onSettings) {
                            Icon(painterResource(Res.drawable.ic_settings), stringResource(Res.string.settings_title))
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            val selected = state.selected
            if (state.isListAvailable && selected != null && !state.isReordering) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (state.canAddExercise) {
                            onAddExercise(selected.id)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    getString(Res.string.exercise_limit_reached, MAX_EXERCISES_PER_ROUTINE),
                                )
                            }
                        }
                    },
                    icon = { Icon(painterResource(Res.drawable.ic_add), contentDescription = null) },
                    text = { Text(stringResource(Res.string.exercise_add)) },
                )
            }
        },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        // Full-width rows separated by dividers; the rows pad their own content.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 96.dp,
            ),
        ) {
            when {
                state.isLoading -> item(key = "loading") {
                    Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                    }
                }

                state.loadError -> item(key = "error") {
                    LoadErrorState(
                        message = stringResource(Res.string.routines_load_error),
                        onRetry = viewModel::reload,
                        modifier = Modifier.animateItem(),
                    )
                }

                state.routines.isEmpty() -> item(key = "noRoutines") {
                    NoRoutinesState(onCreate = viewModel::openNewRoutine, modifier = Modifier.animateItem())
                }

                else -> {
                    item(key = "selector") {
                        RoutineSelector(
                            routines = state.routines,
                            selectedId = state.selectedId,
                            onSelect = viewModel::select,
                            onManage = onManage,
                            modifier = Modifier.padding(horizontal = 16.dp).animateItem(),
                        )
                    }
                    val selected = state.selected
                    if (selected != null && state.exercises.isEmpty()) {
                        item(key = "noExercises") {
                            NoExercisesState(routineName = selected.name, modifier = Modifier.animateItem())
                        }
                    }
                    // The column labels do not apply to the reorder arrows.
                    if (state.exercises.isNotEmpty() && !state.isReordering) {
                        item(key = "columns") { ColumnsHeader(Modifier.animateItem()) }
                    }
                    itemsIndexed(state.exercises, key = { _, item -> item.id }) { index, item ->
                        Column(Modifier.animateItem()) {
                            if (index > 0) RowDivider()
                            ExerciseRow(
                                item = item,
                                weightFormat = weightFormat,
                                isReordering = state.isReordering,
                                isFirst = index == 0,
                                isLast = index == state.exercises.lastIndex,
                                onClick = { selected?.let { onEditExercise(it.id, item.id) } },
                                onToggleProgression = { viewModel.toggleProgression(item.id) },
                                onApply = { viewModel.applyIncrease(item.id) },
                                onMove = { offset -> viewModel.moveExercise(item.id, offset) },
                            )
                        }
                    }
                }
            }
        }
    }

    state.nameDialog?.let { dialog ->
        RoutineNameDialogView(
            dialog = dialog,
            onNameChange = viewModel::setDialogName,
            onConfirm = viewModel::submitDialog,
            onDismiss = viewModel::dismissDialog,
        )
    }
}

/**
 * Active routine: segmented buttons up to [SEGMENTED_MAX] routines (one tap to switch), a
 * dropdown beyond, which also leads to "Manage routines".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoutineSelector(
    routines: List<RoutineSummary>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onManage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelStyle = MaterialTheme.typography.titleMedium.copy(fontFamily = CondensedFontFamily, fontSize = 18.sp)
    if (routines.size <= SEGMENTED_MAX) {
        SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
            routines.forEachIndexed { index, routine ->
                SegmentedButton(
                    selected = routine.id == selectedId,
                    onClick = { onSelect(routine.id) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = routines.size, baseShape = MaterialTheme.shapes.small),
                    // No check mark: the space goes to the name, the selection is shown by the color.
                    icon = {},
                    modifier = Modifier.weight(1f),
                ) {
                    Text(routine.name, style = labelStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    } else {
        var expanded by remember { mutableStateOf(false) }
        val selected = routines.firstOrNull { it.id == selectedId }
        val selectorLabel = stringResource(Res.string.routines_selector_label)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = modifier.fillMaxWidth(),
        ) {
            // The routine name as a condensed uppercase title that opens the menu.
            Row(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .semantics(mergeDescendants = true) {
                        role = Role.DropdownList
                        contentDescription = "$selectorLabel, ${selected?.name.orEmpty()}"
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selected?.name.orEmpty().uppercase(),
                    style = AppTextStyles.condensedTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false).clearAndSetSemantics { },
                )
                Spacer(Modifier.width(4.dp))
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                routines.forEach { routine ->
                    DropdownMenuItem(
                        text = { Text(routine.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        onClick = {
                            expanded = false
                            onSelect(routine.id)
                        },
                        trailingIcon = if (routine.id == selectedId) {
                            { Icon(painterResource(Res.drawable.ic_check), contentDescription = null) }
                        } else {
                            null
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.routines_manage)) },
                    leadingIcon = { Icon(painterResource(Res.drawable.ic_list), contentDescription = null) },
                    onClick = {
                        expanded = false
                        onManage()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

/** "EXERCISE · SETS · KG": small spaced labels over the columns (visual only). */
@Composable
private fun ColumnsHeader(modifier: Modifier = Modifier) {
    val style = AppTextStyles.overline
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 20.dp, bottom = 4.dp)
            // Each row already says what its numbers are.
            .clearAndSetSemantics { },
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(stringResource(Res.string.routines_col_exercise).uppercase(), style = style, color = color, modifier = Modifier.weight(1f))
        Text(
            stringResource(Res.string.routines_col_sets).uppercase(),
            style = style,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.width(SetsColumnWidth),
        )
        Text(
            stringResource(Res.string.unit_kg).uppercase(),
            style = style,
            color = color,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier.width(WeightColumnWidth),
        )
        Spacer(Modifier.width(ProgressionColumnWidth))
    }
}

/**
 * name (+ description, + "Next 16 · Apply") | sets×reps | kg | A/R.
 * In reorder mode the number columns give way to up/down arrows.
 */
@Composable
private fun ExerciseRow(
    item: ExerciseItem,
    weightFormat: WeightFormat,
    isReordering: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onToggleProgression: () -> Unit,
    onApply: () -> Unit,
    onMove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(enabled = !isReordering, onClick = onClick)
            .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
        // Numbers line up with the first line of the name.
        verticalAlignment = Alignment.Top,
    ) {
        Column(Modifier.weight(1f)) {
            WholeWordsText(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
            )
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // Only with something to apply: Increase *and* a weight. The last value is kept
            // while the line animates out (after Apply the exercise is back to Repeat).
            // Plain (non-observable) holder: written during composition without triggering
            // another recomposition.
            val lastNext = remember { IntArray(1) { item.nextWeightHalfKg ?: 0 } }
            item.nextWeightHalfKg?.let { lastNext[0] = it }
            AnimatedVisibility(visible = item.nextWeightHalfKg != null && !isReordering) {
                lastNext[0].takeIf { it > 0 }?.let { next ->
                    NextWeightLine(
                        exerciseName = item.name,
                        nextWeight = weightFormat.number(next),
                        nextWeightSpoken = weightFormat.withUnit(next),
                        onApply = onApply,
                    )
                }
            }
        }
        AnimatedContent(
            targetState = isReordering,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            contentAlignment = Alignment.TopEnd,
            label = "exerciseColumns",
        ) { reordering ->
            if (reordering) {
                Row(Modifier.offset(y = (-12).dp)) {
                    IconButton(onClick = { onMove(-1) }, enabled = !isFirst) {
                        Icon(painterResource(Res.drawable.ic_arrow_up), stringResource(Res.string.a11y_move_up, item.name))
                    }
                    IconButton(onClick = { onMove(1) }, enabled = !isLast) {
                        Icon(painterResource(Res.drawable.ic_arrow_down), stringResource(Res.string.a11y_move_down, item.name))
                    }
                }
            } else {
                NumberColumns(item = item, weightFormat = weightFormat, onToggleProgression = onToggleProgression)
            }
        }
    }
}

@Composable
private fun NumberColumns(item: ExerciseItem, weightFormat: WeightFormat, onToggleProgression: () -> Unit) {
    val numberStyle = AppTextStyles.numberSmall
    // Values shrink rather than wrap (large font scale) so the columns stay aligned.
    val autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = numberStyle.fontSize)
    val spoken = listOfNotNull(
        pluralStringResource(Res.plurals.a11y_sets, item.sets, item.sets),
        pluralStringResource(Res.plurals.a11y_reps, item.reps, item.reps),
        item.weightHalfKg?.let(weightFormat::withUnit) ?: stringResource(Res.string.a11y_no_weight),
    ).joinToString(", ")
    Row(verticalAlignment = Alignment.Top) {
        Row(Modifier.clearAndSetSemantics { contentDescription = spoken }) {
            Text(
                text = stringResource(Res.string.exercise_sets_reps, item.sets, item.reps),
                style = numberStyle,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                autoSize = autoSize,
                modifier = Modifier.width(SetsColumnWidth),
            )
            Text(
                // Weight without unit (the column header says kg); a dash for bodyweight.
                text = item.weightHalfKg?.let(weightFormat::number) ?: "—",
                style = numberStyle,
                color = if (item.weightHalfKg != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
                autoSize = autoSize,
                modifier = Modifier.width(WeightColumnWidth),
            )
        }
        ProgressionToggle(
            exerciseName = item.name,
            progression = item.progression,
            incrementHalfKg = item.incrementHalfKg,
            weightFormat = weightFormat,
            onToggle = onToggleProgression,
            // The 48 dp target is centered on the first text line (24 sp line height).
            modifier = Modifier.offset(y = (-12).dp),
        )
    }
}

/**
 * A/R in a small circle: Increase filled with the primary color, Repeat outlined. One toggle
 * (switch role), announced with its label and state.
 */
@Composable
private fun ProgressionToggle(
    exerciseName: String,
    progression: Progression,
    incrementHalfKg: Int,
    weightFormat: WeightFormat,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val increase = progression == Progression.Increase
    val primary = MaterialTheme.colorScheme.primary
    val fill by animateColorAsState(if (increase) primary else primary.copy(alpha = 0f), label = "progressionFill")
    val letterColor by animateColorAsState(
        if (increase) MaterialTheme.colorScheme.onPrimary else primary,
        label = "progressionLetter",
    )
    val label = stringResource(Res.string.a11y_progression, exerciseName)
    val stateText = if (increase) {
        stringResource(Res.string.a11y_progression_increase_state, weightFormat.withUnit(incrementHalfKg))
    } else {
        stringResource(Res.string.a11y_progression_repeat_state)
    }
    Box(
        modifier = modifier
            .size(ProgressionColumnWidth)
            .minimumInteractiveComponentSize()
            .toggleable(value = increase, role = Role.Switch, onValueChange = { onToggle() })
            // On the toggle node itself: label + custom state ("Increase by 2 kg"), read with
            // the switch role, a single focus stop.
            .semantics {
                contentDescription = label
                stateDescription = stateText
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(fill)
                .border(1.5.dp, primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(
                    if (increase) Res.string.progression_increase_letter else Res.string.progression_repeat_letter,
                ),
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = CondensedFontFamily, fontSize = 16.sp),
                color = letterColor,
                maxLines = 1,
                modifier = Modifier.clearAndSetSemantics { },
            )
        }
    }
}

/** "Next 16 · Apply": the weight after the increase and a text action (48 dp target). */
@Composable
private fun NextWeightLine(
    exerciseName: String,
    nextWeight: String,
    nextWeightSpoken: String,
    onApply: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(Res.string.exercise_next_short, nextWeight) + " ·",
            style = MaterialTheme.typography.titleSmall.copy(fontFamily = CondensedFontFamily, fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clearAndSetSemantics { },
        )
        val applyLabel = stringResource(Res.string.a11y_apply, nextWeightSpoken, exerciseName)
        Box(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(role = Role.Button, onClick = onApply)
                .semantics { contentDescription = applyLabel }
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.action_apply),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clearAndSetSemantics { },
            )
        }
    }
}

@Composable
private fun NoRoutinesState(onCreate: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painterResource(Res.drawable.ic_fitness),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.routines_empty_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.routines_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        FilledTonalButton(onClick = onCreate, shape = MaterialTheme.shapes.small, modifier = Modifier.heightIn(min = 48.dp)) {
            Icon(painterResource(Res.drawable.ic_add), contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.routines_new))
        }
    }
}

@Composable
private fun NoExercisesState(routineName: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.routine_empty_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.routine_empty_message, routineName),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun ExerciseRowPreview() {
    AppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            val format = rememberWeightFormat()
            ColumnsHeader()
            ExerciseRow(
                item = ExerciseItem(1, "Panca manubri", "Schiena appoggiata", 4, 8, 29, Progression.Increase, 4, 33),
                weightFormat = format,
                isReordering = false,
                isFirst = true,
                isLast = false,
                onClick = {}, onToggleProgression = {}, onApply = {}, onMove = {},
            )
            RowDivider()
            ExerciseRow(
                item = ExerciseItem(2, "Trazioni", "", 3, 10, null, Progression.Repeat, 4, null),
                weightFormat = format,
                isReordering = false,
                isFirst = false,
                isLast = true,
                onClick = {}, onToggleProgression = {}, onApply = {}, onMove = {},
            )
        }
    }
}
