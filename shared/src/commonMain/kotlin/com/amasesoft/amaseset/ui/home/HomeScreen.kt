package com.amasesoft.amaseset.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amasesoft.amaseset.components.ListLoadingItem
import com.amasesoft.amaseset.components.LoadErrorState
import com.amasesoft.amaseset.components.MoveButtons
import com.amasesoft.amaseset.components.RowDivider
import com.amasesoft.amaseset.components.SectionLabel
import com.amasesoft.amaseset.domain.MAX_SAVED_WORKOUTS
import com.amasesoft.amaseset.domain.SavedWorkout
import com.amasesoft.amaseset.domain.WorkoutConfig
import com.amasesoft.amaseset.domain.WorkoutSort
import com.amasesoft.amaseset.domain.compactSummary
import com.amasesoft.amaseset.domain.formatSeconds
import com.amasesoft.amaseset.theme.AppTextStyles
import com.amasesoft.amaseset.theme.TabularNumbers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.amasesoft.amaseset.resources.Res
import com.amasesoft.amaseset.resources.a11y_more
import com.amasesoft.amaseset.resources.a11y_start
import com.amasesoft.amaseset.resources.action_cancel
import com.amasesoft.amaseset.resources.action_delete
import com.amasesoft.amaseset.resources.action_done
import com.amasesoft.amaseset.resources.action_edit
import com.amasesoft.amaseset.resources.action_move_bottom
import com.amasesoft.amaseset.resources.action_move_top
import com.amasesoft.amaseset.resources.action_reorder
import com.amasesoft.amaseset.resources.action_sort
import com.amasesoft.amaseset.resources.app_name
import com.amasesoft.amaseset.resources.editor_title_new
import com.amasesoft.amaseset.resources.home_custom_cycles
import com.amasesoft.amaseset.resources.home_cycles
import com.amasesoft.amaseset.resources.home_delete_message
import com.amasesoft.amaseset.resources.home_delete_title
import com.amasesoft.amaseset.resources.home_empty_message
import com.amasesoft.amaseset.resources.home_empty_title
import com.amasesoft.amaseset.resources.home_load_error
import com.amasesoft.amaseset.resources.home_new_workout
import com.amasesoft.amaseset.resources.home_quick_start
import com.amasesoft.amaseset.resources.home_saved_label
import com.amasesoft.amaseset.resources.home_sets
import com.amasesoft.amaseset.resources.home_summary
import com.amasesoft.amaseset.resources.home_uses
import com.amasesoft.amaseset.resources.ic_add
import com.amasesoft.amaseset.resources.ic_arrow_down
import com.amasesoft.amaseset.resources.ic_arrow_up
import com.amasesoft.amaseset.resources.ic_check
import com.amasesoft.amaseset.resources.ic_delete
import com.amasesoft.amaseset.resources.ic_edit
import com.amasesoft.amaseset.resources.ic_more_vert
import com.amasesoft.amaseset.resources.ic_play
import com.amasesoft.amaseset.resources.ic_reorder
import com.amasesoft.amaseset.resources.ic_settings
import com.amasesoft.amaseset.resources.ic_sort
import com.amasesoft.amaseset.resources.limit_reached
import com.amasesoft.amaseset.resources.settings_title
import com.amasesoft.amaseset.resources.sort_custom
import com.amasesoft.amaseset.resources.sort_longest
import com.amasesoft.amaseset.resources.sort_most_used
import com.amasesoft.amaseset.resources.sort_name
import com.amasesoft.amaseset.resources.sort_newest
import com.amasesoft.amaseset.resources.sort_oldest
import com.amasesoft.amaseset.resources.sort_shortest
import com.amasesoft.amaseset.resources.storage_error

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onQuickWorkout: () -> Unit,
    /** Starts [config] right away as a quick (unsaved) workout. */
    onStartQuick: (config: WorkoutConfig) -> Unit,
    onNewWorkout: () -> Unit,
    onEditWorkout: (Long) -> Unit,
    onStartWorkout: (SavedWorkout) -> Unit,
    onSettings: () -> Unit,
    /** Main tabs bar (Timer | Routines), provided by the navigation host. */
    bottomBar: @Composable () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val currentOnStart by rememberUpdatedState(onStartWorkout)
    var pendingDelete by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.StartRun -> currentOnStart(event.workout)
                HomeEvent.StorageError -> snackbarHostState.showSnackbar(getString(Res.string.storage_error))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // The brand name in condensed capitals; read as a normal word.
                    val appName = stringResource(Res.string.app_name)
                    Text(
                        text = appName.uppercase(),
                        style = AppTextStyles.condensedTitle,
                        modifier = Modifier.semantics {
                            contentDescription = appName
                            heading()
                        },
                    )
                },
                actions = {
                    if (state.isReordering) {
                        IconButton(onClick = { viewModel.setReordering(false) }) {
                            Icon(painterResource(Res.drawable.ic_check), stringResource(Res.string.action_done))
                        }
                    } else {
                        // Sorting and reordering only make sense on a list we could read.
                        if (state.isListAvailable) {
                            SortMenu(selected = state.sort, onSelect = viewModel::setSort)
                        }
                        if (state.isListAvailable && state.workouts.size > 1) {
                            IconButton(onClick = { viewModel.setReordering(true) }) {
                                Icon(painterResource(Res.drawable.ic_reorder), stringResource(Res.string.action_reorder))
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
            // Hidden while loading or after a read error: with an unknown file content we could
            // exceed the limit or overwrite data. The quick workout stays available.
            if (!state.isReordering && state.isListAvailable) {
                val newWorkoutLabel = stringResource(Res.string.editor_title_new)
                ExtendedFloatingActionButton(
                    onClick = {
                        if (!state.isAtLimit) {
                            onNewWorkout()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(getString(Res.string.limit_reached, MAX_SAVED_WORKOUTS))
                            }
                        }
                    },
                    icon = { Icon(painterResource(Res.drawable.ic_add), contentDescription = null) },
                    text = { Text(stringResource(Res.string.home_new_workout)) },
                    // "New" on screen, "New workout" for screen readers.
                    modifier = Modifier.semantics { contentDescription = newWorkoutLabel },
                )
            }
        },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        // Full-width rows separated by dividers (no cards): the rows pad their own content.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() + 96.dp,
            ),
        ) {
            // Shown only once the last quick configuration is known (settings read).
            val quickConfig = state.quickConfig
            if (!state.isReordering && quickConfig != null) {
                item(key = "quick") {
                    QuickStartRow(
                        config = quickConfig,
                        totalSeconds = state.quickTotalSeconds ?: 0,
                        onClick = onQuickWorkout,
                        onStart = { onStartQuick(quickConfig) },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
            when {
                state.isLoading -> item(key = "loading") {
                    ListLoadingItem(Modifier.animateItem())
                }

                state.loadError -> item(key = "error") {
                    LoadErrorState(
                        message = stringResource(Res.string.home_load_error),
                        onRetry = viewModel::reload,
                        modifier = Modifier.animateItem(),
                    )
                }

                else -> item(key = "header") {
                    SectionLabel(
                        text = stringResource(Res.string.home_saved_label, state.workouts.size, MAX_SAVED_WORKOUTS),
                        modifier = Modifier.animateItem(),
                    )
                }
            }
            if (state.isListAvailable && state.workouts.isEmpty()) {
                item(key = "empty") { EmptyState(Modifier.animateItem()) }
            }
            itemsIndexed(state.workouts, key = { _, item -> item.id }) { index, item ->
                Column(Modifier.animateItem()) {
                    if (index > 0) RowDivider()
                    WorkoutRow(
                        item = item,
                        isReordering = state.isReordering,
                        showUses = state.sort == WorkoutSort.MostUsed,
                        isFirst = index == 0,
                        isCustomOrder = state.sort == WorkoutSort.Custom,
                        isLast = index == state.workouts.lastIndex,
                        onStart = { viewModel.start(item.id) },
                        onEdit = { onEditWorkout(item.id) },
                        onDelete = { pendingDelete = item.id },
                        onMove = { offset -> viewModel.move(item.id, offset) },
                        onMoveToEdge = { toTop -> viewModel.moveToEdge(item.id, toTop) },
                    )
                }
            }
        }
    }

    val deleting = pendingDelete?.let { id -> state.workouts.firstOrNull { it.id == id } }
    if (deleting != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(Res.string.home_delete_title)) },
            text = { Text(stringResource(Res.string.home_delete_message, deleting.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(deleting.id)
                    pendingDelete = null
                }) { Text(stringResource(Res.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(stringResource(Res.string.action_cancel)) }
            },
        )
    }
}

