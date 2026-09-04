package com.spaceflight.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.spaceflight.designsystem.theme.SpaceflightTheme

/**
 * Remote image with a shimmer while loading and a neutral fallback if the URL is missing or dead.
 *
 * @param imageUrl The web URL of the image to be fetched.
 * @param contentDescription Text description of the image for accessibility.
 * @param modifier The [Modifier] to be applied to the image container.
 * @param shape The clipping [Shape] of the image container.
 * @param border Optional [BorderStroke] to draw around the image bounds.
 * @param contentScale Scaling strategy for fitting the image inside its bounds.
 */
@Composable
fun RemoteImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    SubcomposeAsyncImage(
        model = imageUrl.takeIf { it.isNotBlank() },
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clip(shape),
        loading = { ShimmerSurface(Modifier.fillMaxSize()) },
        error = { ImageFallback(Modifier.fillMaxSize()) },
    )
}

/**
 * Placeholder layout with a neutral background and icon used when image fetching fails or URL is blank.
 *
 * @param modifier The [Modifier] to be applied to the fallback layout.
 */
@Composable
private fun ImageFallback(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Image,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp),
        )
    }
}

/**
 * A slow highlight sweep used while images and list placeholders load.
 *
 * @param modifier The [Modifier] to be applied to the shimmer layout.
 */
@Composable
fun ShimmerSurface(modifier: Modifier = Modifier) {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surfaceContainerHigh

    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )

    Box(
        modifier.drawBehind {
            val sweepStart = size.width * (progress.value * 2f - 1f)
            drawRect(
                Brush.linearGradient(
                    colors = listOf(base, highlight, base),
                    start = Offset(sweepStart, 0f),
                    end = Offset(sweepStart + size.width, 0f),
                )
            )
        }
    )
}

@Preview(name = "Remote Image States", showBackground = true)
@Composable
private fun RemoteImagePreview() {
    SpaceflightTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShimmerSurface(
                    modifier = Modifier
                        .size(width = 200.dp, height = 120.dp)
                        .clip(MaterialTheme.shapes.medium)
                )

                RemoteImage(
                    imageUrl = "",
                    contentDescription = "Fallback placeholder example",
                    modifier = Modifier.size(width = 200.dp, height = 120.dp)
                )

                RemoteImage(
                    imageUrl = "",
                    contentDescription = "Bordered fallback placeholder example",
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.size(width = 200.dp, height = 120.dp)
                )
            }
        }
    }
}