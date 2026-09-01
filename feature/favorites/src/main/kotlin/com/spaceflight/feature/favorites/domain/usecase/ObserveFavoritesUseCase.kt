package com.spaceflight.feature.favorites.domain.usecase

import com.spaceflight.core.model.Article
import com.spaceflight.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(): Flow<List<Article>> = repository.observeFavorites()
}
