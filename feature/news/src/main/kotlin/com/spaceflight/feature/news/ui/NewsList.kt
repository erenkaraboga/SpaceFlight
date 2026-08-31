package com.spaceflight.feature.news.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import com.spaceflight.core.domain.model.Article
import com.spaceflight.designsystem.component.EmptyState
import com.spaceflight.designsystem.component.ErrorView
import com.spaceflight.designsystem.component.InlineRetry
import com.spaceflight.designsystem.component.OfflineBanner
import com.spaceflight.designsystem.component.ScreenCanvas
import com.spaceflight.designsystem.component.SearchHeader
import com.spaceflight.designsystem.component.SectionHeader
import com.spaceflight.designsystem.component.rememberStaggeredEntranceState
import com.spaceflight.designsystem.component.staggeredEntrance
import com.spaceflight.designsystem.motion.sharedContent
import com.spaceflight.designsystem.motion.sharedImageKey
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.designsystem.util.rememberRelativeDate
import com.spaceflight.feature.news.R
import com.spaceflight.feature.news.presentation.NewsEvent
import com.spaceflight.feature.news.presentation.NewsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsList(
    state: NewsUiState,
    articles: LazyPagingItems<Article>,
    onEvent: (NewsEvent) -> Unit,
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val isRefreshing = articles.loadState.refresh is LoadState.Loading &&
        articles.itemCount > 0

    ScreenCanvas(modifier) {
        Column(Modifier.fillMaxSize()) {
            SearchHeader(
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

                    else -> ArticleFeed(
                        state = state,
                        articles = articles,
                        listState = listState,
                        onEvent = onEvent,
                        onArticleClick = onArticleClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleFeed(
    state: NewsUiState,
    articles: LazyPagingItems<Article>,
    listState: LazyListState,
    onEvent: (NewsEvent) -> Unit,
    onArticleClick: (Int) -> Unit,
) {
    val showFeatured = state.searchQuery.isBlank()
    val entranceState = rememberStaggeredEntranceState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
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
                .animateItem(
                    fadeInSpec = tween(
                        durationMillis = SpaceflightMotion.FadeThroughEnterMillis,
                        easing = FastOutSlowInEasing,
                    ),
                    fadeOutSpec = tween(180),
                    placementSpec = spring(dampingRatio = 0.9f, stiffness = 380f),
                )
                .staggeredEntrance(
                    index = index,
                    state = entranceState,
                    key = article.id,
                )
            val imageModifier = Modifier.sharedContent(sharedImageKey(article.id))

            if (showFeatured && index == 0) {
                HeroArticleCard(
                    eyebrow = stringResource(R.string.news_featured),
                    title = article.title,
                    imageUrl = article.imageUrl,
                    newsSite = article.newsSite,
                    dateLabel = dateLabel,
                    isFavorite = isFavorite,
                    onClick = { onArticleClick(article.id) },
                    onFavoriteClick = { onEvent(NewsEvent.FavoriteToggled(article)) },
                    modifier = itemModifier,
                    imageModifier = imageModifier,
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = itemModifier,
                ) {
                    if (showFeatured && index == 1) {
                        SectionHeader(
                            title = stringResource(R.string.news_latest),
                            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                        )
                    }
                    ArticleCard(
                        title = article.title,
                        summary = article.summary,
                        imageUrl = article.imageUrl,
                        newsSite = article.newsSite,
                        dateLabel = dateLabel,
                        isFavorite = isFavorite,
                        onClick = { onArticleClick(article.id) },
                        onFavoriteClick = { onEvent(NewsEvent.FavoriteToggled(article)) },
                        imageModifier = imageModifier,
                    )
                }
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        HeroArticleCardPlaceholder(Modifier.staggeredEntrance(0, entranceState))
        SectionHeader(title = stringResource(R.string.news_latest))
        repeat(PlaceholderCount) {
            ArticleCardPlaceholder(Modifier.staggeredEntrance(it + 1, entranceState))
        }
    }
}

private const val PlaceholderCount = 5
