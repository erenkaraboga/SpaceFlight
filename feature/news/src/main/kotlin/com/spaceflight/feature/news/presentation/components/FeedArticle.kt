package com.spaceflight.feature.news.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spaceflight.core.model.Article
import com.spaceflight.designsystem.component.ArticleCard
import com.spaceflight.designsystem.component.StaggeredEntranceState
import com.spaceflight.designsystem.component.staggeredEntrance
import com.spaceflight.designsystem.motion.sharedContent
import com.spaceflight.designsystem.motion.sharedImageKey
import com.spaceflight.designsystem.date.rememberRelativeDate
import com.spaceflight.feature.news.R
import com.spaceflight.feature.news.presentation.state.NewsEvent
import com.spaceflight.feature.news.presentation.state.NewsUiState


@Composable
fun FeedArticle(
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