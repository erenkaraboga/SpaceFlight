package com.spaceflight.feature.news.presentation

import androidx.compose.runtime.Immutable
import com.spaceflight.core.domain.model.Article

@Immutable
data class NewsUiState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isOffline: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
)

sealed interface NewsEvent {
    data class SearchQueryChanged(val query: String) : NewsEvent
    data class SearchActiveChanged(val isActive: Boolean) : NewsEvent
    data class FavoriteToggled(val article: Article) : NewsEvent
}

sealed interface NewsEffect {
    data class ShowMessage(val messageResId: Int) : NewsEffect
}
