package com.spaceflight.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Small, device-local UI preferences. Before this existed, dark theme (in `app`) and feed layout
 * (in `feature:news`) were each backed by their own ad hoc `SharedPreferences` read inside a
 * composable — two independent copies of the same pattern that even happened to pick the same
 * preferences file name. Both now go through this one repository instead, so they follow the same
 * repository/use-case/ViewModel path as everything else the app persists.
 */
interface UserPreferencesRepository {

    /** `null` means there is no saved override yet — the caller should fall back to the system setting. */
    val darkThemeOverride: Flow<Boolean?>

    suspend fun setDarkThemeOverride(enabled: Boolean)

    val isGridLayout: Flow<Boolean>

    suspend fun setGridLayout(enabled: Boolean)
}
