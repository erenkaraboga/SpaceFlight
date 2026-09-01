package com.spaceflight.core.domain.repository

import androidx.paging.PagingData
import com.spaceflight.core.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {

    /**
     * Articles backed by the local cache. A blank [query] streams the latest articles, otherwise the
     * query is sent to the API as a search and falls back to a local search while offline.
     */
    fun getArticles(query: String): Flow<PagingData<Article>>

    /** Emits the cached article, or `null` while it is not present locally. */
    fun observeArticle(id: Int): Flow<Article?>

    /** Fetches a single article from the network and updates the cache. */
    suspend fun refreshArticle(id: Int): Result<Unit>
}
