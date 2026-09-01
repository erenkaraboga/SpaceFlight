package com.spaceflight.feature.news

import app.cash.turbine.test
import com.spaceflight.core.domain.usecase.GetArticlesUseCase
import com.spaceflight.core.domain.usecase.ObserveFavoriteIdsUseCase
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
import com.spaceflight.designsystem.text.UiText
import com.spaceflight.feature.news.logic.NewsEffect
import com.spaceflight.feature.news.logic.NewsEvent
import com.spaceflight.core.domain.model.AppError
import com.spaceflight.core.ui.error.toUiText
import com.spaceflight.feature.news.logic.NewsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

    @Test
    fun `keystrokes collapse into a single search request`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.articles.collect { } }
        advanceUntilIdle()
        articleRepository.requestedQueries.clear()

        viewModel.onEvent(NewsEvent.SearchQueryChanged("s"))
        advanceTimeBy(100)
        viewModel.onEvent(NewsEvent.SearchQueryChanged("st"))
        advanceTimeBy(100)
        viewModel.onEvent(NewsEvent.SearchQueryChanged("starship"))
        advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals(listOf("starship"), articleRepository.requestedQueries)
    }

    @Test
    fun `closing the search restores the feed without waiting out the debounce`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.articles.collect { } }
        advanceUntilIdle()
        viewModel.onEvent(NewsEvent.SearchQueryChanged("starship"))
        advanceTimeBy(300)
        advanceUntilIdle()
        articleRepository.requestedQueries.clear()

        viewModel.onEvent(NewsEvent.SearchActiveChanged(isActive = false))
        advanceUntilIdle()

        assertEquals(listOf(""), articleRepository.requestedQueries)
    }

    @Test
    fun `favoriting an article reports it and updates the ids the list draws from`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(NewsEvent.FavoriteToggled(testArticle(1)))

            assertEquals(NewsEffect.ShowMessage(UiText.Resource(R.string.news_added_to_favorites)), awaitItem())
        }
        advanceUntilIdle()

        assertEquals(setOf(1), viewModel.uiState.value.favoriteIds)
    }

    @Test
    fun `unfavoriting an already favorited article reports the removal`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        favoriteRepository.addFavorite(testArticle(1))
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(NewsEvent.FavoriteToggled(testArticle(1)))

            assertEquals(NewsEffect.ShowMessage(UiText.Resource(R.string.news_removed_from_favorites)), awaitItem())
        }
    }

    @Test
    fun `a failed favorite toggle surfaces the same mapped message every screen would show`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        favoriteRepository.failNextWrite(AppError.NoConnection())
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(NewsEvent.FavoriteToggled(testArticle(1)))

            assertEquals(NewsEffect.ShowMessage(AppError.NoConnection().toUiText()), awaitItem())
        }
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
        toggleFavorite = ToggleFavoriteUseCase(favoriteRepository),
        observeFavoriteIds = ObserveFavoriteIdsUseCase(favoriteRepository),
        networkMonitor = networkMonitor,
    )
}
