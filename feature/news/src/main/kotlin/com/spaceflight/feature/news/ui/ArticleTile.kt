package com.spaceflight.feature.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.PhotoFavoriteButton
import com.spaceflight.designsystem.component.RemoteImage
import com.spaceflight.designsystem.component.ShimmerSurface

/**
 * Image-led tile for the two-column feed. Summary is omitted so the grid stays scannable.
 */
@Composable
fun ArticleTile(
    title: String,
    imageUrl: String,
    dateLabel: String,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick),
    ) {
        Box {
            RemoteImage(
                imageUrl = imageUrl,
                contentDescription = null,
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .then(imageModifier),
            )
            PhotoFavoriteButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
            )
        }
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
fun ArticleTilePlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f),
        ) {
            ShimmerSurface(Modifier.fillMaxSize())
        }
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
            ) {
                ShimmerSurface(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(8f / 1f),
                )
            }
        }
    }
}
