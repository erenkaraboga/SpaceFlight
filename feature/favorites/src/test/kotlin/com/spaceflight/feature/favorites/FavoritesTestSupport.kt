package com.spaceflight.feature.favorites

import com.spaceflight.core.model.AppError
import com.spaceflight.core.model.Article
import com.spaceflight.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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

class FakeFavoriteRepository(initial: List<Article> = emptyList()) : FavoriteRepository {

    private val favorites = MutableStateFlow(initial.associateBy { it.id })
    private var observeFailure: AppError? = null
    private var writeFailure: AppError? = null

    /** Makes the next (re-)subscription to [observeFavorites] fail once, then behave normally. */
    fun failNextObserve(error: AppError) {
        observeFailure = error
    }

    /** Makes the next mutating call (`addFavorite`/`removeFavorite`/`toggleFavorite`) fail once. */
    fun failNextWrite(error: AppError) {
        writeFailure = error
    }

    override fun observeFavorites(): Flow<List<Article>> = flow {
        observeFailure?.let { throw it.also { observeFailure = null } }
        emitAll(favorites.map { it.values.toList() })
    }

    override fun observeFavoriteIds(): Flow<Set<Int>> = favorites.map { it.keys }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = favorites.map { id in it }

    override suspend fun addFavorite(article: Article): Result<Unit> {
        writeFailure?.let { return Result.failure(it.also { writeFailure = null }) }
        favorites.value = favorites.value + (article.id to article)
        return Result.success(Unit)
    }

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
            addFavorite(article)
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
    updatedAt = Instant.ofEpochMilli(id.toLong()),
    isFeatured = false,
    launchCount = 0,
    eventCount = 0,
)
