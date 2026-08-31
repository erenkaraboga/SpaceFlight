package com.spaceflight.feature.news.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.spaceflight.feature.news.R
import com.spaceflight.feature.news.presentation.NewsEffect
import com.spaceflight.feature.news.presentation.NewsViewModel

@Composable
fun NewsScreen(
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val articles = viewModel.articles.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val refreshFailedMessage = stringResource(R.string.news_refresh_failed)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NewsEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(effect.messageResId))
            }
        }
    }

    val refreshError = articles.refreshError()
    LaunchedEffect(refreshError) {
        if (refreshError != null && articles.itemCount > 0) {
            snackbarHostState.showSnackbar(refreshFailedMessage)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { _ ->
        NewsList(
            state = state,
            articles = articles,
            onEvent = viewModel::onEvent,
            onArticleClick = onArticleClick,
        )
    }
}
