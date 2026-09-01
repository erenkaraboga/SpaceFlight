package com.spaceflight.feature.newsdetail.domain.usecase

import com.spaceflight.core.domain.repository.ArticleRepository
import javax.inject.Inject

class RefreshArticleUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.refreshArticle(id)
}
