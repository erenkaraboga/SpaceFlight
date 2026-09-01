package com.spaceflight.core.domain.usecase

import com.spaceflight.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDarkThemeOverrideUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<Boolean?> = repository.darkThemeOverride
}

class SetDarkThemeOverrideUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDarkThemeOverride(enabled)
}

class ObserveGridLayoutUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.isGridLayout
}

class SetGridLayoutUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setGridLayout(enabled)
}
