package com.amasesoft.amaseset.ui.routines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amasesoft.amaseset.components.RowDivider
import com.amasesoft.amaseset.components.LoadErrorState
import com.amasesoft.amaseset.components.WholeWordsText
import com.amasesoft.amaseset.domain.MAX_ROUTINES
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.amasesoft.amaseset.resources.Res
import com.amasesoft.amaseset.resources.a11y_more
import com.amasesoft.amaseset.resources.a11y_move_down
import com.amasesoft.amaseset.resources.a11y_move_up
import com.amasesoft.amaseset.resources.action_back
import com.amasesoft.amaseset.resources.action_cancel
import com.amasesoft.amaseset.resources.action_delete
import com.amasesoft.amaseset.resources.action_duplicate
import com.amasesoft.amaseset.resources.action_rename
import com.amasesoft.amaseset.resources.ic_arrow_back
import com.amasesoft.amaseset.resources.ic_arrow_down
import com.amasesoft.amaseset.resources.ic_arrow_up
import com.amasesoft.amaseset.resources.ic_copy
import com.amasesoft.amaseset.resources.ic_delete
import com.amasesoft.amaseset.resources.ic_edit
import com.amasesoft.amaseset.resources.ic_more_vert
import com.amasesoft.amaseset.resources.ic_add
import com.amasesoft.amaseset.resources.routine_copy_word
import com.amasesoft.amaseset.resources.routine_delete_message
import com.amasesoft.amaseset.resources.routine_delete_title
import com.amasesoft.amaseset.resources.routine_exercise_count
import com.amasesoft.amaseset.resources.routine_limit_reached
import com.amasesoft.amaseset.resources.routines_load_error
import com.amasesoft.amaseset.resources.routines_manage
import com.amasesoft.amaseset.resources.routines_max
import com.amasesoft.amaseset.resources.routines_new
import com.amasesoft.amaseset.resources.routines_new_hint
import com.amasesoft.amaseset.resources.storage_error

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRoutinesScreen(viewModel: ManageRoutinesViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDelete by rememberSaveable { mutableStateOf<Long?>(null) }
    val copyWord = stringResource(Res.string.routine_copy_word)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                RoutinesEvent.StorageError -> snackbarHostState.showSnackbar(getString(Res.string.storage_error))
                RoutinesEvent.RoutineLimitReached ->
                    snackbarHostState.showSnackbar(getString(Res.string.routine_limit_reached, MAX_ROUTINES))

                RoutinesEvent.ExerciseLimitReached, is RoutinesEvent.IncreaseApplied -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.routines_manage)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.ic_arrow_back), stringResource(Res.string.action_back))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        // Full-width rows separated by dividers; the rows pad their own content.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
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
                    )
                }

                else -> {
                    itemsIndexed(state.routines, key = { _, routine -> routine.id }) { index, routine ->
                        Column(Modifier.animateItem()) {
                            if (index > 0) RowDivider()
                            RoutineRow(
                                routine = routine,
                                isFirst = index == 0,
                                isLast = index == state.routines.lastIndex,
                                onMove = { offset -> viewModel.move(routine.id, offset) },
                                onRename = { viewModel.openRename(routine.id) },
                                onDuplicate = { viewModel.duplicate(routine.id, copyWord) },
                                onDelete = { pendingDelete = routine.id },
                            )
                        }
                    }
                    item(key = "add") {
                        Column(Modifier.animateItem()) {
                            if (state.routines.isNotEmpty()) RowDivider()
                            AddRoutineRow(
                                title = if (state.canAddRoutine) {
                                    stringResource(Res.string.routines_new)
                                } else {
                                    stringResource(Res.string.routines_max, MAX_ROUTINES)
                                },
                                hint = stringResource(Res.string.routines_new_hint),
                                enabled = state.canAddRoutine,
                                onClick = viewModel::openNewRoutine,
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

    val deleting = pendingDelete?.let { id -> state.routines.firstOrNull { it.id == id } }
    if (deleting != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(Res.string.routine_delete_title)) },
            text = { Text(stringResource(Res.string.routine_delete_message, deleting.name)) },
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
private fun RoutineRow(
    routine: RoutineSummary,
    isFirst: Boolean,
    isLast: Boolean,
    onMove: (Int) -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(vertical = 4.dp)) {
            WholeWordsText(
                text = routine.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
            )
            Text(
                text = pluralStringResource(Res.plurals.routine_exercise_count, routine.exerciseCount, routine.exerciseCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = { onMove(-1) }, enabled = !isFirst) {
            Icon(painterResource(Res.drawable.ic_arrow_up), stringResource(Res.string.a11y_move_up, routine.name))
        }
        IconButton(onClick = { onMove(1) }, enabled = !isLast) {
            Icon(painterResource(Res.drawable.ic_arrow_down), stringResource(Res.string.a11y_move_down, routine.name))
        }
        var expanded by remember { mutableStateOf(false) }
        Box {
            IconButton(
                onClick = { expanded = true },
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            ) {
                Icon(painterResource(Res.drawable.ic_more_vert), stringResource(Res.string.a11y_more, routine.name))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.action_rename)) },
                    leadingIcon = { Icon(painterResource(Res.drawable.ic_edit), contentDescription = null) },
                    onClick = {
                        expanded = false
                        onRename()
                    },
                )
                // Always enabled: at the limit the tap explains why (snackbar).
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.action_duplicate)) },
                    leadingIcon = { Icon(painterResource(Res.drawable.ic_copy), contentDescription = null) },
                    onClick = {
                        expanded = false
                        onDuplicate()
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
}

/** "+ New routine" closing the list, as a row in the primary color; at the limit it says why. */
@Composable
private fun AddRoutineRow(
    title: String,
    hint: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(Res.drawable.ic_add), contentDescription = null, tint = color)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = color)
            if (enabled) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
