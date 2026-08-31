package com.spaceflight.feature.news

import androidx.compose.runtime.Immutable
import com.spaceflight.core.domain.model.Article

@Immutable
data class NewsUiState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isOffline: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
    val selectedArticleId: Int? = null,
    val selectedArticle: Article? = null,
    val isDetailRefreshing: Boolean = false,
)

/** The single entry point into [NewsViewModel]; the UI never calls anything else. */
sealed interface NewsEvent {
    data class SearchQueryChanged(val query: String) : NewsEvent
    data class SearchActiveChanged(val isActive: Boolean) : NewsEvent
    data class ArticleSelected(val articleId: Int) : NewsEvent
    data object DetailDismissed : NewsEvent
    data class FavoriteToggled(val article: Article) : NewsEvent
    data class ShareRequested(val article: Article) : NewsEvent
    data class SourceRequested(val article: Article) : NewsEvent
}

/** One-shot outcomes that need an Activity or a Snackbar, so they cannot live in the state. */
sealed interface NewsEffect {
    data class OpenInBrowser(val url: String) : NewsEffect
    data class ShareArticle(val title: String, val url: String) : NewsEffect
    data class ShowMessage(val messageResId: Int) : NewsEffect
}
