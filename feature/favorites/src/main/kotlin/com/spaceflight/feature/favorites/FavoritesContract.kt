package com.spaceflight.feature.favorites

import androidx.compose.runtime.Immutable
import com.spaceflight.core.domain.model.Article

@Immutable
data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<Article> = emptyList(),
    val selectedArticleId: Int? = null,
) {
    val selectedArticle: Article?
        get() = favorites.firstOrNull { it.id == selectedArticleId }
}

sealed interface FavoritesEvent {
    data class ArticleSelected(val articleId: Int) : FavoritesEvent
    data object DetailDismissed : FavoritesEvent
    data class FavoriteRemoved(val article: Article) : FavoritesEvent
    data object UndoRemoval : FavoritesEvent
    data class ShareRequested(val article: Article) : FavoritesEvent
    data class SourceRequested(val article: Article) : FavoritesEvent
}

sealed interface FavoritesEffect {
    data class OpenInBrowser(val url: String) : FavoritesEffect
    data class ShareArticle(val title: String, val url: String) : FavoritesEffect
    data class ShowUndoRemoval(val articleTitle: String) : FavoritesEffect
}
