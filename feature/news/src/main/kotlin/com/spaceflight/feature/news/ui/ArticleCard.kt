package com.spaceflight.feature.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.FavoriteButton
import com.spaceflight.designsystem.component.PillBadge
import com.spaceflight.designsystem.component.RemoteImage
import com.spaceflight.designsystem.component.ShimmerSurface

@Composable
fun ArticleCard(
    title: String,
    summary: String,
    imageUrl: String,
    newsSite: String,
    dateLabel: String,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            if (summary.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                PillBadge(
                    text = newsSite,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RemoteImage(
                imageUrl = imageUrl,
                contentDescription = null,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .size(width = 96.dp, height = 96.dp)
                    .then(imageModifier),
            )
            Spacer(Modifier.height(4.dp))
            FavoriteButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick,
                size = 36.dp,
            )
        }
    }
}

@Composable
fun ArticleCardPlaceholder(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ShimmerBar(widthFraction = 1f, height = 15.dp)
            ShimmerBar(widthFraction = 0.75f, height = 15.dp)
            Spacer(Modifier.height(4.dp))
            ShimmerBar(widthFraction = 0.45f, height = 11.dp)
        }
        Box(
            Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(18.dp))
        ) {
            ShimmerSurface(Modifier.size(96.dp))
        }
    }
}

@Composable
private fun ShimmerBar(widthFraction: Float, height: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(6.dp))
    ) {
        ShimmerSurface(
            Modifier
                .fillMaxWidth()
                .height(height)
        )
    }
}
