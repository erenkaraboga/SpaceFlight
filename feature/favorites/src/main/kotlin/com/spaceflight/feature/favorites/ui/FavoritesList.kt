package com.spaceflight.feature.favorites.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spaceflight.core.domain.model.Article
import com.spaceflight.designsystem.component.EmptyState
import com.spaceflight.designsystem.component.ScreenCanvas
import com.spaceflight.designsystem.component.ScreenHeader
import com.spaceflight.designsystem.component.StaggeredEntranceState
import com.spaceflight.designsystem.component.rememberStaggeredEntranceState
import com.spaceflight.designsystem.component.staggeredEntrance
import com.spaceflight.designsystem.motion.sharedContent
import com.spaceflight.designsystem.motion.sharedImageKey
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.designsystem.util.rememberRelativeDate
import com.spaceflight.feature.favorites.R
import com.spaceflight.feature.favorites.presentation.FavoritesEvent
import com.spaceflight.feature.favorites.presentation.FavoritesUiState

@Composable
fun FavoritesList(
    state: FavoritesUiState,
    onEvent: (FavoritesEvent) -> Unit,
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entranceState = rememberStaggeredEntranceState()

    ScreenCanvas(modifier) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                title = stringResource(R.string.favorites_title),
                subtitle = stringResource(R.string.favorites_subtitle),
            )

            when {
                state.isLoading -> Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    repeat(PlaceholderCount) {
                        ArticleCardPlaceholder(
                            Modifier.staggeredEntrance(
                                index = it,
                                state = entranceState,
                                key = "placeholder-$it",
                            )
                        )
                    }
                }

                state.favorites.isEmpty() -> EmptyState(
                    title = stringResource(R.string.favorites_empty_title),
                    description = stringResource(R.string.favorites_empty_description),
                    icon = Icons.Rounded.FavoriteBorder,
                    modifier = Modifier.fillMaxSize(),
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 108.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(
                        items = state.favorites,
                        key = { _, article -> article.id },
                    ) { index, article ->
                        SwipeToRemoveRow(
                            article = article,
                            index = index,
                            entranceState = entranceState,
                            onEvent = onEvent,
                            onArticleClick = onArticleClick,
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(
                                    durationMillis = SpaceflightMotion.FadeThroughEnterMillis,
                                    easing = FastOutSlowInEasing,
                                ),
                                fadeOutSpec = tween(180),
                                placementSpec = spring(dampingRatio = 0.9f, stiffness = 380f),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToRemoveRow(
    article: Article,
    index: Int,
    entranceState: StaggeredEntranceState,
    onEvent: (FavoritesEvent) -> Unit,
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            onEvent(FavoritesEvent.FavoriteRemoved(article))
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { SwipeBackground(progress = dismissState.progress) },
        modifier = modifier,
    ) {
        ArticleCard(
            title = article.title,
            summary = article.summary,
            imageUrl = article.imageUrl,
            newsSite = article.newsSite,
            dateLabel = rememberRelativeDate(article.publishedAt),
            isFavorite = true,
            onClick = { onArticleClick(article.id) },
            onFavoriteClick = { onEvent(FavoritesEvent.FavoriteRemoved(article)) },
            modifier = Modifier.staggeredEntrance(
                index = index,
                state = entranceState,
                key = article.id,
            ),
            imageModifier = Modifier.sharedContent(sharedImageKey(article.id)),
        )
    }
}

@Composable
private fun SwipeBackground(progress: Float, modifier: Modifier = Modifier) {
    val container by animateColorAsState(
        targetValue = if (progress > 0.4f) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.errorContainer
        },
        label = "swipeBackground",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .background(container)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            imageVector = Icons.Rounded.Delete,
            contentDescription = stringResource(R.string.favorites_remove),
            tint = MaterialTheme.colorScheme.onError,
            modifier = Modifier
                .size(24.dp)
                .scale(0.8f + progress.coerceIn(0f, 1f) * 0.4f),
        )
    }
}

private const val PlaceholderCount = 4
