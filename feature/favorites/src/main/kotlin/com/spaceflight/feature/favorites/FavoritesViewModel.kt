package com.spaceflight.feature.favorites

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spaceflight.core.domain.model.Article
import com.spaceflight.core.domain.usecase.AddFavoriteUseCase
import com.spaceflight.core.domain.usecase.ObserveFavoritesUseCase
import com.spaceflight.core.domain.usecase.RemoveFavoriteUseCase
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
class FavoritesViewModel @Inject constructor(
    observeFavorites: ObserveFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FavoritesUiState(selectedArticleId = savedStateHandle[KEY_SELECTED_ARTICLE_ID])
    )
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _effects = Channel<FavoritesEffect>(Channel.BUFFERED)
    val effects: Flow<FavoritesEffect> = _effects.receiveAsFlow()

    /**
     * Held so an undo can put back the exact snapshot that was removed, including the article's
     * cached body, which may no longer exist anywhere else once the feed cache rolls over.
     */
    private var lastRemoved: Article? = null

    init {
        observeFavorites()
            .onEach { favorites ->
                // Drop the selection if the article it pointed at is gone.
                val selectedId = _uiState.value.selectedArticleId
                    ?.takeIf { id -> favorites.any { it.id == id } }
                savedStateHandle[KEY_SELECTED_ARTICLE_ID] = selectedId
                _uiState.update {
                    it.copy(
                        favorites = favorites,
                        isLoading = false,
                        selectedArticleId = selectedId,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.ArticleSelected -> {
                savedStateHandle[KEY_SELECTED_ARTICLE_ID] = event.articleId
                _uiState.update { it.copy(selectedArticleId = event.articleId) }
            }

            FavoritesEvent.DetailDismissed -> {
                savedStateHandle[KEY_SELECTED_ARTICLE_ID] = null
                _uiState.update { it.copy(selectedArticleId = null) }
            }

            is FavoritesEvent.FavoriteRemoved -> viewModelScope.launch {
                lastRemoved = event.article
                removeFavorite(event.article.id)
                _effects.send(FavoritesEffect.ShowUndoRemoval(event.article.title))
            }

            FavoritesEvent.UndoRemoval -> viewModelScope.launch {
                lastRemoved?.let { addFavorite(it) }
                lastRemoved = null
            }

            is FavoritesEvent.ShareRequested -> viewModelScope.launch {
                _effects.send(
                    FavoritesEffect.ShareArticle(event.article.title, event.article.url)
                )
            }

            is FavoritesEvent.SourceRequested -> viewModelScope.launch {
                _effects.send(FavoritesEffect.OpenInBrowser(event.article.url))
            }
        }
    }

    private companion object {
        const val KEY_SELECTED_ARTICLE_ID = "selectedArticleId"
    }
}
