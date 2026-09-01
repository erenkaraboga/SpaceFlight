package com.spaceflight.feature.newsdetail.domain.usecase

import com.spaceflight.core.model.Article
import com.spaceflight.core.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveArticleUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    operator fun invoke(id: Int): Flow<Article?> = repository.observeArticle(id)
}
