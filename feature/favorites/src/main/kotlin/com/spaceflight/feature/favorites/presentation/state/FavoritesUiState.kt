package com.spaceflight.feature.favorites.presentation.state

import androidx.compose.runtime.Immutable
import com.spaceflight.core.model.Article
import com.spaceflight.core.common.text.UiText

@Immutable
data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<Article> = emptyList(),
)

sealed interface FavoritesEvent {
    data class FavoriteRemoved(val article: Article) : FavoritesEvent
}

sealed interface FavoritesEffect {
    data class ShowMessage(val text: UiText) : FavoritesEffect
}
