package com.spaceflight.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.spaceflight.core.database.dao.ArticleDao
import com.spaceflight.core.data.mapper.toDomain
import com.spaceflight.core.data.mapper.toEntity
import com.spaceflight.core.network.SpaceflightApi
import com.spaceflight.core.common.error.toAppError
import com.spaceflight.core.model.Article
import kotlinx.coroutines.CancellationException

/**
 * Search hits the API, then writes each page into Room so opening a result can read the same
 * article the list already showed. The home feed is still a dated window; a later refresh replaces
 * it with the latest snapshot.
 */
class SearchArticlePagingSource(
    private val api: SpaceflightApi,
    private val articleDao: ArticleDao,
    private val query: String,
    private val pageSize: Int,
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? =
        state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(pageSize) ?: page?.nextKey?.minus(pageSize)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val offset = params.key ?: 0
        return try {
            val response = api.getArticles(
                limit = params.loadSize,
                offset = offset,
                search = query,
            )
            val entities = response.results.map { it.toEntity() }
            if (entities.isNotEmpty()) articleDao.upsertAll(entities)
            LoadResult.Page(
                data = entities.map { it.toDomain() },
                prevKey = if (offset == 0) null else (offset - pageSize).coerceAtLeast(0),
                nextKey = if (response.next == null || response.results.isEmpty()) {
                    null
                } else {
                    offset + response.results.size
                },
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            LoadResult.Error(error.toAppError())
        }
    }
}
