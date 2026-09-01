package com.spaceflight.feature.news.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.spaceflight.core.domain.connectivity.NetworkMonitor
import com.spaceflight.core.domain.model.Article
import com.spaceflight.core.domain.usecase.GetArticlesUseCase
import com.spaceflight.core.domain.usecase.ObserveFavoriteIdsUseCase
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
import com.spaceflight.feature.news.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    getArticles: GetArticlesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    observeFavoriteIds: ObserveFavoriteIdsUseCase,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val _effects = Channel<NewsEffect>(Channel.BUFFERED)
    val effects: Flow<NewsEffect> = _effects.receiveAsFlow()

    val articles: Flow<PagingData<Article>> = _uiState
        .map { it.searchQuery.trim() }
        .distinctUntilChanged()
        .debounce { query -> if (query.isEmpty()) 0L else SEARCH_DEBOUNCE_MILLIS }
        .flatMapLatest { query -> getArticles(query) }
        .cachedIn(viewModelScope)

    init {
        observeFavoriteIds()
            .onEach { ids -> _uiState.update { it.copy(favoriteIds = ids) } }
            .launchIn(viewModelScope)

        networkMonitor.isOnline
            .onEach { isOnline -> _uiState.update { it.copy(isOffline = !isOnline) } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: NewsEvent) {
        when (event) {
            is NewsEvent.SearchQueryChanged ->
                _uiState.update { it.copy(searchQuery = event.query) }

            is NewsEvent.SearchActiveChanged ->
                _uiState.update {
                    it.copy(
                        isSearchActive = event.isActive,
                        searchQuery = if (event.isActive) it.searchQuery else "",
                    )
                }

            is NewsEvent.FavoriteToggled -> viewModelScope.launch {
                val added = toggleFavorite(event.article)
                _effects.send(
                    NewsEffect.ShowMessage(
                        if (added) R.string.news_added_to_favorites
                        else R.string.news_removed_from_favorites
                    )
                )
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
