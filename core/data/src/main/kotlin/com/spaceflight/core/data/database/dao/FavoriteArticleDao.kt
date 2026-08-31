package com.spaceflight.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spaceflight.core.data.database.entity.FavoriteArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteArticleDao {

    @Query("SELECT * FROM favorite_articles ORDER BY favoritedAt DESC")
    fun observeAll(): Flow<List<FavoriteArticleEntity>>

    @Query("SELECT id FROM favorite_articles")
    fun observeIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_articles WHERE id = :id)")
    fun observeIsFavorite(id: Int): Flow<Boolean>

    @Query("SELECT * FROM favorite_articles WHERE id = :id")
    fun observeById(id: Int): Flow<FavoriteArticleEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_articles WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("SELECT * FROM favorite_articles WHERE id = :id")
    suspend fun getById(id: Int): FavoriteArticleEntity?

    @Upsert
    suspend fun upsert(favorite: FavoriteArticleEntity)

    @Query("DELETE FROM favorite_articles WHERE id = :id")
    suspend fun deleteById(id: Int)
}
