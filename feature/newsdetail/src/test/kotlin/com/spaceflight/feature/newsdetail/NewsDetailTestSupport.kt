package com.spaceflight.feature.newsdetail

import androidx.paging.PagingData
import com.spaceflight.core.domain.repository.ArticleRepository
import com.spaceflight.core.domain.repository.FavoriteRepository
import com.spaceflight.core.model.AppError
import com.spaceflight.core.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.Instant

/** `viewModelScope` runs on the main dispatcher, which does not exist in a JVM test. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}

class FakeArticleRepository(
    private val articles: List<Article> = emptyList(),
) : ArticleRepository {

    val refreshedIds = mutableListOf<Int>()
    private var refreshFailure: AppError? = null

    /** Makes the next `refreshArticle` call fail once. */
    fun failNextRefresh(error: AppError) {
        refreshFailure = error
    }

    override fun getArticles(query: String): Flow<PagingData<Article>> =
        flowOf(PagingData.from(articles))

    override fun observeArticle(id: Int): Flow<Article?> =
        flowOf(articles.firstOrNull { it.id == id })

    override suspend fun refreshArticle(id: Int): Result<Unit> {
        refreshedIds += id
        refreshFailure?.let { return Result.failure(it.also { refreshFailure = null }) }
        return Result.success(Unit)
    }
}

class FakeFavoriteRepository : FavoriteRepository {

    private val favorites = MutableStateFlow<Map<Int, Article>>(emptyMap())
    private var writeFailure: AppError? = null

    /** Makes the next mutating call (`removeFavorite`/`toggleFavorite`) fail once. */
    fun failNextWrite(error: AppError) {
        writeFailure = error
    }

    override fun observeFavorites(): Flow<List<Article>> = favorites.map { it.values.toList() }

    override fun observeFavoriteIds(): Flow<Set<Int>> = favorites.map { it.keys }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favorites.map { id in it }

    override suspend fun removeFavorite(id: Int): Result<Unit> {
        writeFailure?.let { return Result.failure(it.also { writeFailure = null }) }
        favorites.value = favorites.value - id
        return Result.success(Unit)
    }

    override suspend fun toggleFavorite(article: Article): Result<Boolean> {
        writeFailure?.let { return Result.failure(it.also { writeFailure = null }) }
        return if (article.id in favorites.value) {
            removeFavorite(article.id)
            Result.success(false)
        } else {
            favorites.value = favorites.value + (article.id to article)
            Result.success(true)
        }
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
    isFeatured = false,
    launchCount = 0,
    eventCount = 0,
)
