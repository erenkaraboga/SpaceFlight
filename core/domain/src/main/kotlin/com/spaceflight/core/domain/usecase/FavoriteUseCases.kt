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
    suspend operator fun invoke(article: Article): Boolean = repository.toggleFavorite(article)
}

class AddFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    suspend operator fun invoke(article: Article) = repository.addFavorite(article)
}

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    suspend operator fun invoke(id: Int) = repository.removeFavorite(id)
}
