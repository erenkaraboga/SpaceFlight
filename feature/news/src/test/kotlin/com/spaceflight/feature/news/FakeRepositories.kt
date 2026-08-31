package com.spaceflight.feature.news

import androidx.paging.PagingData
import com.spaceflight.core.domain.connectivity.NetworkMonitor
import com.spaceflight.core.domain.model.Article
import com.spaceflight.core.domain.repository.ArticleRepository
import com.spaceflight.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Instant

class FakeArticleRepository(
    private val articles: List<Article> = emptyList(),
) : ArticleRepository {

    val requestedQueries = mutableListOf<String>()
    val refreshedIds = mutableListOf<Int>()

    override fun getArticles(query: String): Flow<PagingData<Article>> {
        requestedQueries += query
        return flowOf(PagingData.from(articles))
    }

    override fun observeArticle(id: Int): Flow<Article?> =
        flowOf(articles.firstOrNull { it.id == id })

    override suspend fun refreshArticle(id: Int): Result<Unit> {
        refreshedIds += id
        return Result.success(Unit)
    }
}

class FakeFavoriteRepository : FavoriteRepository {

    private val favorites = MutableStateFlow<Map<Int, Article>>(emptyMap())

    override fun observeFavorites(): Flow<List<Article>> = favorites.map { it.values.toList() }

    override fun observeFavoriteIds(): Flow<Set<Int>> = favorites.map { it.keys }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favorites.map { id in it }

    override suspend fun addFavorite(article: Article) {
        favorites.value = favorites.value + (article.id to article)
    }

    override suspend fun removeFavorite(id: Int) {
        favorites.value = favorites.value - id
    }

    override suspend fun toggleFavorite(article: Article): Boolean =
        if (article.id in favorites.value) {
            removeFavorite(article.id)
            false
        } else {
            addFavorite(article)
            true
        }
}

class FakeNetworkMonitor(isOnline: Boolean = true) : NetworkMonitor {
    private val online = MutableStateFlow(isOnline)
    override val isOnline: Flow<Boolean> = online

    fun setOnline(value: Boolean) {
        online.value = value
    }
}

fun testArticle(id: Int, title: String = "Article $id") = Article(
    id = id,
    title = title,
    summary = "Summary $id",
    imageUrl = "https://example.com/$id.jpg",
    newsSite = "Test Site",
    url = "https://example.com/$id",
    authors = listOf("Ada Lovelace"),
    publishedAt = Instant.ofEpochMilli(id.toLong()),
    updatedAt = Instant.ofEpochMilli(id.toLong()),
    isFeatured = false,
    launchCount = 0,
    eventCount = 0,
)
