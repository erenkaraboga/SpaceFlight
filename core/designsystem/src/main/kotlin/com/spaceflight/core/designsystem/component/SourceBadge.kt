package com.spaceflight.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spaceflight.core.designsystem.theme.SourceBadgeTextStyle
import java.util.Locale

/** Publisher attribution, which the Spaceflight News API terms require us to keep visible. */
@Composable
fun SourceBadge(
    newsSite: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    if (newsSite.isBlank()) return

    Text(
        text = newsSite.uppercase(Locale.getDefault()),
        style = SourceBadgeTextStyle,
        color = contentColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(containerColor, MaterialTheme.shapes.extraSmall)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
