package com.spaceflight.core.data.fake

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.spaceflight.core.database.dao.ArticleDao
import com.spaceflight.core.database.dao.FavoriteArticleDao
import com.spaceflight.core.database.entity.ArticleEntity
import com.spaceflight.core.database.entity.FavoriteArticleEntity
import com.spaceflight.core.network.SpaceflightApi
import com.spaceflight.core.network.dto.ArticleDto
import com.spaceflight.core.network.dto.PaginatedArticlesDto
import com.spaceflight.core.domain.connectivity.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Hand-written fakes rather than mocks: the DAOs are stateful, and asserting on real state reads
 * far better than asserting on recorded calls.
 */
class FakeArticleDao(initial: List<ArticleEntity> = emptyList()) : ArticleDao {

    private val articles = MutableStateFlow(initial.associateBy { it.id })

    val stored: List<ArticleEntity> get() = articles.value.values.toList()

    override fun pagingSource(): PagingSource<Int, ArticleEntity> =
        SinglePagePagingSource(newestFirst())

    override fun searchPagingSource(query: String): PagingSource<Int, ArticleEntity> =
        SinglePagePagingSource(
            newestFirst().filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.summary.contains(query, ignoreCase = true)
            }
        )

    override fun observeById(id: Int): Flow<ArticleEntity?> = articles.map { it[id] }

    override suspend fun upsertAll(articles: List<ArticleEntity>) {
        this.articles.value = this.articles.value + articles.associateBy { it.id }
    }

    override suspend fun clearAll() {
        articles.value = emptyMap()
    }

    override suspend fun count(): Int = articles.value.size

    private fun newestFirst() = articles.value.values.sortedByDescending { it.publishedAt }
}

class FakeFavoriteArticleDao(
    initial: List<FavoriteArticleEntity> = emptyList(),
    private var failure: Throwable? = null,
) : FavoriteArticleDao {

    private val favorites = MutableStateFlow(initial.associateBy { it.id })

    /** Makes the next mutating call (`upsert`/`deleteById`) throw, to exercise the failure path. */
    fun failNextWrite(error: Throwable) {
        failure = error
    }

    override fun observeAll(): Flow<List<FavoriteArticleEntity>> =
        favorites.map { it.values.sortedByDescending(FavoriteArticleEntity::favoritedAt) }

    override fun observeIds(): Flow<List<Int>> = favorites.map { it.keys.toList() }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favorites.map { id in it }

    override fun observeById(id: Int): Flow<FavoriteArticleEntity?> = favorites.map { it[id] }

    override suspend fun isFavorite(id: Int): Boolean = id in favorites.value

    override suspend fun getById(id: Int): FavoriteArticleEntity? = favorites.value[id]

    override suspend fun upsert(favorite: FavoriteArticleEntity) {
        failure?.let { throw it.also { failure = null } }
        favorites.value = favorites.value + (favorite.id to favorite)
    }

    override suspend fun deleteById(id: Int) {
        failure?.let { throw it.also { failure = null } }
        favorites.value = favorites.value - id
    }
}

class FakeNetworkMonitor(isOnline: Boolean = true) : NetworkMonitor {
    private val online = MutableStateFlow(isOnline)
    override val isOnline: Flow<Boolean> = online

    fun setOnline(value: Boolean) {
        online.value = value
    }
}

class FakeSpaceflightApi(
    private var articles: List<ArticleDto> = emptyList(),
    private var failure: Throwable? = null,
) : SpaceflightApi {

    var lastSearch: String? = null
        private set

    override suspend fun getArticles(
        limit: Int,
        offset: Int,
        search: String?,
        ordering: String,
    ): PaginatedArticlesDto {
        failure?.let { throw it }
        lastSearch = search
        val page = articles.drop(offset).take(limit)
        return PaginatedArticlesDto(
            next = if (offset + page.size < articles.size) "next" else null,
            results = page,
        )
    }

    override suspend fun getArticle(id: Int): ArticleDto {
        failure?.let { throw it }
        return articles.firstOrNull { it.id == id } ?: error("No article $id")
    }
}

private class SinglePagePagingSource(
    private val items: List<ArticleEntity>,
) : PagingSource<Int, ArticleEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ArticleEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleEntity> =
        LoadResult.Page(data = items, prevKey = null, nextKey = null)
}
