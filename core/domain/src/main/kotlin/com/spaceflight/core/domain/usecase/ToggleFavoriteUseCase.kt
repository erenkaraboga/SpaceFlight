package com.spaceflight.core.domain.usecase

import com.spaceflight.core.domain.repository.FavoriteRepository
import com.spaceflight.core.model.Article
import javax.inject.Inject

/**
 * The one use case shared by more than one feature (`feature:news` and `feature:newsdetail` both
 * let the user toggle a favorite), so it lives here as a genuinely "global" use case rather than
 * being duplicated per feature the way a feature-exclusive use case would be.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    suspend operator fun invoke(article: Article): Result<Boolean> = repository.toggleFavorite(article)
}
