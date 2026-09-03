package com.spaceflight.feature.newsdetail.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spaceflight.designsystem.component.EmptyState
import com.spaceflight.designsystem.component.FavoriteButton
import com.spaceflight.designsystem.component.GlassIconButton
import com.spaceflight.designsystem.motion.sharedContent
import com.spaceflight.designsystem.motion.sharedImageKey
import com.spaceflight.core.common.text.asString
import com.spaceflight.core.common.intent.openUrlInCustomTab
import com.spaceflight.designsystem.date.rememberAbsoluteDate
import com.spaceflight.core.common.intent.shareText
import com.spaceflight.feature.newsdetail.R
import com.spaceflight.feature.newsdetail.presentation.components.ArticleDetailContent
import com.spaceflight.feature.newsdetail.presentation.components.heroTitleAlpha
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailEffect
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailEvent
import kotlinx.coroutines.launch

@Composable
fun NewsDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()
    val chooserTitle = stringResource(R.string.newsdetail_share_chooser)
    val noBrowserMessage = stringResource(R.string.newsdetail_no_browser)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                NewsDetailEffect.NavigateBack -> onBack()

                is NewsDetailEffect.OpenInBrowser ->
                    if (!context.openUrlInCustomTab(effect.url, toolbarColor)) {
                        scope.launch { snackbarHostState.showSnackbar(noBrowserMessage) }
                    }

                is NewsDetailEffect.ShareArticle ->
                    if (!context.shareText(
                            effect.title,
                            "${effect.title}\n\n${effect.url}",
                            chooserTitle,
                        )
                    ) {
                        scope.launch { snackbarHostState.showSnackbar(noBrowserMessage) }
                    }

                is NewsDetailEffect.ShowMessage -> {
                    val message = effect.text.asString(context)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { _ ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            state.article == null -> EmptyState(
                title = stringResource(R.string.newsdetail_missing_title),
                description = stringResource(R.string.newsdetail_missing_description),
                icon = Icons.Rounded.SearchOff,
                modifier = Modifier.fillMaxSize(),
            )

            else -> {
                val scrollState = rememberScrollState()
                val showTitleInBar by remember {
                    derivedStateOf { heroTitleAlpha(scrollState.value) < 0.15f }
                }
                state.article?.let { article ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                    ) {
                        ArticleDetailContent(
                            title = article.title,
                            summary = article.summary,
                            imageUrl = article.imageUrl,
                            newsSite = article.newsSite,
                            authors = article.authors,
                            dateLabel = rememberAbsoluteDate(article.publishedAt),
                            launchCount = article.launchCount,
                            eventCount = article.eventCount,
                            onReadMore = { viewModel.onEvent(NewsDetailEvent.SourceRequested) },
                            imageModifier = Modifier.sharedContent(
                                sharedImageKey(article.id),
                                clipShape = MaterialTheme.shapes.extraLarge,
                            ),
                            scrollState = scrollState,
                        )
                        DetailChrome(
                            title = article.title,
                            showTitle = showTitleInBar,
                            isFavorite = state.isFavorite,
                            onBack = { viewModel.onEvent(NewsDetailEvent.BackClicked) },
                            onShare = { viewModel.onEvent(NewsDetailEvent.ShareRequested) },
                            onFavorite = { viewModel.onEvent(NewsDetailEvent.FavoriteToggled) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailChrome(
    title: String,
    showTitle: Boolean,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit,
) {
    val glass = Color.Black.copy(alpha = 0.38f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GlassIconButton(onClick = onBack, containerColor = glass) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.newsdetail_back),
                tint = Color.White,
            )
        }

        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
        if (!showTitle) {
            Box(Modifier.weight(1f))
        }

        GlassIconButton(onClick = onShare, containerColor = glass) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = stringResource(R.string.newsdetail_share),
                tint = Color.White,
            )
        }
        FavoriteButton(
            isFavorite = isFavorite,
            onClick = onFavorite,
            containerColor = glass,
            inactiveTint = Color.White,
        )
    }
}