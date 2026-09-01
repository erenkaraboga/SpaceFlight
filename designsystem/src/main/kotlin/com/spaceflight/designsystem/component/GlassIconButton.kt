package com.spaceflight.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A circular, tinted icon button -- the "glass chip" chrome used for controls that float over
 * photo or media content (the detail screen's back/share buttons, the favorite heart). Sharing
 * this one implementation means every floating control gets the same touch target, ripple and
 * clip behavior instead of each screen clipping its own circle.
 *
 * @param containerColor The background color of the circular container.
 * @param size Total diameter of the touch target and background shape.
 * @param content The icon (or other small content) centered inside the button.
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    size: Dp = 48.dp,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor),
    ) {
        content()
    }
}