@Composable
private fun SortMenu(selected: WorkoutSort, onSelect: (WorkoutSort) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(painterResource(Res.drawable.ic_sort), stringResource(Res.string.action_sort))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            WorkoutSort.entries.forEach { sort ->
                DropdownMenuItem(
                    text = { Text(stringResource(sort.label)) },
                    onClick = {
                        onSelect(sort)
                        expanded = false
                    },
                    trailingIcon = if (sort == selected) {
                        { Icon(painterResource(Res.drawable.ic_check), contentDescription = null) }
                    } else {
                        null
                    },
                )
            }
        }
    }
}

private val WorkoutSort.label: StringResource
    get() = when (this) {
        WorkoutSort.Custom -> Res.string.sort_custom
        WorkoutSort.Name -> Res.string.sort_name
        WorkoutSort.MostUsed -> Res.string.sort_most_used
        WorkoutSort.Newest -> Res.string.sort_newest
        WorkoutSort.Oldest -> Res.string.sort_oldest
        WorkoutSort.Shortest -> Res.string.sort_shortest
        WorkoutSort.Longest -> Res.string.sort_longest
    }

/** Play icon without a filled circle, in the primary color; 48 dp target. */
@Composable
private fun PlayButton(contentDescription: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary),
    ) {
        Icon(painterResource(Res.drawable.ic_play), contentDescription, modifier = Modifier.size(32.dp))
    }
}

