package com.spaceflight.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spaceflight.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.spaceflight.feature.favorites.domain.usecase.RemoveFavoriteUseCase
import com.spaceflight.core.common.error.toAppErrorOrUnknown
import com.spaceflight.core.common.error.toUiText
import com.spaceflight.feature.favorites.presentation.state.FavoritesEffect
import com.spaceflight.feature.favorites.presentation.state.FavoritesEvent
import com.spaceflight.feature.favorites.presentation.state.FavoritesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavorites: ObserveFavoritesUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _effects = Channel<FavoritesEffect>(Channel.BUFFERED)
    val effects: Flow<FavoritesEffect> = _effects.receiveAsFlow()

    init {
        observeFavorites()
            .onEach { favorites ->
                _uiState.update {
                    it.copy(
                        favorites = favorites,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.FavoriteRemoved -> viewModelScope.launch {
                removeFavorite(event.article.id).onFailure { error ->
                    _effects.send(
                        FavoritesEffect.ShowMessage(
                            error.toAppErrorOrUnknown().toUiText()
                        )
                    )
                }
            }
        }
    }
}
