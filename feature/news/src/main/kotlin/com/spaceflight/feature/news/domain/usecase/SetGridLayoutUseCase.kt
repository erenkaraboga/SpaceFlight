package com.spaceflight.feature.news.domain.usecase

import com.spaceflight.core.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetGridLayoutUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setGridLayout(enabled)
}
