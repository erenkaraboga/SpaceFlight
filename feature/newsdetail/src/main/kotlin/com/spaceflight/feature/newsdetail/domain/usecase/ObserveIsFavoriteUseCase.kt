package com.spaceflight.feature.newsdetail.domain.usecase

import com.spaceflight.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(id: Int): Flow<Boolean> = repository.observeIsFavorite(id)
}
