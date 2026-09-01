package com.spaceflight.feature.news.ui

import LayoutToggleButton
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
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
import com.spaceflight.designsystem.component.StaggeredEntranceState
import com.spaceflight.designsystem.component.rememberStaggeredEntranceState
import com.spaceflight.designsystem.component.staggeredEntrance
import com.spaceflight.designsystem.motion.sharedContent
import com.spaceflight.designsystem.motion.sharedImageKey
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.designsystem.util.rememberRelativeDate
import com.spaceflight.designsystem.util.rememberTodayDate
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
    val gridState = rememberLazyGridState()
    val (isGrid, toggleGrid) = rememberGridLayout()
    val isRefreshing = articles.loadState.refresh is LoadState.Loading &&
        articles.itemCount > 0

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
                    articles.isInitialLoad() -> LoadingList(
                        isGrid = isGrid,
                        onToggleLayout = toggleGrid,
                    )

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
                        isGrid = isGrid,
                        onToggleLayout = toggleGrid,
                        listState = listState,
                        gridState = gridState,
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
    isGrid: Boolean,
    onToggleLayout: () -> Unit,
    listState: LazyListState,
    gridState: LazyGridState,
    onEvent: (NewsEvent) -> Unit,
    onArticleClick: (Int) -> Unit,
) {
    val showFeatured = state.searchQuery.isBlank()
    val entranceState = rememberStaggeredEntranceState()
    val feedPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 108.dp)

    if (isGrid) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            contentPadding = feedPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                item(
                    key = "latest-header",
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    LatestSectionHeader(
                        isGrid = isGrid,
                        onToggleLayout = onToggleLayout,
                    )
                }
            } else if (!showFeatured) {
                item(
                    key = "latest-header",
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    LatestSectionHeader(
                        isGrid = isGrid,
                        onToggleLayout = onToggleLayout,
                    )
                }
            }

            val tileOffset = if (showFeatured) 1 else 0
            items(
                count = (articles.itemCount - tileOffset).coerceAtLeast(0),
                key = { gridIndex -> articles.peek(gridIndex + tileOffset)?.id ?: gridIndex },
            ) { gridIndex ->
                val index = gridIndex + tileOffset
                val article = articles[index] ?: return@items
                FeedArticle(
                    article = article,
                    index = index,
                    featured = false,
                    grid = true,
                    state = state,
                    entranceState = entranceState,
                    onEvent = onEvent,
                    onArticleClick = onArticleClick,
                    modifier = feedItemModifier(),
                )
            }

            when (val append = articles.loadState.append) {
                is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    ArticleCardPlaceholder()
                }
                is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                    InlineRetry(
                        message = stringResource(append.error.messageResId()),
                        onRetry = articles::retry,
                    )
                }
                else -> Unit
            }
        }
    } else {
        LazyColumn(
            state = listState,
            contentPadding = feedPadding,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            if (showFeatured && articles.itemCount > 0) {
                item(key = "featured") {
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
                item(key = "latest-header") {
                    LatestSectionHeader(
                        isGrid = isGrid,
                        onToggleLayout = onToggleLayout,
                    )
                }
            } else if (!showFeatured) {
                item(key = "latest-header") {
                    LatestSectionHeader(
                        isGrid = isGrid,
                        onToggleLayout = onToggleLayout,
                    )
                }
            }

            val listOffset = if (showFeatured) 1 else 0
            items(
                count = (articles.itemCount - listOffset).coerceAtLeast(0),
                key = { listIndex -> articles.peek(listIndex + listOffset)?.id ?: listIndex },
            ) { listIndex ->
                val index = listIndex + listOffset
                val article = articles[index] ?: return@items
                FeedArticle(
                    article = article,
                    index = index,
                    featured = false,
                    grid = false,
                    state = state,
                    entranceState = entranceState,
                    onEvent = onEvent,
                    onArticleClick = onArticleClick,
                    modifier = feedItemModifier(),
                )
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
}

@Composable
private fun FeedArticle(
    article: Article,
    index: Int,
    featured: Boolean,
    grid: Boolean,
    state: NewsUiState,
    entranceState: StaggeredEntranceState,
    onEvent: (NewsEvent) -> Unit,
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFavorite = article.id in state.favoriteIds
    val dateLabel = rememberRelativeDate(article.publishedAt)
    val itemModifier = modifier.staggeredEntrance(
        index = index,
        state = entranceState,
        key = article.id,
    )
    val imageModifier = Modifier.sharedContent(
        key = sharedImageKey(article.id),
        clipShape = when {
            featured -> MaterialTheme.shapes.extraLarge
            grid -> MaterialTheme.shapes.large
            else -> RoundedCornerShape(18.dp)
        },
    )

    when {
        featured -> HeroArticleCard(
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
        grid -> ArticleTile(
            title = article.title,
            imageUrl = article.imageUrl,
            dateLabel = dateLabel,
            isFavorite = isFavorite,
            onClick = { onArticleClick(article.id) },
            onFavoriteClick = { onEvent(NewsEvent.FavoriteToggled(article)) },
            modifier = itemModifier,
            imageModifier = imageModifier,
        )
        else -> ArticleCard(
            title = article.title,
            summary = article.summary,
            imageUrl = article.imageUrl,
            newsSite = article.newsSite,
            dateLabel = dateLabel,
            isFavorite = isFavorite,
            onClick = { onArticleClick(article.id) },
            onFavoriteClick = { onEvent(NewsEvent.FavoriteToggled(article)) },
            modifier = itemModifier,
            imageModifier = imageModifier,
        )
    }
}

@Composable
private fun LatestSectionHeader(
    isGrid: Boolean,
    onToggleLayout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionHeader(
        title = stringResource(R.string.news_latest),
        modifier = modifier.padding(top = 8.dp, bottom = 2.dp),
        trailingContent = {
            LayoutToggleButton(isGrid = isGrid, onClick = onToggleLayout)
        },
    )
}

@Composable
private fun LoadingList(
    isGrid: Boolean,
    onToggleLayout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val entranceState = rememberStaggeredEntranceState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        HeroArticleCardPlaceholder(Modifier.staggeredEntrance(0, entranceState))
        LatestSectionHeader(isGrid = isGrid, onToggleLayout = onToggleLayout)
        repeat(PlaceholderCount) {
            ArticleCardPlaceholder(Modifier.staggeredEntrance(it + 1, entranceState))
        }
    }
}

private fun LazyItemScope.feedItemModifier(): Modifier = Modifier.animateItem(
    fadeInSpec = FeedFadeIn,
    fadeOutSpec = FeedFadeOut,
    placementSpec = FeedPlacement,
)

private fun LazyGridItemScope.feedItemModifier(): Modifier =
    Modifier.animateItem(
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

private const val PlaceholderCount = 5
