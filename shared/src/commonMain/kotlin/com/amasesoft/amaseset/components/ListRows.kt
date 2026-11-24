package com.amasesoft.amaseset.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.amasesoft.amaseset.resources.Res
import com.amasesoft.amaseset.resources.a11y_move_down
import com.amasesoft.amaseset.resources.a11y_move_up
import com.amasesoft.amaseset.resources.ic_arrow_down
import com.amasesoft.amaseset.resources.ic_arrow_up
import com.amasesoft.amaseset.theme.AppTextStyles
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Building blocks of the list screens (Home, Routines, Manage routines): full-width rows
// separated by dividers instead of cards.

/** Divider between list rows, inset like the row content. */
@Composable
fun RowDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    )
}

/** Small spaced uppercase label above a list ("SAVED · 2/50"). */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = AppTextStyles.overline,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 4.dp)
            .semantics {
                contentDescription = text
                heading()
            },
    )
}

/** Up/down arrows of a row in reorder mode; [name] completes the accessibility labels. */
@Composable
fun MoveButtons(
    name: String,
    isFirst: Boolean,
    isLast: Boolean,
    onMove: (offset: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        IconButton(onClick = { onMove(-1) }, enabled = !isFirst) {
            Icon(painterResource(Res.drawable.ic_arrow_up), stringResource(Res.string.a11y_move_up, name))
        }
        IconButton(onClick = { onMove(1) }, enabled = !isLast) {
            Icon(painterResource(Res.drawable.ic_arrow_down), stringResource(Res.string.a11y_move_down, name))
        }
    }
}

/** Loading placeholder of a list: a small spinner, centered. */
@Composable
fun ListLoadingItem(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
    }
}
