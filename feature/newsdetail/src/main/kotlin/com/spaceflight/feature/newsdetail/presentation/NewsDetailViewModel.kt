package com.spaceflight.feature.newsdetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.spaceflight.core.common.error.toAppErrorOrUnknown
import com.spaceflight.core.common.error.toUiText
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
import com.spaceflight.feature.newsdetail.domain.usecase.ObserveArticleUseCase
import com.spaceflight.feature.newsdetail.domain.usecase.ObserveIsFavoriteUseCase
import com.spaceflight.feature.newsdetail.domain.usecase.RefreshArticleUseCase
import com.spaceflight.feature.newsdetail.navigation.NewsDetailRoute
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailEffect
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailEvent
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
class NewsDetailViewModel(
    private val articleId: Int,
    observeArticle: ObserveArticleUseCase,
    observeIsFavorite: ObserveIsFavoriteUseCase,
    private val refreshArticle: RefreshArticleUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    @Inject constructor(
        savedStateHandle: SavedStateHandle,
        observeArticle: ObserveArticleUseCase,
        observeIsFavorite: ObserveIsFavoriteUseCase,
        refreshArticle: RefreshArticleUseCase,
        toggleFavorite: ToggleFavoriteUseCase,
    ) : this(
        articleId = savedStateHandle.toRoute<NewsDetailRoute>().articleId,
        observeArticle = observeArticle,
        observeIsFavorite = observeIsFavorite,
        refreshArticle = refreshArticle,
        toggleFavorite = toggleFavorite,
    )

    private val _uiState = MutableStateFlow(NewsDetailUiState())
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

    private val _effects = Channel<NewsDetailEffect>(Channel.CONFLATED)
    val effects: Flow<NewsDetailEffect> = _effects.receiveAsFlow()

    private var favoriteToggleJob: Job? = null

    init {
        observeArticle(articleId)
            .onEach { article ->
                if (article != null) {
                    _uiState.update { it.copy(article = article, isLoading = false) }
                }
            }
            .launchIn(viewModelScope)

        observeIsFavorite(articleId)
            .onEach { isFavorite -> _uiState.update { it.copy(isFavorite = isFavorite) } }
            .launchIn(viewModelScope)

        refreshArticle()
    }

    fun onEvent(event: NewsDetailEvent) {
        when (event) {
            NewsDetailEvent.BackClicked -> viewModelScope.launch {
                _effects.send(NewsDetailEffect.NavigateBack)
            }
            NewsDetailEvent.FavoriteToggled -> {
                favoriteToggleJob?.cancel()
                favoriteToggleJob = viewModelScope.launch {
                    val article = _uiState.value.article ?: return@launch
                    toggleFavorite(article).onFailure { error ->
                        _effects.send(
                            NewsDetailEffect.ShowMessage(
                                error.toAppErrorOrUnknown().toUiText()
                            )
                        )
                    }
                }
            }

            NewsDetailEvent.ShareRequested -> viewModelScope.launch {
                val article = _uiState.value.article ?: return@launch
                _effects.send(NewsDetailEffect.ShareArticle(article.title, article.url))
            }

            NewsDetailEvent.SourceRequested -> viewModelScope.launch {
                val article = _uiState.value.article ?: return@launch
                _effects.send(NewsDetailEffect.OpenInBrowser(article.url))
            }
        }
    }

    private fun refreshArticle() {
        viewModelScope.launch {
            refreshArticle(articleId).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false) } },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.send(
                        NewsDetailEffect.ShowMessage(
                            error.toAppErrorOrUnknown().toUiText()
                        )
                    )
                },
            )
        }
    }
}