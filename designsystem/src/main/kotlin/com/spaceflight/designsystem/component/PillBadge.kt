package com.spaceflight.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.theme.EyebrowTextStyle
import com.spaceflight.designsystem.theme.SpaceColors
import com.spaceflight.designsystem.theme.SpaceflightTheme

/**
 * Small pill label with an optional leading dot.
 *
 * @param text The string to be displayed inside the pill in uppercase format.
 * @param modifier The [Modifier] to be applied to the pill layout.
 * @param containerColor Background color of the pill container.
 * @param contentColor Text color used inside the pill.
 * @param dotColor Color of the leading indicator dot when [showDot] is true.
 * @param showDot Controls whether the leading indicator dot is visible.
 */
@Composable
fun PillBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    dotColor: Color = MaterialTheme.colorScheme.secondary,
    showDot: Boolean = true,
) {
    if (text.isBlank()) return

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .padding(start = 8.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showDot) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
        Text(
            text = text.uppercase(),
            style = EyebrowTextStyle,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(name = "Pill Badge Variants", showBackground = true)
@Composable
private fun PillBadgePreview() {
    SpaceflightTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillBadge(
                    text = "Live",
                    showDot = true
                )

                PillBadge(
                    text = "Upcoming",
                    showDot = false
                )

                PillBadge(
                    text = "Active Mission",
                    containerColor = SpaceColors.IonInk,
                    contentColor = SpaceColors.Bright,
                    dotColor = SpaceColors.IonDeep,
                    showDot = true
                )
            }
        }
    }
}