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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.spaceflight.core.ui.error.toAppErrorOrUnknown
import com.spaceflight.core.ui.error.toUiText
import com.spaceflight.designsystem.text.asString
import com.spaceflight.feature.news.logic.NewsEffect
import com.spaceflight.feature.news.logic.NewsViewModel
import com.spaceflight.feature.news.logic.appendError
import com.spaceflight.feature.news.logic.refreshError

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

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NewsEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(effect.text.asString(context))
            }
        }
    }

    val refreshError = articles.refreshError()
    LaunchedEffect(refreshError) {
        if (refreshError != null) {
            val message = refreshError.toAppErrorOrUnknown().toUiText().asString(context)
            snackbarHostState.showSnackbar(message)
        }
    }

    val appendError = articles.appendError()
    LaunchedEffect(appendError) {
        if (appendError != null) {
            val message = appendError.toAppErrorOrUnknown().toUiText().asString(context)
            snackbarHostState.showSnackbar(message)
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
