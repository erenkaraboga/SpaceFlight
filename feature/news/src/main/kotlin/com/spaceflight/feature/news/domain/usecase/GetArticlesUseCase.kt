package com.spaceflight.feature.news.domain.usecase

import androidx.paging.PagingData
import com.spaceflight.core.model.Article
import com.spaceflight.core.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    operator fun invoke(query: String): Flow<PagingData<Article>> = repository.getArticles(query)
}
