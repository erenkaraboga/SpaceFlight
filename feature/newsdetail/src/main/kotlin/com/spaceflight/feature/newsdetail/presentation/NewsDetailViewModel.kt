package com.spaceflight.feature.newsdetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spaceflight.core.domain.usecase.ObserveArticleUseCase
import com.spaceflight.core.domain.usecase.ObserveIsFavoriteUseCase
import com.spaceflight.core.domain.usecase.RefreshArticleUseCase
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
import com.spaceflight.feature.newsdetail.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeArticle: ObserveArticleUseCase,
    observeIsFavorite: ObserveIsFavoriteUseCase,
    private val refreshArticle: RefreshArticleUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val articleId: Int = checkNotNull(savedStateHandle["articleId"]) {
        "NewsDetailRoute.articleId is required"
    }

    private val _uiState = MutableStateFlow(NewsDetailUiState())
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

    private val _effects = Channel<NewsDetailEffect>(Channel.BUFFERED)
    val effects: Flow<NewsDetailEffect> = _effects.receiveAsFlow()

    init {
        observeArticle(articleId)
            .onEach { article ->
                _uiState.update { it.copy(article = article, isLoading = false) }
            }
            .launchIn(viewModelScope)

        observeIsFavorite(articleId)
            .onEach { isFavorite -> _uiState.update { it.copy(isFavorite = isFavorite) } }
            .launchIn(viewModelScope)

        refresh()
    }

    fun onEvent(event: NewsDetailEvent) {
        when (event) {
            NewsDetailEvent.BackClicked -> viewModelScope.launch {
                _effects.send(NewsDetailEffect.NavigateBack)
            }

            NewsDetailEvent.FavoriteToggled -> viewModelScope.launch {
                val article = _uiState.value.article ?: return@launch
                val added = toggleFavorite(article)
                _effects.send(
                    NewsDetailEffect.ShowMessage(
                        if (added) R.string.newsdetail_added_to_favorites
                        else R.string.newsdetail_removed_from_favorites
                    )
                )
            }

            NewsDetailEvent.ShareRequested -> viewModelScope.launch {
                val article = _uiState.value.article ?: return@launch
                _effects.send(NewsDetailEffect.ShareArticle(article.title, article.url))
            }

            NewsDetailEvent.SourceRequested -> viewModelScope.launch {
                val article = _uiState.value.article ?: return@launch
                _effects.send(NewsDetailEffect.OpenInBrowser(article.url))
            }

            NewsDetailEvent.Retry -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch { refreshArticle(articleId) }
    }
}
