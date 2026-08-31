package com.spaceflight.core.domain.usecase

import androidx.paging.PagingData
import com.spaceflight.core.domain.model.Article
import com.spaceflight.core.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    operator fun invoke(query: String): Flow<PagingData<Article>> = repository.getArticles(query)
}

class ObserveArticleUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    operator fun invoke(id: Int): Flow<Article?> = repository.observeArticle(id)
}

class RefreshArticleUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.refreshArticle(id)
}
