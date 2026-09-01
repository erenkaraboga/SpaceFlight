package com.spaceflight.feature.newsdetail

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import app.cash.turbine.test
import com.spaceflight.core.domain.model.Article
import com.spaceflight.core.domain.repository.ArticleRepository
import com.spaceflight.core.domain.repository.FavoriteRepository
import com.spaceflight.core.domain.usecase.ObserveArticleUseCase
import com.spaceflight.core.domain.usecase.ObserveIsFavoriteUseCase
import com.spaceflight.core.domain.usecase.RefreshArticleUseCase
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
import com.spaceflight.feature.newsdetail.logic.NewsDetailEffect
import com.spaceflight.feature.newsdetail.logic.NewsDetailEvent
import com.spaceflight.feature.newsdetail.logic.NewsDetailViewModel
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

    override fun getArticles(query: String): Flow<PagingData<Article>> =
        flowOf(PagingData.from(articles))

    override fun observeArticle(id: Int): Flow<Article?> =
        flowOf(articles.firstOrNull { it.id == id })

    override suspend fun refreshArticle(id: Int): Result<Unit> {
        refreshedIds += id
        return Result.success(Unit)
    }
}

private class FakeFavoriteRepository : FavoriteRepository {

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

private fun testArticle(id: Int, title: String = "Article $id") = Article(
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
