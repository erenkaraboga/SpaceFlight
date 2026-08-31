package com.spaceflight.core.domain.repository

import com.spaceflight.core.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    /** Favourites keep a full snapshot of the article, so they stay readable offline. */
    fun observeFavorites(): Flow<List<Article>>

    fun observeFavoriteIds(): Flow<Set<Int>>

    fun observeIsFavorite(id: Int): Flow<Boolean>

    suspend fun addFavorite(article: Article)

    suspend fun removeFavorite(id: Int)

    /** Returns `true` when the article ended up favourited. */
    suspend fun toggleFavorite(article: Article): Boolean
}
