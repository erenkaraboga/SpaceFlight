@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalSharedTransitionApi::class)

package com.spaceflight.feature.favorites

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import com.spaceflight.core.designsystem.motion.LocalPaneAnimatedVisibilityScope
import com.spaceflight.core.designsystem.motion.LocalSharedTransitionScope
import com.spaceflight.core.designsystem.util.openArticleInCustomTab
import com.spaceflight.core.designsystem.util.shareArticle

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()
    val chooserTitle = stringResource(R.string.favorites_share_chooser)
    val noBrowserMessage = stringResource(R.string.favorites_no_browser)
    val removedMessage = stringResource(R.string.favorites_removed)
    val undoLabel = stringResource(R.string.favorites_undo)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FavoritesEffect.OpenInBrowser ->
                    if (!context.openArticleInCustomTab(effect.url, toolbarColor)) {
                        snackbarHostState.showSnackbar(noBrowserMessage)
                    }

                is FavoritesEffect.ShareArticle ->
                    if (!context.shareArticle(effect.title, effect.url, chooserTitle)) {
                        snackbarHostState.showSnackbar(noBrowserMessage)
                    }

                is FavoritesEffect.ShowUndoRemoval -> {
                    // Replaces any Snackbar still on screen, so rapid swipes only ever offer to
                    // undo the most recent removal rather than queueing up stale offers.
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = removedMessage,
                        actionLabel = undoLabel,
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(FavoritesEvent.UndoRemoval)
                    }
                }
            }
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
        viewModel.onEvent(FavoritesEvent.DetailDismissed)
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
                                FavoritesListPane(
                                    state = state,
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
                                FavoriteDetailPane(
                                    article = state.selectedArticle,
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
