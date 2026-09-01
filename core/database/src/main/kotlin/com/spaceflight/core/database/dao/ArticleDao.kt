package com.spaceflight.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spaceflight.core.database.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun pagingSource(): PagingSource<Int, ArticleEntity>

    /** Offline fallback for the search screen, over whatever the feed has already cached. */
    @Query(
        """
        SELECT * FROM articles
        WHERE title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%'
        ORDER BY publishedAt DESC
        """
    )
    fun searchPagingSource(query: String): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE id = :id")
    fun observeById(id: Int): Flow<ArticleEntity?>

    @Upsert
    suspend fun upsertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun count(): Int
}
