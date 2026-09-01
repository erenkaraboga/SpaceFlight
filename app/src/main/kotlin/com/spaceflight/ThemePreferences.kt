package com.spaceflight

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.spaceflight.core.domain.usecase.ObserveDarkThemeOverrideUseCase
import com.spaceflight.core.domain.usecase.SetDarkThemeOverrideUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Resolves the effective dark-theme flag (saved override, or the system setting when there is
 * none yet) and hands back a toggle. The saved override now lives behind [ThemeViewModel] /
 * [com.spaceflight.core.domain.repository.UserPreferencesRepository] instead of a raw
 * `SharedPreferences` read in this composable, so it goes through the same repository/use-case
 * path as everything else the app persists — and no longer risks colliding with feed layout's own
 * preference, which used to live in a separate copy of this exact pattern under the same file name.
 */
@Composable
fun rememberDarkTheme(viewModel: ThemeViewModel = hiltViewModel()): Pair<Boolean, () -> Unit> {
    val systemDark = isSystemInDarkTheme()
    val override by viewModel.darkThemeOverride.collectAsStateWithLifecycle()
    val dark = override ?: systemDark
    return dark to { viewModel.setDarkThemeOverride(!dark) }
}

@HiltViewModel
class ThemeViewModel @Inject constructor(
    observeDarkThemeOverride: ObserveDarkThemeOverrideUseCase,
    private val setDarkThemeOverrideUseCase: SetDarkThemeOverrideUseCase,
) : ViewModel() {

    val darkThemeOverride: StateFlow<Boolean?> = observeDarkThemeOverride()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setDarkThemeOverride(enabled: Boolean) {
        viewModelScope.launch { setDarkThemeOverrideUseCase(enabled) }
    }
}
