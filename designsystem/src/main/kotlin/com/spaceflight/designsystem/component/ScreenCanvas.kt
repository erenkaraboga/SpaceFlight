package com.spaceflight.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.theme.SpaceflightTheme
import com.spaceflight.designsystem.theme.auroraWash

/**
 * The branded wash behind every top-level screen, so the near-black surface is never a flat slab.
 *
 * @param modifier The [Modifier] to be applied to the root canvas container.
 * @param content The composable content slot to be rendered on top of the branded background wash.
 */
@Composable
fun ScreenCanvas(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(MaterialTheme.auroraWash),
        )
        content()
    }
}

@Preview(name = "Screen Canvas Preview", showBackground = true)
@Composable
private fun ScreenCanvasPreview() {
    SpaceflightTheme {
        ScreenCanvas {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Screen Title",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Content rendered over aurora wash background.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}