/**
 * "Quick start": the last quick configuration in compact form ("1 × 8 · 20/10") and its total.
 * Tapping the row opens the quick editor; the play icon starts it right away.
 */
@Composable
private fun QuickStartRow(
    config: WorkoutConfig,
    totalSeconds: Int,
    onClick: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(Res.string.home_quick_start)
    val summary = config.compactSummary(stringResource(Res.string.home_custom_cycles))
    val spoken = stringResource(
        Res.string.home_summary,
        formatSeconds(totalSeconds),
        pluralStringResource(Res.plurals.home_sets, config.sets, config.sets),
        pluralStringResource(Res.plurals.home_cycles, config.plan.count, config.plan.count),
    )
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .clickable(onClickLabel = stringResource(Res.string.action_edit), onClick = onClick)
                .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clearAndSetSemantics { contentDescription = "$title, $spoken" },
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = summary,
                    style = MaterialTheme.typography.titleMedium.merge(TabularNumbers),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = formatSeconds(totalSeconds),
                style = AppTextStyles.numberLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp).clearAndSetSemantics { },
            )
            PlayButton(contentDescription = stringResource(Res.string.a11y_start, title), onClick = onStart)
            // Same width as the rows' overflow menu: durations and play icons line up.
            Spacer(Modifier.width(48.dp))
        }
        RowDivider()
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.home_empty_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.home_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WorkoutRow(
    item: WorkoutItem,
    isReordering: Boolean,
    /** Number of uses, only meaningful (and shown) when sorting by most used. */
    showUses: Boolean,
    isFirst: Boolean,
    isCustomOrder: Boolean,
    isLast: Boolean,
    onStart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMove: (Int) -> Unit,
    onMoveToEdge: (toTop: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val compact = item.config.compactSummary(stringResource(Res.string.home_custom_cycles))
    val uses = pluralStringResource(Res.plurals.home_uses, item.usageCount, item.usageCount)
    val spoken = stringResource(
        Res.string.home_summary,
        formatSeconds(item.totalSeconds),
        pluralStringResource(Res.plurals.home_sets, item.sets, item.sets),
        pluralStringResource(Res.plurals.home_cycles, item.cycles, item.cycles),
    ) + if (showUses) ", $uses" else ""
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clickable(
                enabled = !isReordering,
                // The row opens the editor: announce the action.
                onClickLabel = stringResource(Res.string.action_edit),
                onClick = onEdit,
            )
            .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = if (showUses) "$compact · $uses" else compact,
                style = MaterialTheme.typography.titleMedium.merge(TabularNumbers),
                color = MaterialTheme.colorScheme.primary,
                // The compact form is visual: screen readers get the spelled-out summary.
                modifier = Modifier.padding(top = 2.dp).clearAndSetSemantics { contentDescription = spoken },
            )
        }
        AnimatedContent(
            targetState = isReordering,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            contentAlignment = Alignment.CenterEnd,
            label = "rowActions",
        ) { reordering ->
            if (reordering) {
                MoveButtons(name = item.name, isFirst = isFirst, isLast = isLast, onMove = onMove)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatSeconds(item.totalSeconds),
                        style = AppTextStyles.numberLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp).clearAndSetSemantics { },
                    )
                    PlayButton(contentDescription = stringResource(Res.string.a11y_start, item.name), onClick = onStart)
                    WorkoutOverflowMenu(
                        name = item.name,
                        canMoveUp = !isFirst || !isCustomOrder,
                        canMoveDown = !isLast || !isCustomOrder,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        onMoveToEdge = onMoveToEdge,
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutOverflowMenu(
    name: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveToEdge: (toTop: Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        // Discreet: secondary actions only.
        IconButton(
            onClick = { expanded = true },
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
        ) {
            Icon(painterResource(Res.drawable.ic_more_vert), stringResource(Res.string.a11y_more, name))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // Sets the custom order (and switches to it) without entering reorder mode.
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_move_top)) },
                leadingIcon = { Icon(painterResource(Res.drawable.ic_arrow_up), contentDescription = null) },
                enabled = canMoveUp,
                onClick = {
                    expanded = false
                    onMoveToEdge(true)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_move_bottom)) },
                leadingIcon = { Icon(painterResource(Res.drawable.ic_arrow_down), contentDescription = null) },
                enabled = canMoveDown,
                onClick = {
                    expanded = false
                    onMoveToEdge(false)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_edit)) },
                leadingIcon = { Icon(painterResource(Res.drawable.ic_edit), contentDescription = null) },
                onClick = {
                    expanded = false
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_delete)) },
                leadingIcon = { Icon(painterResource(Res.drawable.ic_delete), contentDescription = null) },
                onClick = {
                    expanded = false
                    onDelete()
                },
            )
        }
    }
}
