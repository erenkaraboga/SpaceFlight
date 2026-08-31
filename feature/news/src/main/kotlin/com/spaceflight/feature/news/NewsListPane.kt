package com.spaceflight.feature.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.spaceflight.core.designsystem.component.ArticleCard
import com.spaceflight.core.designsystem.component.ArticleCardPlaceholder
import com.spaceflight.core.designsystem.component.EmptyState
import com.spaceflight.core.designsystem.component.ErrorView
import com.spaceflight.core.designsystem.component.FeaturedArticleCard
import com.spaceflight.core.designsystem.component.InlineRetry
import com.spaceflight.core.designsystem.component.OfflineBanner
import com.spaceflight.core.designsystem.component.SearchTopBar
import com.spaceflight.core.designsystem.component.rememberStaggeredEntranceState
import com.spaceflight.core.designsystem.component.staggeredEntrance
import com.spaceflight.core.designsystem.motion.sharedArticleImage
import com.spaceflight.core.designsystem.motion.sharedArticleTitle
import com.spaceflight.core.designsystem.util.rememberRelativeDate
import com.spaceflight.core.domain.model.Article

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListPane(
    state: NewsUiState,
    articles: LazyPagingItems<Article>,
    onEvent: (NewsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val isRefreshing = articles.loadState.refresh is LoadState.Loading &&
        articles.itemCount > 0

    Column(modifier.fillMaxSize()) {
        SearchTopBar(
            title = stringResource(R.string.news_title),
            query = state.searchQuery,
            isSearchActive = state.isSearchActive,
            onQueryChange = { onEvent(NewsEvent.SearchQueryChanged(it)) },
            onSearchActiveChange = { onEvent(NewsEvent.SearchActiveChanged(it)) },
        )

        OfflineBanner(isVisible = state.isOffline)

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = articles::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                articles.isInitialLoad() -> LoadingList()

                articles.isInitialFailure() -> ErrorView(
                    title = stringResource(R.string.news_error_title),
                    description = articles.refreshErrorMessage(),
                    onRetry = articles::retry,
                    modifier = Modifier.fillMaxSize(),
                )

                articles.isEmptyResult() -> EmptyState(
                    title = stringResource(R.string.news_empty_title),
                    description = stringResource(
                        R.string.news_empty_description,
                        state.searchQuery.trim(),
                    ),
                    icon = Icons.Rounded.SearchOff,
                    modifier = Modifier.fillMaxSize(),
                )

                else -> ArticleList(
                    state = state,
                    articles = articles,
                    listState = listState,
                    onEvent = onEvent,
                )
            }
        }
    }
}

@Composable
private fun ArticleList(
    state: NewsUiState,
    articles: LazyPagingItems<Article>,
    listState: LazyListState,
    onEvent: (NewsEvent) -> Unit,
) {
    // Only the very first item of an unfiltered feed becomes the hero; during a search every result
    // is equally relevant, so the editorial treatment would be misleading.
    val showFeatured = state.searchQuery.isBlank()
    val entranceState = rememberStaggeredEntranceState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            count = articles.itemCount,
            key = { index -> articles.peek(index)?.id ?: index },
        ) { index ->
            val article = articles[index] ?: return@items
            val isFavorite = article.id in state.favoriteIds
            val dateLabel = rememberRelativeDate(article.publishedAt)
            val itemModifier = Modifier
                .animateItem()
                .staggeredEntrance(index, entranceState, key = article.id)

            if (showFeatured && index == 0) {
                FeaturedArticleCard(
                    title = article.title,
                    imageUrl = article.imageUrl,
                    newsSite = article.newsSite,
                    dateLabel = dateLabel,
                    isFavorite = isFavorite,
                    onClick = { onEvent(NewsEvent.ArticleSelected(article.id)) },
                    onFavoriteClick = { onEvent(NewsEvent.FavoriteToggled(article)) },
                    imageModifier = Modifier.sharedArticleImage(article.id),
                    titleModifier = Modifier.sharedArticleTitle(article.id),
                    modifier = itemModifier,
                )
            } else {
                ArticleCard(
                    title = article.title,
                    summary = article.summary,
                    imageUrl = article.imageUrl,
                    newsSite = article.newsSite,
                    dateLabel = dateLabel,
                    isFavorite = isFavorite,
                    isSelected = article.id == state.selectedArticleId,
                    onClick = { onEvent(NewsEvent.ArticleSelected(article.id)) },
                    onFavoriteClick = { onEvent(NewsEvent.FavoriteToggled(article)) },
                    imageModifier = Modifier.sharedArticleImage(article.id),
                    titleModifier = Modifier.sharedArticleTitle(article.id),
                    modifier = itemModifier,
                )
            }
        }

        when (val append = articles.loadState.append) {
            is LoadState.Loading -> item { ArticleCardPlaceholder() }
            is LoadState.Error -> item {
                InlineRetry(
                    message = stringResource(append.error.messageResId()),
                    onRetry = articles::retry,
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun LoadingList(modifier: Modifier = Modifier) {
    val entranceState = rememberStaggeredEntranceState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(PlaceholderCount) {
            ArticleCardPlaceholder(Modifier.staggeredEntrance(it, entranceState))
        }
    }
}

private const val PlaceholderCount = 6
