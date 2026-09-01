package com.spaceflight.feature.favorites.domain.usecase

import com.spaceflight.core.domain.repository.FavoriteRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.removeFavorite(id)
}
