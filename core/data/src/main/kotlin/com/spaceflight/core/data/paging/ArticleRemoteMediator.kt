package com.spaceflight.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.spaceflight.core.data.database.SpaceflightDatabase
import com.spaceflight.core.data.database.entity.ArticleEntity
import com.spaceflight.core.data.database.entity.RemoteKeyEntity
import com.spaceflight.core.data.mapper.toEntity
import com.spaceflight.core.data.network.SpaceflightApi
import com.spaceflight.core.data.network.toAppError
import kotlinx.coroutines.CancellationException

/**
 * Keeps the local feed cache in sync with `/v4/articles/`.
 *
 * A REFRESH wipes the cache inside the same transaction that refills it, so the list can never be a
 * mix of two different snapshots. APPEND continues from the offset stored in [RemoteKeyEntity], and
 * pagination ends when the API stops handing out a `next` link.
 */
@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val api: SpaceflightApi,
    private val database: SpaceflightDatabase,
    private val pageSize: Int,
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun initialize(): InitializeAction =
        InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>,
    ): MediatorResult {
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val key = database.remoteKeyDao().get()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                if (key.endReached) return MediatorResult.Success(endOfPaginationReached = true)
                key.nextOffset
            }
        }

        return try {
            val response = api.getArticles(limit = pageSize, offset = offset)
            val endReached = response.next == null || response.results.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.articleDao().clearAll()
                    database.remoteKeyDao().clear()
                }
                database.articleDao().upsertAll(response.results.map { it.toEntity() })
                database.remoteKeyDao().upsert(
                    RemoteKeyEntity(
                        nextOffset = offset + response.results.size,
                        endReached = endReached,
                        lastRefreshedAt = System.currentTimeMillis(),
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            MediatorResult.Error(error.toAppError())
        }
    }
}
