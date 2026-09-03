package com.spaceflight.feature.newsdetail.presentation.state

import androidx.compose.runtime.Immutable
import com.spaceflight.core.model.Article
import com.spaceflight.core.common.text.UiText

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
}

sealed interface NewsDetailEffect {
    data object NavigateBack : NewsDetailEffect
    data class OpenInBrowser(val url: String) : NewsDetailEffect
    data class ShareArticle(val title: String, val url: String) : NewsDetailEffect
    data class ShowMessage(val text: UiText) : NewsDetailEffect
}
