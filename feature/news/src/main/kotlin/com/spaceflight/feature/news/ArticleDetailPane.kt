package com.spaceflight.feature.news

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.spaceflight.core.designsystem.component.ArticleDetailContent
import com.spaceflight.core.designsystem.component.EmptyState
import com.spaceflight.core.designsystem.component.FavoriteButton
import com.spaceflight.core.designsystem.component.heroTitleAlpha
import com.spaceflight.core.designsystem.motion.sharedArticleImage
import com.spaceflight.core.designsystem.motion.sharedArticleTitle
import com.spaceflight.core.designsystem.util.rememberAbsoluteDate
import com.spaceflight.core.domain.model.Article

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailPane(
    article: Article?,
    isFavorite: Boolean,
    showBackButton: Boolean,
    onEvent: (NewsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (article == null) {
        EmptyState(
            title = stringResource(R.string.news_detail_placeholder_title),
            description = stringResource(R.string.news_detail_placeholder_description),
            icon = Icons.AutoMirrored.Rounded.Article,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    val scrollState = rememberScrollState()
    // The hero owns the headline until it scrolls away; only then does the bar take over, so the
    // title is never rendered twice.
    val showTitleInBar by remember {
        derivedStateOf { heroTitleAlpha(scrollState.value) < 0.15f }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = showTitleInBar,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Text(
                            text = article.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = { onEvent(NewsEvent.DetailDismissed) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.news_back),
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(NewsEvent.ShareRequested(article)) }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.news_share),
                        )
                    }
                    FavoriteButton(
                        isFavorite = isFavorite,
                        onClick = { onEvent(NewsEvent.FavoriteToggled(article)) },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Box(Modifier.padding(top = innerPadding.calculateTopPadding())) {
            ArticleDetailContent(
                title = article.title,
                summary = article.summary,
                imageUrl = article.imageUrl,
                newsSite = article.newsSite,
                authors = article.authors,
                dateLabel = rememberAbsoluteDate(article.publishedAt),
                launchCount = article.launchCount,
                eventCount = article.eventCount,
                onReadMore = { onEvent(NewsEvent.SourceRequested(article)) },
                scrollState = scrollState,
                imageModifier = Modifier.sharedArticleImage(article.id),
                titleModifier = Modifier.sharedArticleTitle(article.id),
            )
        }
    }
}
