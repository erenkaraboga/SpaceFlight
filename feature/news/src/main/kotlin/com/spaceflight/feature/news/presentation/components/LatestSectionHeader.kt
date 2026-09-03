package com.spaceflight.feature.news.presentation.components

import com.spaceflight.designsystem.component.LayoutToggleButton
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.SectionHeader
import com.spaceflight.feature.news.R

@Composable
fun LatestSectionHeader(
    isGrid: Boolean,
    onToggleLayout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionHeader(
        title = stringResource(R.string.news_latest),
        modifier = modifier.padding(top = 8.dp, bottom = 2.dp),
        trailingContent = {
            LayoutToggleButton(isGrid = isGrid, onClick = onToggleLayout)
        },
    )
}