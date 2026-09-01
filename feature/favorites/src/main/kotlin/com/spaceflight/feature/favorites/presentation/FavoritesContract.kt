package com.spaceflight.feature.favorites.presentation

import androidx.compose.runtime.Immutable
import com.spaceflight.core.domain.model.Article

@Immutable
data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<Article> = emptyList(),
)

sealed interface FavoritesEvent {
    data class FavoriteRemoved(val article: Article) : FavoritesEvent
}

sealed interface FavoritesEffect {
    data class ShowUndoRemoval(val articleTitle: String) : FavoritesEffect
}
