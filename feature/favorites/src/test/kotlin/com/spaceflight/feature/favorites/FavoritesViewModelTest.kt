package com.spaceflight.feature.favorites

import app.cash.turbine.test
import com.spaceflight.core.domain.model.AppError
import com.spaceflight.core.domain.usecase.ObserveFavoritesUseCase
import com.spaceflight.core.domain.usecase.RemoveFavoriteUseCase
import com.spaceflight.core.ui.error.toUiText
import com.spaceflight.feature.favorites.logic.FavoritesEffect
import com.spaceflight.feature.favorites.logic.FavoritesEvent
import com.spaceflight.feature.favorites.logic.FavoritesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeFavoriteRepository(
        initial = listOf(testArticle(1, "Starship static fire"), testArticle(2)),
    )

    @Test
    fun `favorite articles arrive and the loading placeholder goes away`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(listOf(1, 2), state.favorites.map { it.id })
    }

    @Test
    fun `removing an article drops it from the list`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.FavoriteRemoved(testArticle(1)))
        advanceUntilIdle()

        assertEquals(listOf(2), viewModel.uiState.value.favorites.map { it.id })
    }

    @Test
    fun `a failed removal surfaces the same mapped message every screen would show`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.failNextWrite(AppError.NoConnection())

        viewModel.effects.test {
            viewModel.onEvent(FavoritesEvent.FavoriteRemoved(testArticle(1)))

            assertEquals(FavoritesEffect.ShowMessage(AppError.NoConnection().toUiText()), awaitItem())
        }
        // The removal itself never went through, so the article is still there.
        assertEquals(listOf(1, 2), viewModel.uiState.value.favorites.map { it.id })
    }

    @Test
    fun `a failure loading favorites is shown as a full-screen error, not silently swallowed`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        repository.failNextObserve(AppError.NoConnection())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(AppError.NoConnection().toUiText(), state.error)
    }

    @Test
    fun `retrying after a load failure re-subscribes and recovers`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        repository.failNextObserve(AppError.NoConnection())
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.Retry)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertEquals(listOf(1, 2), state.favorites.map { it.id })
    }

    private fun createViewModel() = FavoritesViewModel(
        observeFavorites = ObserveFavoritesUseCase(repository),
        removeFavorite = RemoveFavoriteUseCase(repository),
    )
}
