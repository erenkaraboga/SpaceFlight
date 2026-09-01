package com.spaceflight.feature.favorites

import app.cash.turbine.test
import com.spaceflight.core.domain.usecase.AddFavoriteUseCase
import com.spaceflight.core.domain.usecase.ObserveFavoritesUseCase
import com.spaceflight.core.domain.usecase.RemoveFavoriteUseCase
import com.spaceflight.feature.favorites.logic.FavoritesEffect
import com.spaceflight.feature.favorites.logic.FavoritesEvent
import com.spaceflight.feature.favorites.logic.FavoritesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
        assertEquals(listOf(1, 2), state.favorites.map { it.id })
    }

    @Test
    fun `removing an article drops it and offers an undo`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onEvent(FavoritesEvent.FavoriteRemoved(testArticle(1)))

            assertEquals(
                FavoritesEffect.ShowUndoRemoval("Article 1"),
                awaitItem(),
            )
        }
        advanceUntilIdle()

        assertEquals(listOf(2), viewModel.uiState.value.favorites.map { it.id })
    }

    @Test
    fun `undo restores the article that was just removed`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onEvent(FavoritesEvent.FavoriteRemoved(testArticle(1, "Starship static fire")))
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.UndoRemoval)
        advanceUntilIdle()

        val restored = viewModel.uiState.value.favorites.first { it.id == 1 }
        assertEquals("Starship static fire", restored.title)
    }

    @Test
    fun `undo without a prior removal does nothing`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.UndoRemoval)
        advanceUntilIdle()

        assertEquals(listOf(1, 2), viewModel.uiState.value.favorites.map { it.id })
    }

    private fun createViewModel() = FavoritesViewModel(
        observeFavorites = ObserveFavoritesUseCase(repository),
        addFavorite = AddFavoriteUseCase(repository),
        removeFavorite = RemoveFavoriteUseCase(repository),
    )
}
