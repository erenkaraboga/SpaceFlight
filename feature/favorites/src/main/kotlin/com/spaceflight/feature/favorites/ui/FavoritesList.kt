package com.spaceflight.feature.favorites.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.ScreenCanvas
import com.spaceflight.designsystem.component.ScreenHeader
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

                state.favorites.isEmpty() -> FavoritesEmpty()

                else -> LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 24.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(
                        items = state.favorites,
                        key = { _, article -> article.id },
                    ) { index, article ->
                        ArticleCard(
                            title = article.title,
                            summary = article.summary,
                            imageUrl = article.imageUrl,
                            newsSite = article.newsSite,
                            dateLabel = rememberRelativeDate(article.publishedAt),
                            isFavorite = true,
                            onClick = { onArticleClick(article.id) },
                            onFavoriteClick = { onEvent(FavoritesEvent.FavoriteRemoved(article)) },
                            modifier = Modifier
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
                                ),
                            imageModifier = Modifier.sharedContent(
                                sharedImageKey(article.id),
                                clipShape = RoundedCornerShape(18.dp),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(R.string.favorites_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.favorites_empty_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private const val PlaceholderCount = 4