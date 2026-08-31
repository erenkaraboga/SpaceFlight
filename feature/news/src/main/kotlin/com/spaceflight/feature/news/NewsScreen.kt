@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalSharedTransitionApi::class)

package com.spaceflight.feature.news

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.spaceflight.core.designsystem.motion.LocalPaneAnimatedVisibilityScope
import com.spaceflight.core.designsystem.motion.LocalSharedTransitionScope
import com.spaceflight.core.designsystem.util.openArticleInCustomTab
import com.spaceflight.core.designsystem.util.shareArticle

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val articles = viewModel.articles.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()
    val chooserTitle = stringResource(R.string.news_share_chooser)
    val noBrowserMessage = stringResource(R.string.news_no_browser)
    val refreshFailedMessage = stringResource(R.string.news_refresh_failed)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NewsEffect.OpenInBrowser ->
                    if (!context.openArticleInCustomTab(effect.url, toolbarColor)) {
                        snackbarHostState.showSnackbar(noBrowserMessage)
                    }

                is NewsEffect.ShareArticle ->
                    if (!context.shareArticle(effect.title, effect.url, chooserTitle)) {
                        snackbarHostState.showSnackbar(noBrowserMessage)
                    }

                is NewsEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(effect.messageResId))
            }
        }
    }

    // A refresh that fails while the cache still has articles is a background problem, not a
    // dead end, so it gets a Snackbar instead of replacing the list with an error screen.
    val refreshError = articles.refreshError()
    LaunchedEffect(refreshError) {
        if (refreshError != null && articles.itemCount > 0) {
            snackbarHostState.showSnackbar(refreshFailedMessage)
        }
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val isSinglePane = navigator.scaffoldDirective.maxHorizontalPartitions == 1

    LaunchedEffect(state.selectedArticleId) {
        val selectedId = state.selectedArticleId
        if (selectedId != null) {
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, selectedId)
        } else if (navigator.canNavigateBack()) {
            navigator.navigateBack()
        }
    }

    BackHandler(enabled = isSinglePane && state.selectedArticleId != null) {
        viewModel.onEvent(NewsEvent.DetailDismissed)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        SharedTransitionLayout(Modifier.padding(innerPadding)) {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                ListDetailPaneScaffold(
                    directive = navigator.scaffoldDirective,
                    scaffoldState = navigator.scaffoldState,
                    listPane = {
                        AnimatedPane {
                            CompositionLocalProvider(
                                LocalPaneAnimatedVisibilityScope provides this@AnimatedPane
                            ) {
                                NewsListPane(
                                    state = state,
                                    articles = articles,
                                    onEvent = viewModel::onEvent,
                                )
                            }
                        }
                    },
                    detailPane = {
                        AnimatedPane {
                            CompositionLocalProvider(
                                LocalPaneAnimatedVisibilityScope provides this@AnimatedPane
                            ) {
                                ArticleDetailPane(
                                    article = state.selectedArticle,
                                    isFavorite = state.selectedArticleId in state.favoriteIds,
                                    showBackButton = isSinglePane,
                                    onEvent = viewModel::onEvent,
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
