package com.spaceflight.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.theme.SpaceflightTheme

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


@Preview(name = "Glass Icon Buttons", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun GlassIconButtonPreview() {
    SpaceflightTheme {
        Surface(color = Color(0xFF1E1E2C)) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                GlassIconButton(
                    onClick = {},
                    containerColor = Color.Black.copy(alpha = 0.3f),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }

                GlassIconButton(
                    onClick = {},
                    containerColor = Color.Black.copy(alpha = 0.3f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = Color(0xFFFF4081),
                    )
                }

                GlassIconButton(
                    onClick = {},
                    containerColor = Color.White.copy(alpha = 0.15f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                    )
                }
            }
        }
    }
}
