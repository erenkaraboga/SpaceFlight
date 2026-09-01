package com.spaceflight.feature.news.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.FavoriteButton
import com.spaceflight.designsystem.component.PillBadge
import com.spaceflight.designsystem.component.RemoteImage
import com.spaceflight.designsystem.component.ShimmerSurface
import com.spaceflight.designsystem.theme.EyebrowTextStyle
import com.spaceflight.designsystem.theme.imageScrim

/**
 * A featured full-width hero card component designed for high-priority news articles.
 * Adapts its aspect ratio based on device orientation (16:9 for landscape, 4:5 for portrait).
 *
 * @param eyebrow Category or callout label displayed in an uppercase badge at the top-left (e.g., "FEATURED", "BREAKING").
 * @param title The main headline of the article. Truncated to a maximum of 4 lines.
 * @param imageUrl The remote URL of the full-bleed background image.
 * @param newsSite The source/publisher name of the article (e.g., NASA, SpaceNews).
 * @param dateLabel The formatted publication date string.
 * @param isFavorite Indicates whether the article is marked as favorite.
 * @param onClick Callback invoked when the hero card is clicked.
 * @param onFavoriteClick Callback invoked when the favorite button is clicked.
 * @param modifier The [Modifier] to be applied to the outer container.
 * @param imageModifier Additional [Modifier] to be applied to the background image container.
 */
@Composable
fun HeroArticleCard(
    eyebrow: String,
    title: String,
    imageUrl: String,
    newsSite: String,
    dateLabel: String,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
) {
    val isLandscape = with(LocalWindowInfo.current.containerSize) { width > height }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (isLandscape) 16f / 9f else 4f / 5f)
            .clip(MaterialTheme.shapes.extraLarge)
            .then(imageModifier)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick),
    ) {
        RemoteImage(
            imageUrl = imageUrl,
            contentDescription = null,
            shape = RectangleShape,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(imageScrim)
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = eyebrow.uppercase(),
                style = EyebrowTextStyle,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
            FavoriteButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick,
                containerColor = Color.Black.copy(alpha = 0.35f),
                inactiveTint = Color.White,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PillBadge(
                    text = newsSite,
                    containerColor = Color.White.copy(alpha = 0.16f),
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.78f),
                    maxLines = 1,
                )
            }
        }
    }
}

/**
 * A placeholder skeleton component displayed while hero article data is loading.
 *
 * @param modifier The [Modifier] to be applied to the outer layout.
 */
@Composable
fun HeroArticleCardPlaceholder(modifier: Modifier = Modifier) {
    val isLandscape = with(LocalWindowInfo.current.containerSize) { width > height }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (isLandscape) 16f / 9f else 4f / 5f)
            .clip(MaterialTheme.shapes.extraLarge)
    ) {
        ShimmerSurface(Modifier.fillMaxSize())
    }
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "Hero Article Card - Default", showBackground = true)
@Composable
private fun HeroArticleCardPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            HeroArticleCard(
                eyebrow = "Featured",
                title = "Artemis Program Prepares for Historic Return to the Moon",
                imageUrl = "",
                newsSite = "NASA",
                dateLabel = "1 hour ago",
                isFavorite = false,
                onClick = {},
                onFavoriteClick = {},
            )
        }
    }
}

@Preview(name = "Hero Article Card - Favorited", showBackground = true)
@Composable
private fun HeroArticleCardFavoritePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            HeroArticleCard(
                eyebrow = "Breaking",
                title = "Starship Achieves Full Orbital Insertion on Test Flight",
                imageUrl = "",
                newsSite = "SpaceX",
                dateLabel = " Just now",
                isFavorite = true,
                onClick = {},
                onFavoriteClick = {},
            )
        }
    }
}

@Preview(name = "Hero Article Card - Placeholder", showBackground = true)
@Composable
private fun HeroArticleCardPlaceholderPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            HeroArticleCardPlaceholder()
        }
    }
}