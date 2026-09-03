package com.spaceflight.feature.news

import androidx.paging.PagingData
import com.spaceflight.core.domain.connectivity.NetworkMonitor
import com.spaceflight.core.domain.repository.ArticleRepository
import com.spaceflight.core.domain.repository.FavoriteRepository
import com.spaceflight.core.domain.repository.UserPreferencesRepository
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

class FakeFavoriteRepository(private var failure: AppError? = null) : FavoriteRepository {

    private val favorites = MutableStateFlow<Map<Int, Article>>(emptyMap())

    override fun observeFavorites(): Flow<List<Article>> = favorites.map { it.values.toList() }

    override fun observeFavoriteIds(): Flow<Set<Int>> = favorites.map { it.keys }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favorites.map { id in it }

    /** Makes the next mutating call (`removeFavorite`/`toggleFavorite`) fail once. */
    fun failNextWrite(error: AppError) {
        failure = error
    }

    override suspend fun removeFavorite(id: Int): Result<Unit> {
        failure?.let { return Result.failure(it.also { failure = null }) }
        favorites.value = favorites.value - id
        return Result.success(Unit)
    }

    override suspend fun toggleFavorite(article: Article): Result<Boolean> {
        failure?.let { return Result.failure(it.also { failure = null }) }
        return if (article.id in favorites.value) {
            removeFavorite(article.id)
            Result.success(false)
        } else {
            favorites.value = favorites.value + (article.id to article)
            Result.success(true)
        }
    }
}

class FakeNetworkMonitor(isOnline: Boolean = true) : NetworkMonitor {
    private val online = MutableStateFlow(isOnline)
    override val isOnline: Flow<Boolean> = online

    fun setOnline(value: Boolean) {
        online.value = value
    }
}

class FakeUserPreferencesRepository(
    initialGridLayout: Boolean = false,
) : UserPreferencesRepository {

    private val darkTheme = MutableStateFlow<Boolean?>(null)
    private val gridLayout = MutableStateFlow(initialGridLayout)

    override val darkThemeOverride: Flow<Boolean?> = darkTheme

    override suspend fun setDarkThemeOverride(enabled: Boolean) {
        darkTheme.value = enabled
    }

    override val isGridLayout: Flow<Boolean> = gridLayout

    override suspend fun setGridLayout(enabled: Boolean) {
        gridLayout.value = enabled
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
