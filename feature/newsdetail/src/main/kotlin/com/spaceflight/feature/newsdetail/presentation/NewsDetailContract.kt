package com.spaceflight.feature.newsdetail.presentation

import androidx.compose.runtime.Immutable
import com.spaceflight.core.domain.model.Article

@Immutable
data class NewsDetailUiState(
    val article: Article? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
)

sealed interface NewsDetailEvent {
    data object BackClicked : NewsDetailEvent
    data object FavoriteToggled : NewsDetailEvent
    data object ShareRequested : NewsDetailEvent
    data object SourceRequested : NewsDetailEvent
    data object Retry : NewsDetailEvent
}

sealed interface NewsDetailEffect {
    data object NavigateBack : NewsDetailEffect
    data class OpenInBrowser(val url: String) : NewsDetailEffect
    data class ShareArticle(val title: String, val url: String) : NewsDetailEffect
    data class ShowMessage(val messageResId: Int) : NewsDetailEffect
}
