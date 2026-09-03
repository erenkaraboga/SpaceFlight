package com.spaceflight.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.R
import com.spaceflight.designsystem.theme.SpaceflightTheme

/**
 * Toggle button for switching between Grid and List view layouts.
 *
 * @param isGrid Current layout state; `true` if grid view is active, `false` for list view.
 * @param onClick Callback to trigger when the user clicks the layout toggle icon.
 * @param modifier The [Modifier] to be applied to the button layout.
 */
@Composable
fun LayoutToggleButton(
    isGrid: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .focusProperties { canFocus = false },
    ) {
        Icon(
            imageVector = if (isGrid) Icons.Rounded.ViewAgenda else Icons.Rounded.GridView,
            contentDescription = stringResource(
                if (isGrid) R.string.ds_use_list_layout else R.string.ds_use_grid_layout,
            ),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Preview(name = "Section Header & Layout Toggle States", showBackground = true)
@Composable
private fun SectionHeaderComponentsPreview() {
    SpaceflightTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LayoutToggleButton(isGrid = true, onClick = {})
                    LayoutToggleButton(isGrid = false, onClick = {})
                }
            }
        }
    }
}


