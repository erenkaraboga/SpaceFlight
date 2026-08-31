package com.spaceflight.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.spaceflight.core.data.mapper.toDomain
import com.spaceflight.core.data.network.SpaceflightApi
import com.spaceflight.core.data.network.toAppError
import com.spaceflight.core.domain.model.Article
import kotlinx.coroutines.CancellationException

/**
 * Search results are served straight from the API instead of the cache: they are a transient view
 * of the archive and would otherwise pollute the offline feed snapshot.
 */
class SearchArticlePagingSource(
    private val api: SpaceflightApi,
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
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
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
