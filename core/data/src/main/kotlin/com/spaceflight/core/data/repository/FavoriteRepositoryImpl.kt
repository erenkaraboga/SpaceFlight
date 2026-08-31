package com.spaceflight.core.data.repository

import com.spaceflight.core.data.database.dao.FavoriteArticleDao
import com.spaceflight.core.data.mapper.toDomain
import com.spaceflight.core.data.mapper.toFavoriteEntity
import com.spaceflight.core.domain.model.Article
import com.spaceflight.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteArticleDao,
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<Article>> =
        favoriteDao.observeAll().map { favorites -> favorites.map { it.toDomain() } }

    override fun observeFavoriteIds(): Flow<Set<Int>> =
        favoriteDao.observeIds().map { it.toSet() }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favoriteDao.observeIsFavorite(id)

    override suspend fun addFavorite(article: Article) {
        favoriteDao.upsert(article.toFavoriteEntity(System.currentTimeMillis()))
    }

    override suspend fun removeFavorite(id: Int) {
        favoriteDao.deleteById(id)
    }

    override suspend fun toggleFavorite(article: Article): Boolean =
        if (favoriteDao.isFavorite(article.id)) {
            removeFavorite(article.id)
            false
        } else {
            addFavorite(article)
            true
        }
}
