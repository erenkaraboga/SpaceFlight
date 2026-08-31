package com.spaceflight.feature.news.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.FavoriteButton
import com.spaceflight.designsystem.component.PillBadge
import com.spaceflight.designsystem.component.RemoteImage
import com.spaceflight.designsystem.component.ShimmerSurface
import com.spaceflight.designsystem.theme.EyebrowTextStyle
import com.spaceflight.designsystem.theme.imageScrim

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
