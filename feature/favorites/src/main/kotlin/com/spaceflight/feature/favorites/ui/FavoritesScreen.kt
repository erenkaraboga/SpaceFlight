package com.spaceflight.feature.favorites.ui

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spaceflight.designsystem.component.FloatingBanner
import com.spaceflight.feature.favorites.R
import com.spaceflight.feature.favorites.presentation.FavoritesEffect
import com.spaceflight.feature.favorites.presentation.FavoritesEvent
import com.spaceflight.feature.favorites.presentation.FavoritesViewModel
import kotlinx.coroutines.delay

@Composable
fun FavoritesScreen(
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val removedMessage = stringResource(R.string.favorites_removed)
    val undoLabel = stringResource(R.string.favorites_undo)

    var showUndo by remember { mutableStateOf(false) }
    var undoGeneration by remember { mutableIntStateOf(0) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FavoritesEffect.ShowUndoRemoval -> {
                    showUndo = true
                    undoGeneration++
                }
            }
        }
    }

    LaunchedEffect(showUndo, undoGeneration) {
        if (!showUndo) return@LaunchedEffect
        delay(BannerDurationMillis)
        showUndo = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { _ ->
        Box(Modifier.fillMaxSize()) {
            FavoritesList(
                state = state,
                onEvent = viewModel::onEvent,
                onArticleClick = onArticleClick,
            )
            FloatingBanner(
                visible = showUndo,
                message = removedMessage,
                actionLabel = undoLabel,
                onAction = {
                    viewModel.onEvent(FavoritesEvent.UndoRemoval)
                    showUndo = false
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, bottom = BannerBottomPadding),
            )
        }
    }
}

private const val BannerDurationMillis = 4_000L
private val BannerBottomPadding = 88.dp
