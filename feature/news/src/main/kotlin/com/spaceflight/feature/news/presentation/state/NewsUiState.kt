package com.spaceflight.feature.news.presentation.state

import androidx.compose.runtime.Immutable
import com.spaceflight.core.model.Article
import com.spaceflight.designsystem.text.UiText

@Immutable
data class NewsUiState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isOffline: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
    val isGridLayout: Boolean = false,
)

sealed interface NewsEvent {
    data class SearchQueryChanged(val query: String) : NewsEvent
    data class SearchActiveChanged(val isActive: Boolean) : NewsEvent
    data class FavoriteToggled(val article: Article) : NewsEvent
    data object LayoutToggled : NewsEvent
}

sealed interface NewsEffect {
    data class ShowMessage(val text: UiText) : NewsEffect
}
