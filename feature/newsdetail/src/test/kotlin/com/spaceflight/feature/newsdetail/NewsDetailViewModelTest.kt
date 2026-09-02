package com.spaceflight.feature.newsdetail

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import app.cash.turbine.test
import com.spaceflight.core.model.AppError
import com.spaceflight.core.model.Article
import com.spaceflight.core.domain.repository.ArticleRepository
import com.spaceflight.core.domain.repository.FavoriteRepository
import com.spaceflight.feature.newsdetail.domain.usecase.ObserveArticleUseCase
import com.spaceflight.feature.newsdetail.domain.usecase.ObserveIsFavoriteUseCase
import com.spaceflight.feature.newsdetail.domain.usecase.RefreshArticleUseCase
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
import com.spaceflight.core.common.error.toUiText
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailEffect
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailEvent
import com.spaceflight.feature.newsdetail.presentation.NewsDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class NewsDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val articleRepository = FakeArticleRepository(
        articles = listOf(testArticle(1, "Starship static fire")),
    )
    private val favoriteRepository = FakeFavoriteRepository()

    @Test
    fun `the cached article is shown and refreshed behind the scenes`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel(articleId = 1)
        advanceUntilIdle()

        assertEquals("Starship static fire", viewModel.uiState.value.article?.title)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
        assertEquals(listOf(1), articleRepository.refreshedIds)
    }

    @Test
    fun `a missing article leaves the screen empty so the error state can take over`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel(articleId = 404)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.article)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `a refresh failure with nothing cached is shown as a full-screen error`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        articleRepository.failNextRefresh(AppError.NoConnection())

        val viewModel = createViewModel(articleId = 404)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.article)
        assertEquals(AppError.NoConnection().toUiText(), state.error)
    }

    @Test
    fun `retrying after a failed refresh clears the error once it succeeds`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        articleRepository.failNextRefresh(AppError.NoConnection())
        val viewModel = createViewModel(articleId = 404)
        advanceUntilIdle()

        viewModel.onEvent(NewsDetailEvent.Retry)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `a refresh failure with a cached article keeps showing it and only sends a message`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        articleRepository.failNextRefresh(AppError.NoConnection())
        val viewModel = createViewModel(articleId = 1)

        viewModel.effects.test {
            advanceUntilIdle()

            assertEquals(NewsDetailEffect.ShowMessage(AppError.NoConnection().toUiText()), awaitItem())
        }
        assertEquals("Starship static fire", viewModel.uiState.value.article?.title)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `favoriting flips the heart without a message`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel(articleId = 1)
        advanceUntilIdle()

        viewModel.onEvent(NewsDetailEvent.FavoriteToggled)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `a failed favorite toggle surfaces the same mapped message every screen would show`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel(articleId = 1)
        advanceUntilIdle()
        favoriteRepository.failNextWrite(AppError.NoConnection())

        viewModel.effects.test {
            viewModel.onEvent(NewsDetailEvent.FavoriteToggled)

            assertEquals(NewsDetailEffect.ShowMessage(AppError.NoConnection().toUiText()), awaitItem())
        }
    }

    @Test
    fun `sharing emits the title alongside the url`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel(articleId = 1)
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(NewsDetailEvent.ShareRequested)

            assertEquals(
                NewsDetailEffect.ShareArticle("Starship static fire", "https://example.com/1"),
                awaitItem(),
            )
        }
    }

    @Test
    fun `asking to read the source emits the publisher url`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel(articleId = 1)
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(NewsDetailEvent.SourceRequested)

            assertEquals(NewsDetailEffect.OpenInBrowser("https://example.com/1"), awaitItem())
        }
    }

    @Test
    fun `back asks the host to pop`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel(articleId = 1)

        viewModel.effects.test {
            viewModel.onEvent(NewsDetailEvent.BackClicked)

            assertEquals(NewsDetailEffect.NavigateBack, awaitItem())
        }
    }

    private fun createViewModel(articleId: Int) = NewsDetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf("articleId" to articleId)),
        observeArticle = ObserveArticleUseCase(articleRepository),
        observeIsFavorite = ObserveIsFavoriteUseCase(favoriteRepository),
        refreshArticle = RefreshArticleUseCase(articleRepository),
        toggleFavorite = ToggleFavoriteUseCase(favoriteRepository),
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

private class FakeArticleRepository(
    private val articles: List<Article> = emptyList(),
) : ArticleRepository {

    val refreshedIds = mutableListOf<Int>()
    private var refreshFailure: AppError? = null

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

private class FakeFavoriteRepository : FavoriteRepository {

    private val favorites = MutableStateFlow<Map<Int, Article>>(emptyMap())
    private var writeFailure: AppError? = null

    fun failNextWrite(error: AppError) {
        writeFailure = error
    }

    override fun observeFavorites(): Flow<List<Article>> = favorites.map { it.values.toList() }

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

private fun testArticle(id: Int, title: String = "Article $id") = Article(
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
