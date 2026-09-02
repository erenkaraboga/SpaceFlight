package com.spaceflight

import com.spaceflight.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * [ThemeViewModel] is the one thing `app` owns that isn't a Composable, so it's the one thing here
 * worth a JVM unit test -- it resolves the saved dark-theme override (or the absence of one) and
 * forwards writes back to [UserPreferencesRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeUserPreferencesRepository()

    @Test
    fun `with no saved override, the flag starts out null so the system setting wins`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = ThemeViewModel(repository)
        advanceUntilIdle()

        assertNull(viewModel.darkThemeOverride.value)
    }

    @Test
    fun `a saved override is loaded into the initial state`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        repository.setDarkThemeOverride(true)

        val viewModel = ThemeViewModel(repository)
        advanceUntilIdle()

        assertEquals(true, viewModel.darkThemeOverride.value)
    }

    @Test
    fun `setting the override persists it and the state reflects it`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        val viewModel = ThemeViewModel(repository)
        advanceUntilIdle()

        viewModel.setDarkThemeOverride(true)
        advanceUntilIdle()

        assertEquals(true, viewModel.darkThemeOverride.value)
        assertEquals(true, repository.darkTheme.value)
    }

    @Test
    fun `toggling back to false is stored, not just cleared`() = runTest(
        mainDispatcherRule.testDispatcher
    ) {
        repository.setDarkThemeOverride(true)
        val viewModel = ThemeViewModel(repository)
        advanceUntilIdle()

        viewModel.setDarkThemeOverride(false)
        advanceUntilIdle()

        assertEquals(false, viewModel.darkThemeOverride.value)
    }
}

/** `viewModelScope` runs on the main dispatcher, which does not exist in a JVM test. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

private class FakeUserPreferencesRepository : UserPreferencesRepository {

    val darkTheme = MutableStateFlow<Boolean?>(null)
    private val gridLayout = MutableStateFlow(false)

    override val darkThemeOverride: Flow<Boolean?> = darkTheme

    override suspend fun setDarkThemeOverride(enabled: Boolean) {
        darkTheme.value = enabled
    }

    override val isGridLayout: Flow<Boolean> = gridLayout

    override suspend fun setGridLayout(enabled: Boolean) {
        gridLayout.value = enabled
    }
}
