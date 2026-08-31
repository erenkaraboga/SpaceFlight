package com.spaceflight.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * The workhorse list row: thumbnail, publisher, headline, a two-line teaser and the favourite
 * toggle. [imageModifier] and [titleModifier] are exposed so the caller can attach shared-element
 * modifiers and let those two pieces fly into the detail pane.
 */
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
    isSelected: Boolean = false,
    imageModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncArticleImage(
                imageUrl = imageUrl,
                contentDescription = null,
                shape = MaterialTheme.shapes.medium,
                modifier = imageModifier.size(96.dp),
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SourceBadge(newsSite = newsSite)

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = titleModifier,
                )

                if (summary.isNotBlank()) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            FavoriteButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

/** Static skeleton mirroring [ArticleCard]'s layout, shown while the first page loads. */
@Composable
fun ArticleCardPlaceholder(modifier: Modifier = Modifier) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { },
    ) {
        Row(Modifier.padding(12.dp)) {
            ShimmerSurface(
                Modifier
                    .size(96.dp)
                    .clip(MaterialTheme.shapes.small),
            )
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShimmerSurface(
                    Modifier
                        .width(72.dp)
                        .height(14.dp)
                        .clip(MaterialTheme.shapes.small),
                )
                ShimmerSurface(
                    Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(MaterialTheme.shapes.small),
                )
                ShimmerSurface(
                    Modifier
                        .fillMaxWidth(0.7f)
                        .height(18.dp)
                        .clip(MaterialTheme.shapes.small),
                )
                ShimmerSurface(
                    Modifier
                        .width(96.dp)
                        .height(12.dp)
                        .clip(MaterialTheme.shapes.small),
                )
            }
        }
    }
}
