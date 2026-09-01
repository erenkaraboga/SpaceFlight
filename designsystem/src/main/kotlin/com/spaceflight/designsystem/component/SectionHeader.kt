package com.spaceflight.designsystem.component

import LayoutToggleButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.R
import com.spaceflight.designsystem.theme.SpaceflightTheme

/**
 * Divides a scrolling feed into named runs, with an optional action on the right.
 *
 * @param title The primary text for the section header.
 * @param modifier The [Modifier] to be applied to the layout container.
 * @param trailing Optional string text displayed on the right end of the header.
 * @param trailingContent Optional custom slot composable rendered on the right end of the header.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        when {
            trailingContent != null -> trailingContent()
            trailing != null -> Text(
                text = trailing,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
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
                SectionHeader(
                    title = "Upcoming Launches",
                    trailing = "12 Total",
                )

                var isGridState by remember { mutableStateOf(false) }
                SectionHeader(
                    title = "Spaceflight News",
                    trailingContent = {
                        LayoutToggleButton(
                            isGrid = isGridState,
                            onClick = { isGridState = !isGridState },
                        )
                    },
                )

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