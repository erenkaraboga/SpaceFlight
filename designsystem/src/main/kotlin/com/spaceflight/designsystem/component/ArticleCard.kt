package com.spaceflight.designsystem.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A card component that displays a news article item in a list. Shared by the News feed and the
 * Favorites list, which otherwise rendered the exact same card twice.
 *
 * @param title The headline of the article. Truncated to a maximum of 3 lines.
 * @param summary A brief summary of the article. Rendered below the title if not blank.
 * @param imageUrl The remote URL of the article image.
 * @param newsSite The source/publisher name of the article (e.g., NASA, SpaceNews).
 * @param dateLabel The formatted publication date string.
 * @param isFavorite Indicates whether the article is marked as favorite.
 * @param onClick Callback invoked when the article card is clicked.
 * @param onFavoriteClick Callback invoked when the favorite button is clicked.
 * @param modifier The [Modifier] to be applied to the outer container.
 * @param imageModifier Additional [Modifier] to be applied directly to the image component.
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
    imageModifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
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

            RemoteImage(
                imageUrl = imageUrl,
                contentDescription = null,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .size(96.dp)
                    .then(imageModifier),
            )
        }

        PhotoFavoriteButton(
            isFavorite = isFavorite,
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
        )
    }
}

/**
 * A placeholder skeleton component displayed while article data is loading.
 *
 * @param modifier The [Modifier] to be applied to the outer layout.
 */
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

/**
 * Internal shimmer bar used to construct loading placeholders.
 *
 * @param widthFraction The fraction of maximum width to occupy (0.0f to 1.0f).
 * @param height The height of the shimmer bar.
 */
@Composable
private fun ShimmerBar(widthFraction: Float, height: Dp) {
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

@Preview(name = "Article Card - Default", showBackground = true)
@Composable
private fun ArticleCardPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            ArticleCard(
                title = "Artemis III: NASA's Lunar South Pole Exploration Mission",
                summary = "NASA outlines key goals for human landing near the lunar south pole in upcoming mission.",
                imageUrl = "",
                newsSite = "NASA",
                dateLabel = "2 hours ago",
                isFavorite = false,
                onClick = {},
                onFavoriteClick = {},
            )
        }
    }
}

@Preview(name = "Article Card - Favorited", showBackground = true)
@Composable
private fun ArticleCardFavoritePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            ArticleCard(
                title = "Starship Prepares for Orbital Test Flight",
                summary = "SpaceX is finalizing preparations for the next launch iteration from Starbase.",
                imageUrl = "",
                newsSite = "SpaceNews",
                dateLabel = "1 day ago",
                isFavorite = true,
                onClick = {},
                onFavoriteClick = {},
            )
        }
    }
}

@Preview(name = "Article Card - Loading Placeholder", showBackground = true)
@Composable
private fun ArticleCardPlaceholderPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            ArticleCardPlaceholder()
        }
    }
}
