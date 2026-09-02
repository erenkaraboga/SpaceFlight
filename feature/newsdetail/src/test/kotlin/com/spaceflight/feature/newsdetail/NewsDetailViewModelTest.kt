package com.spaceflight.feature.newsdetail

import app.cash.turbine.test
import com.spaceflight.core.model.AppError
import com.spaceflight.feature.newsdetail.domain.usecase.ObserveArticleUseCase
import com.spaceflight.feature.newsdetail.domain.usecase.ObserveIsFavoriteUseCase
import com.spaceflight.feature.newsdetail.domain.usecase.RefreshArticleUseCase
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase
import com.spaceflight.core.common.error.toUiText
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailEffect
import com.spaceflight.feature.newsdetail.presentation.state.NewsDetailEvent
import com.spaceflight.feature.newsdetail.presentation.NewsDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

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
    fun `a missing article leaves the screen empty`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel(articleId = 404)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.article)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `a refresh failure with nothing cached sends a message and falls back to the empty state`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        articleRepository.failNextRefresh(AppError.NoConnection())
        val viewModel = createViewModel(articleId = 404)

        viewModel.effects.test {
            advanceUntilIdle()

            assertEquals(NewsDetailEffect.ShowMessage(AppError.NoConnection().toUiText()), awaitItem())
        }
        // Nothing cached and the refresh failed: isLoading clears so the screen falls through to
        // the "article not found" empty state instead of spinning forever.
        val state = viewModel.uiState.value
        assertNull(state.article)
        assertFalse(state.isLoading)
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
        articleId = articleId,
        observeArticle = ObserveArticleUseCase(articleRepository),
        observeIsFavorite = ObserveIsFavoriteUseCase(favoriteRepository),
        refreshArticle = RefreshArticleUseCase(articleRepository),
        toggleFavorite = ToggleFavoriteUseCase(favoriteRepository),
    )
}
