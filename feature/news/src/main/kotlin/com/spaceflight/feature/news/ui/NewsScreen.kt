package com.spaceflight.feature.news.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.spaceflight.designsystem.component.FloatingBanner
import com.spaceflight.feature.news.R
import com.spaceflight.feature.news.presentation.NewsEffect
import com.spaceflight.feature.news.presentation.NewsViewModel
import kotlinx.coroutines.delay

@Composable
fun NewsScreen(
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val articles = viewModel.articles.collectAsLazyPagingItems()

    val context = LocalContext.current
    val refreshFailedMessage = stringResource(R.string.news_refresh_failed)
    var bannerMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NewsEffect.ShowMessage ->
                    bannerMessage = context.getString(effect.messageResId)
            }
        }
    }

    val refreshError = articles.refreshError()
    LaunchedEffect(refreshError) {
        if (refreshError != null && articles.itemCount > 0) {
            bannerMessage = refreshFailedMessage
        }
    }

    LaunchedEffect(bannerMessage) {
        if (bannerMessage == null) return@LaunchedEffect
        delay(BannerDurationMillis)
        bannerMessage = null
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { _ ->
        Box(Modifier.fillMaxSize()) {
            NewsList(
                state = state,
                articles = articles,
                onEvent = viewModel::onEvent,
                onArticleClick = onArticleClick,
            )
            FloatingBanner(
                visible = bannerMessage != null,
                message = bannerMessage.orEmpty(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, bottom = BannerBottomPadding),
            )
        }
    }
}

private const val BannerDurationMillis = 3_200L
private val BannerBottomPadding = 88.dp
