package com.spaceflight.feature.news.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.PhotoFavoriteButton
import com.spaceflight.designsystem.component.RemoteImage

/**
 * An image-led tile component designed for two-column grid layouts.
 * Summary is omitted to keep the grid compact and scannable.
 *
 * @param title The headline of the article. Truncated to a maximum of 3 lines.
 * @param imageUrl The remote URL of the article image.
 * @param dateLabel The formatted publication date string.
 * @param isFavorite Indicates whether the article is marked as favorite.
 * @param onClick Callback invoked when the article tile is clicked.
 * @param onFavoriteClick Callback invoked when the favorite button is clicked.
 * @param modifier The [Modifier] to be applied to the outer container layout.
 * @param imageModifier Additional [Modifier] to be applied directly to the image component.
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

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "Article Tile - Default", showBackground = true, widthDp = 180)
@Composable
private fun ArticleTilePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(8.dp)) {
            ArticleTile(
                title = "James Webb Telescope Discovers New Exoplanet",
                imageUrl = "",
                dateLabel = "3 hours ago",
                isFavorite = false,
                onClick = {},
                onFavoriteClick = {},
            )
        }
    }
}

@Preview(name = "Article Tile - Favorited", showBackground = true, widthDp = 180)
@Composable
private fun ArticleTileFavoritePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(8.dp)) {
            ArticleTile(
                title = "Falcon Heavy Successfully Launches Next-Gen Satellite",
                imageUrl = "",
                dateLabel = "5 hours ago",
                isFavorite = true,
                onClick = {},
                onFavoriteClick = {},
            )
        }
    }
}