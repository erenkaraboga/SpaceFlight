package com.spaceflight.feature.news

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.spaceflight.core.domain.connectivity.NetworkMonitor
import com.spaceflight.core.domain.model.Article
import com.spaceflight.core.domain.usecase.GetArticlesUseCase
import com.spaceflight.core.domain.usecase.ObserveArticleUseCase
import com.spaceflight.core.domain.usecase.ObserveFavoriteIdsUseCase
import com.spaceflight.core.domain.usecase.RefreshArticleUseCase
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
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
import kotlinx.coroutines.flow.flowOf
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
    private val observeArticle: ObserveArticleUseCase,
    private val refreshArticle: RefreshArticleUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    observeFavoriteIds: ObserveFavoriteIdsUseCase,
    networkMonitor: NetworkMonitor,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /**
     * Which article is open is the one piece of state worth surviving process death - everything
     * else is either cached in Room or cheap to derive.
     */
    private val _uiState = MutableStateFlow(
        NewsUiState(selectedArticleId = savedStateHandle[KEY_SELECTED_ARTICLE_ID])
    )
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val _effects = Channel<NewsEffect>(Channel.BUFFERED)
    val effects: Flow<NewsEffect> = _effects.receiveAsFlow()

    /**
     * Kept out of [uiState] on purpose: paging data is a stream of its own that the list collects
     * directly, and folding it into the state would re-emit the whole screen on every page.
     */
    val articles: Flow<PagingData<Article>> = _uiState
        .map { it.searchQuery.trim() }
        .distinctUntilChanged()
        // Typing should not fire a request per keystroke, but clearing the field should restore the
        // feed immediately.
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

        _uiState
            .map { it.selectedArticleId }
            .distinctUntilChanged()
            .flatMapLatest { id -> if (id == null) flowOf(null) else observeArticle(id) }
            .onEach { article -> _uiState.update { it.copy(selectedArticle = article) } }
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

            is NewsEvent.ArticleSelected -> selectArticle(event.articleId)

            NewsEvent.DetailDismissed -> {
                savedStateHandle[KEY_SELECTED_ARTICLE_ID] = null
                _uiState.update { it.copy(selectedArticleId = null, selectedArticle = null) }
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

            is NewsEvent.ShareRequested -> viewModelScope.launch {
                _effects.send(NewsEffect.ShareArticle(event.article.title, event.article.url))
            }

            is NewsEvent.SourceRequested -> viewModelScope.launch {
                _effects.send(NewsEffect.OpenInBrowser(event.article.url))
            }
        }
    }

    /**
     * The cached copy is shown right away and refreshed behind it, so opening an article is instant
     * even offline and still picks up publisher edits when there is a connection.
     */
    private fun selectArticle(articleId: Int) {
        savedStateHandle[KEY_SELECTED_ARTICLE_ID] = articleId
        _uiState.update { it.copy(selectedArticleId = articleId, isDetailRefreshing = true) }
        viewModelScope.launch {
            refreshArticle(articleId)
            _uiState.update { it.copy(isDetailRefreshing = false) }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 300L
        const val KEY_SELECTED_ARTICLE_ID = "selectedArticleId"
    }
}
