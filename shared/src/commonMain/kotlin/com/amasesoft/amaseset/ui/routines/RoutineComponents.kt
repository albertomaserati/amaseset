package com.amasesoft.amaseset.ui.routines

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amasesoft.amaseset.components.MoveButtons
import com.amasesoft.amaseset.components.WeightFormat
import com.amasesoft.amaseset.components.WholeWordsText
import com.amasesoft.amaseset.domain.GymNameError
import com.amasesoft.amaseset.domain.Progression
import com.amasesoft.amaseset.domain.RoutineLimits
import com.amasesoft.amaseset.theme.AppTextStyles
import com.amasesoft.amaseset.theme.CondensedFontFamily
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.amasesoft.amaseset.resources.Res
import com.amasesoft.amaseset.resources.a11y_apply
import com.amasesoft.amaseset.resources.a11y_no_weight
import com.amasesoft.amaseset.resources.a11y_progression
import com.amasesoft.amaseset.resources.a11y_progression_increase_state
import com.amasesoft.amaseset.resources.a11y_progression_repeat_state
import com.amasesoft.amaseset.resources.a11y_reps
import com.amasesoft.amaseset.resources.a11y_sets
import com.amasesoft.amaseset.resources.action_apply
import com.amasesoft.amaseset.resources.action_cancel
import com.amasesoft.amaseset.resources.action_edit
import com.amasesoft.amaseset.resources.action_save
import com.amasesoft.amaseset.resources.char_counter
import com.amasesoft.amaseset.resources.editor_name_required
import com.amasesoft.amaseset.resources.exercise_next_short
import com.amasesoft.amaseset.resources.exercise_sets_reps
import com.amasesoft.amaseset.resources.name_too_long
import com.amasesoft.amaseset.resources.progression_increase_letter
import com.amasesoft.amaseset.resources.progression_repeat_letter
import com.amasesoft.amaseset.resources.routine_name
import com.amasesoft.amaseset.resources.routine_name_taken
import com.amasesoft.amaseset.resources.routine_rename_title
import com.amasesoft.amaseset.resources.routines_col_exercise
import com.amasesoft.amaseset.resources.routines_col_sets
import com.amasesoft.amaseset.resources.routines_new
import com.amasesoft.amaseset.resources.unit_kg

/**
 * Single-line name field with an inline error (same style as the workout editor) and a
 * character counter. Input is not truncated: going over [maxLength] shows the error.
 */
@Composable
internal fun GymNameField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: GymNameError?,
    maxLength: Int,
    modifier: Modifier = Modifier,
    duplicateMessage: StringResource = Res.string.routine_name_taken,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {
    val length = value.trim().length
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null || length > maxLength,
        supportingText = {
            Row {
                Text(
                    text = when (error) {
                        GymNameError.Empty -> stringResource(Res.string.editor_name_required)
                        GymNameError.TooLong -> stringResource(Res.string.name_too_long, maxLength)
                        GymNameError.Duplicate -> stringResource(duplicateMessage)
                        null -> ""
                    },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                // The counter is visual only; the error text is what matters to screen readers.
                Text(
                    text = stringResource(Res.string.char_counter, length, maxLength),
                    modifier = Modifier.clearAndSetSemantics { },
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
        modifier = modifier.fillMaxWidth(),
    )
}

/** Create / rename dialog of a routine. */
@Composable
internal fun RoutineNameDialogView(
    dialog: RoutineNameDialog,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (dialog.target is RoutineNameTarget.Rename) Res.string.routine_rename_title else Res.string.routines_new,
                ),
            )
        },
        text = {
            GymNameField(
                value = dialog.text,
                onValueChange = onNameChange,
                label = stringResource(Res.string.routine_name),
                error = dialog.error,
                maxLength = RoutineLimits.NAME_MAX_LENGTH,
                imeAction = ImeAction.Done,
                onImeAction = onConfirm,
                modifier = Modifier.focusRequester(focusRequester),
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !dialog.isSaving) { Text(stringResource(Res.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) }
        },
    )
}

// Exercise rows of the Routines tab: name (+ description, + "Next 16 · Apply") | sets×reps | kg | A/R.

/**
 * Vertical offset that centers a 48 dp touch target on the first text line of a row (24 sp
 * line height, so it follows the font scale): -12 dp at 100%.
 */
@Composable
private fun firstLineTargetOffset(): Dp = with(LocalDensity.current) { 24.sp.toDp() } / 2 - 24.dp

// Fixed widths of the number columns, so values line up from row to row.
private val SetsColumnWidth = 60.dp
private val WeightColumnWidth = 56.dp
private val ProgressionColumnWidth = 48.dp

/** "EXERCISE · SETS · KG": small spaced labels over the columns (visual only). */
@Composable
internal fun ColumnsHeader(modifier: Modifier = Modifier) {
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
        // Fixed-width columns: the labels shrink instead of being cut at large font scales.
        val autoSize = TextAutoSize.StepBased(minFontSize = 7.sp, maxFontSize = style.fontSize, stepSize = 0.5.sp)
        Text(
            stringResource(Res.string.routines_col_sets).uppercase(),
            style = style,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            autoSize = autoSize,
            modifier = Modifier.width(SetsColumnWidth),
        )
        Text(
            stringResource(Res.string.unit_kg).uppercase(),
            style = style,
            color = color,
            textAlign = TextAlign.End,
            maxLines = 1,
            softWrap = false,
            autoSize = autoSize,
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
internal fun ExerciseRow(
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
            .clickable(
                enabled = !isReordering,
                // The row is the way to edit (and delete) the exercise: say so.
                onClickLabel = stringResource(Res.string.action_edit),
                onClick = onClick,
            )
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
            // Only with something to apply: Increase *and* a weight (hidden while reordering).
            // AnimatedContent keeps the outgoing value during the exit (after Apply the exercise
            // is back to Repeat, so the new state has no value).
            AnimatedContent(
                targetState = item.nextWeightHalfKg.takeUnless { isReordering },
                transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
                label = "nextWeight",
            ) { next ->
                if (next != null) {
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
                MoveButtons(
                    name = item.name,
                    isFirst = isFirst,
                    isLast = isLast,
                    onMove = onMove,
                    modifier = Modifier.offset(y = firstLineTargetOffset()),
                )
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
            modifier = Modifier.offset(y = firstLineTargetOffset()),
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
                // Sized in dp: the letter must stay inside the 28 dp circle at any font scale.
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = CondensedFontFamily,
                    fontSize = with(LocalDensity.current) { 16.dp.toSp() },
                    lineHeight = with(LocalDensity.current) { 18.dp.toSp() },
                ),
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
