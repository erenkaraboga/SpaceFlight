package com.spaceflight.feature.favorites

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.Text
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
import com.spaceflight.core.designsystem.component.ArticleCard
import com.spaceflight.core.designsystem.component.ArticleCardPlaceholder
import com.spaceflight.core.designsystem.component.EmptyState
import com.spaceflight.core.designsystem.component.StaggeredEntranceState
import com.spaceflight.core.designsystem.component.rememberStaggeredEntranceState
import com.spaceflight.core.designsystem.component.staggeredEntrance
import com.spaceflight.core.designsystem.motion.sharedArticleImage
import com.spaceflight.core.designsystem.motion.sharedArticleTitle
import com.spaceflight.core.designsystem.util.rememberRelativeDate
import com.spaceflight.core.domain.model.Article

@Composable
fun FavoritesListPane(
    state: FavoritesUiState,
    onEvent: (FavoritesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entranceState = rememberStaggeredEntranceState()

    Column(modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.favorites_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
        )

        when {
            state.isLoading -> Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                repeat(PlaceholderCount) {
                    ArticleCardPlaceholder(
                        Modifier.staggeredEntrance(it, entranceState, key = "placeholder-$it")
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = state.favorites,
                    key = { _, article -> article.id },
                ) { index, article ->
                    SwipeToRemoveRow(
                        article = article,
                        isSelected = article.id == state.selectedArticleId,
                        index = index,
                        entranceState = entranceState,
                        onEvent = onEvent,
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}

/**
 * Removal is a swipe with an undo rather than a confirmation dialog: it keeps the list fluid, and
 * the Snackbar makes an accidental swipe cheap to reverse.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToRemoveRow(
    article: Article,
    isSelected: Boolean,
    index: Int,
    entranceState: StaggeredEntranceState,
    onEvent: (FavoritesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            onEvent(FavoritesEvent.FavoriteRemoved(article))
            // Reset immediately: the row disappears because the data changed, not because the box
            // stays dismissed, which is what lets an undo slot the same item back in.
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
            isSelected = isSelected,
            onClick = { onEvent(FavoritesEvent.ArticleSelected(article.id)) },
            onFavoriteClick = { onEvent(FavoritesEvent.FavoriteRemoved(article)) },
            imageModifier = Modifier.sharedArticleImage(article.id),
            titleModifier = Modifier.sharedArticleTitle(article.id),
            modifier = Modifier.staggeredEntrance(index, entranceState, key = article.id),
        )
    }
}

/** The bin grows and the backdrop deepens as the swipe approaches the release point. */
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
