package com.spaceflight.feature.news.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.spaceflight.core.model.Article
import com.spaceflight.designsystem.component.ArticleCardPlaceholder
import com.spaceflight.designsystem.component.EmptyState
import com.spaceflight.designsystem.component.OfflineBanner
import com.spaceflight.designsystem.component.ScreenCanvas
import com.spaceflight.designsystem.component.SearchHeader
import com.spaceflight.designsystem.component.rememberStaggeredEntranceState
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.designsystem.util.rememberTodayDate
import com.spaceflight.feature.news.R
import com.spaceflight.feature.news.presentation.components.FeedArticle
import com.spaceflight.feature.news.presentation.components.HeroArticleCardPlaceholder
import com.spaceflight.feature.news.presentation.components.LatestSectionHeader
import com.spaceflight.feature.news.presentation.components.NewsLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsList(
    state: NewsUiState,
    articles: LazyPagingItems<Article>,
    onEvent: (NewsEvent) -> Unit,
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val isGrid = state.isGridLayout
    val toggleGrid: () -> Unit = { onEvent(NewsEvent.LayoutToggled) }
    val isRefreshing = articles.loadState.refresh is LoadState.Loading && articles.itemCount > 0
    val refreshState = articles.loadState.refresh
    ScreenCanvas(modifier) {
        Column(Modifier.fillMaxSize()) {
            SearchHeader(
                title = stringResource(R.string.news_title),
                subtitle = stringResource(R.string.news_welcome),
                dateLabel = rememberTodayDate(),
                query = state.searchQuery,
                isSearchActive = state.isSearchActive,
                onQueryChange = { onEvent(NewsEvent.SearchQueryChanged(it)) },
                onSearchActiveChange = { onEvent(NewsEvent.SearchActiveChanged(it)) },
            )

            OfflineBanner(isVisible = state.isOffline)
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    if (articles.loadState.refresh !is LoadState.Loading) {
                        articles.refresh()
                    }
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    refreshState is LoadState.Loading -> {
                        NewsLoading()
                    }
                    refreshState is LoadState.NotLoading && articles.itemCount == 0 -> {
                        EmptyState(
                            title = stringResource(R.string.news_empty_title),
                            description = if (state.searchQuery.isBlank()) {
                                stringResource(R.string.news_empty_generic_description)
                            } else {
                                stringResource(
                                    R.string.news_empty_description,
                                    state.searchQuery.trim()
                                )
                            },
                            icon = Icons.Rounded.SearchOff,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    else -> {
                        ArticleFeed(
                            state = state,
                            articles = articles,
                            isGrid = isGrid,
                            onToggleLayout = toggleGrid,
                            gridState = gridState,
                            onEvent = onEvent,
                            onArticleClick = onArticleClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleFeed(
    state: NewsUiState,
    articles: LazyPagingItems<Article>,
    isGrid: Boolean,
    onToggleLayout: () -> Unit,
    gridState: LazyGridState,
    onEvent: (NewsEvent) -> Unit,
    onArticleClick: (Int) -> Unit,
) {
    val showFeatured = state.searchQuery.isBlank()
    val entranceState = rememberStaggeredEntranceState()
    val feedPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 108.dp)

    val columns = if (isGrid) GridCells.Fixed(2) else GridCells.Fixed(1)

    LazyVerticalGrid(
        columns = columns,
        state = gridState,
        contentPadding = feedPadding,
        verticalArrangement = Arrangement.spacedBy(if (isGrid) 12.dp else 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (showFeatured && articles.itemCount > 0) {
            item(
                key = "featured",
                span = { GridItemSpan(maxLineSpan) },
            ) {
                val article = articles[0]
                if (article == null) {
                    HeroArticleCardPlaceholder(modifier = feedItemModifier())
                } else {
                    FeedArticle(
                        article = article,
                        index = 0,
                        featured = true,
                        grid = false,
                        state = state,
                        entranceState = entranceState,
                        onEvent = onEvent,
                        onArticleClick = onArticleClick,
                        modifier = feedItemModifier(),
                    )
                }
            }
        }

        item(
            key = "latest-header",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            LatestSectionHeader(
                isGrid = isGrid,
                onToggleLayout = onToggleLayout,
            )
        }

        val startIndex = if (showFeatured && articles.itemCount > 0) 1 else 0
        val itemCount = (articles.itemCount - startIndex).coerceAtLeast(0)

        items(
            count = itemCount,
            key = { relativeIndex ->
                val actualIndex = relativeIndex + startIndex
                articles.peek(actualIndex)?.id ?: actualIndex
            },
        ) { relativeIndex ->
            val actualIndex = relativeIndex + startIndex
            val article = articles[actualIndex]

            if (article != null) {
                FeedArticle(
                    article = article,
                    index = actualIndex,
                    featured = false,
                    grid = isGrid,
                    state = state,
                    entranceState = entranceState,
                    onEvent = onEvent,
                    onArticleClick = onArticleClick,
                    modifier = feedItemModifier(),
                )
            } else {
                ArticleCardPlaceholder(modifier = feedItemModifier())
            }
        }

        if (articles.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ArticleCardPlaceholder(modifier = feedItemModifier())
            }
        }
    }
}

private fun LazyGridItemScope.feedItemModifier(): Modifier = Modifier.animateItem(
    fadeInSpec = FeedFadeIn,
    fadeOutSpec = FeedFadeOut,
    placementSpec = FeedPlacement,
)

private val FeedFadeIn = tween<Float>(
    durationMillis = SpaceflightMotion.FadeThroughEnterMillis,
    easing = FastOutSlowInEasing,
)
private val FeedFadeOut = tween<Float>(180)
private val FeedPlacement = spring<IntOffset>(
    dampingRatio = 0.9f,
    stiffness = 380f,
)