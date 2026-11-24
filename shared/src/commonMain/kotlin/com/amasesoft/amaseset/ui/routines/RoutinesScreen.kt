package com.amasesoft.amaseset.ui.routines

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.amasesoft.amaseset.components.ListLoadingItem
import com.amasesoft.amaseset.components.LoadErrorState
import com.amasesoft.amaseset.components.RowDivider
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
import org.jetbrains.compose.resources.stringResource
import com.amasesoft.amaseset.resources.Res
import com.amasesoft.amaseset.resources.action_done
import com.amasesoft.amaseset.resources.action_reorder
import com.amasesoft.amaseset.resources.action_undo
import com.amasesoft.amaseset.resources.exercise_add
import com.amasesoft.amaseset.resources.exercise_limit_reached
import com.amasesoft.amaseset.resources.exercise_weight_updated
import com.amasesoft.amaseset.resources.ic_add
import com.amasesoft.amaseset.resources.ic_check
import com.amasesoft.amaseset.resources.ic_fitness
import com.amasesoft.amaseset.resources.ic_list
import com.amasesoft.amaseset.resources.ic_reorder
import com.amasesoft.amaseset.resources.ic_settings
import com.amasesoft.amaseset.resources.routine_empty_message
import com.amasesoft.amaseset.resources.routine_empty_title
import com.amasesoft.amaseset.resources.routine_limit_reached
import com.amasesoft.amaseset.resources.routines_empty_message
import com.amasesoft.amaseset.resources.routines_empty_title
import com.amasesoft.amaseset.resources.routines_load_error
import com.amasesoft.amaseset.resources.routines_manage
import com.amasesoft.amaseset.resources.routines_new
import com.amasesoft.amaseset.resources.routines_selector_label
import com.amasesoft.amaseset.resources.routines_title
import com.amasesoft.amaseset.resources.settings_title
import com.amasesoft.amaseset.resources.storage_error

/** Up to this many routines the selector is a segmented row, then a dropdown. */
private const val SEGMENTED_MAX = 3

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
                    ListLoadingItem()
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
