package com.spaceflight.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.spaceflight.core.database.SpaceflightDatabase
import com.spaceflight.core.database.dao.ArticleDao
import com.spaceflight.core.database.dao.FavoriteArticleDao
import com.spaceflight.core.data.mapper.toDomain
import com.spaceflight.core.data.mapper.toEntity
import com.spaceflight.core.network.SpaceflightApi
import com.spaceflight.core.data.paging.ArticleRemoteMediator
import com.spaceflight.core.data.paging.SearchArticlePagingSource
import com.spaceflight.core.common.error.safeCall
import com.spaceflight.core.domain.connectivity.NetworkMonitor
import com.spaceflight.core.domain.repository.ArticleRepository
import com.spaceflight.core.model.Article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@Singleton
class ArticleRepositoryImpl @Inject constructor(
    private val api: SpaceflightApi,
    private val database: SpaceflightDatabase,
    private val articleDao: ArticleDao,
    private val favoriteDao: FavoriteArticleDao,
    private val networkMonitor: NetworkMonitor,
) : ArticleRepository {

    override fun getArticles(query: String): Flow<PagingData<Article>> =
        if (query.isBlank()) latestArticles() else searchArticles(query)

    /** The feed is offline-first: Room is the single source of truth, the network only fills it. */
    private fun latestArticles(): Flow<PagingData<Article>> = Pager(
        config = pagingConfig(),
        remoteMediator = ArticleRemoteMediator(api, database, PAGE_SIZE),
        pagingSourceFactory = { articleDao.pagingSource() },
    ).flow.map { paging -> paging.map { it.toDomain() } }

    /**
     * Searching hits the API while there is a connection and degrades to a local `LIKE` over the
     * cached feed when there is none, so the search field keeps working offline.
     */
    private fun searchArticles(query: String): Flow<PagingData<Article>> =
        networkMonitor.isOnline
            .distinctUntilChanged()
            .flatMapLatest { isOnline ->
                if (isOnline) {
                    Pager(
                        config = pagingConfig(),
                        pagingSourceFactory = {
                            SearchArticlePagingSource(api, articleDao, query, PAGE_SIZE)
                        },
                    ).flow
                } else {
                    Pager(
                        config = pagingConfig(),
                        pagingSourceFactory = { articleDao.searchPagingSource(query) },
                    ).flow.map { paging -> paging.map { it.toDomain() } }
                }
            }

    override fun observeArticle(id: Int): Flow<Article?> =
        combine(
            articleDao.observeById(id),
            favoriteDao.observeById(id),
        ) { cached, favorite ->
            cached?.toDomain() ?: favorite?.toDomain()
        }.distinctUntilChanged()

    override suspend fun refreshArticle(id: Int): Result<Unit> = safeCall {
        val entity = api.getArticle(id).toEntity()
        articleDao.upsertAll(listOf(entity))
        favoriteDao.getById(id)?.let { existing ->
            favoriteDao.upsert(
                existing.copy(
                    title = entity.title,
                    summary = entity.summary,
                    imageUrl = entity.imageUrl,
                    newsSite = entity.newsSite,
                    url = entity.url,
                    authors = entity.authors,
                    publishedAt = entity.publishedAt,
                    isFeatured = entity.isFeatured,
                    launchCount = entity.launchCount,
                    eventCount = entity.eventCount,
                )
            )
        }
    }

    private fun pagingConfig() = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE * 2,
        prefetchDistance = PAGE_SIZE,
        enablePlaceholders = true,
        maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,
    )

    companion object {
        const val PAGE_SIZE = 20
    }
}
