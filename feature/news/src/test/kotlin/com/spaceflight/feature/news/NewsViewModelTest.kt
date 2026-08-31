package com.spaceflight.feature.news

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.spaceflight.core.domain.usecase.GetArticlesUseCase
import com.spaceflight.core.domain.usecase.ObserveArticleUseCase
import com.spaceflight.core.domain.usecase.ObserveFavoriteIdsUseCase
import com.spaceflight.core.domain.usecase.RefreshArticleUseCase
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val articleRepository = FakeArticleRepository(
        articles = listOf(testArticle(1, "Starship static fire"), testArticle(2)),
    )
    private val favoriteRepository = FakeFavoriteRepository()
    private val networkMonitor = FakeNetworkMonitor()
    private val savedStateHandle = SavedStateHandle()

    @Test
    fun `keystrokes collapse into a single search request`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.articles.collect { } }
        advanceUntilIdle()

        viewModel.onEvent(NewsEvent.SearchQueryChanged("s"))
        advanceTimeBy(100)
        viewModel.onEvent(NewsEvent.SearchQueryChanged("st"))
        advanceTimeBy(100)
        viewModel.onEvent(NewsEvent.SearchQueryChanged("starship"))
        advanceUntilIdle()

        assertEquals(listOf("", "starship"), articleRepository.requestedQueries)
    }

    @Test
    fun `closing the search restores the feed without waiting out the debounce`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.articles.collect { } }
        advanceUntilIdle()
        viewModel.onEvent(NewsEvent.SearchQueryChanged("starship"))
        advanceUntilIdle()

        viewModel.onEvent(NewsEvent.SearchActiveChanged(isActive = false))
        advanceTimeBy(10)

        assertEquals(listOf("", "starship", ""), articleRepository.requestedQueries)
    }

    @Test
    fun `favoriting an article reports it and updates the ids the list draws from`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(NewsEvent.FavoriteToggled(testArticle(1)))

            assertEquals(NewsEffect.ShowMessage(R.string.news_added_to_favorites), awaitItem())
        }
        advanceUntilIdle()

        assertEquals(setOf(1), viewModel.uiState.value.favoriteIds)
    }

    @Test
    fun `unfavoriting an already saved article reports the removal`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        favoriteRepository.addFavorite(testArticle(1))
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(NewsEvent.FavoriteToggled(testArticle(1)))

            assertEquals(NewsEffect.ShowMessage(R.string.news_removed_from_favorites), awaitItem())
        }
    }

    @Test
    fun `asking to read the source emits the publisher url`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onEvent(NewsEvent.SourceRequested(testArticle(1)))

            assertEquals(NewsEffect.OpenInBrowser("https://example.com/1"), awaitItem())
        }
    }

    @Test
    fun `sharing emits the title alongside the url`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onEvent(NewsEvent.ShareRequested(testArticle(1, "Starship static fire")))

            assertEquals(
                NewsEffect.ShareArticle("Starship static fire", "https://example.com/1"),
                awaitItem(),
            )
        }
    }

    @Test
    fun `selecting an article shows the cached copy and refreshes it behind the scenes`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()

        viewModel.onEvent(NewsEvent.ArticleSelected(1))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.selectedArticleId)
        assertEquals("Starship static fire", state.selectedArticle?.title)
        assertEquals(listOf(1), articleRepository.refreshedIds)
    }

    @Test
    fun `dismissing the detail clears both the id and the article`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        viewModel.onEvent(NewsEvent.ArticleSelected(1))
        advanceUntilIdle()

        viewModel.onEvent(NewsEvent.DetailDismissed)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedArticleId)
        assertNull(viewModel.uiState.value.selectedArticle)
    }

    @Test
    fun `the open article is restored after process death`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        createViewModel().onEvent(NewsEvent.ArticleSelected(1))
        advanceUntilIdle()

        val recreated = createViewModel()
        advanceUntilIdle()

        assertEquals(1, recreated.uiState.value.selectedArticleId)
        assertEquals("Starship static fire", recreated.uiState.value.selectedArticle?.title)
    }

    @Test
    fun `losing the connection flips the offline flag the banner reads`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        networkMonitor.setOnline(false)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOffline)
    }

    private fun createViewModel() = NewsViewModel(
        getArticles = GetArticlesUseCase(articleRepository),
        observeArticle = ObserveArticleUseCase(articleRepository),
        refreshArticle = RefreshArticleUseCase(articleRepository),
        toggleFavorite = ToggleFavoriteUseCase(favoriteRepository),
        observeFavoriteIds = ObserveFavoriteIdsUseCase(favoriteRepository),
        networkMonitor = networkMonitor,
        savedStateHandle = savedStateHandle,
    )
}
