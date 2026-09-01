package com.spaceflight.core.domain.usecase

import com.spaceflight.core.domain.model.Article
import com.spaceflight.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(): Flow<List<Article>> = repository.observeFavorites()
}

class ObserveFavoriteIdsUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(): Flow<Set<Int>> = repository.observeFavoriteIds()
}

class ObserveIsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(id: Int): Flow<Boolean> = repository.observeIsFavorite(id)
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    suspend operator fun invoke(article: Article): Result<Boolean> = repository.toggleFavorite(article)
}

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.removeFavorite(id)
}
