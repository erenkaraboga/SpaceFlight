package com.spaceflight.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.spaceflight.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Resolves the effective dark-theme flag (saved override, or the system setting when there is
 * none yet) and hands back a toggle. `app` has no domain layer of its own in this architecture --
 * unlike a feature module, it does not own use cases -- so [ThemeViewModel] talks to
 * [UserPreferencesRepository] directly instead of through one.
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
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val darkThemeOverride: StateFlow<Boolean?> = userPreferencesRepository.darkThemeOverride
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setDarkThemeOverride(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setDarkThemeOverride(enabled) }
    }
}
