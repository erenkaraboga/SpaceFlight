package com.spaceflight.core.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.spaceflight.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backed by a single `SharedPreferences` file for every device-local UI preference. Both dark
 * theme and feed layout used to live in their own screen-local copy of this same pattern; they now
 * share one implementation instead of coincidentally reinventing it twice.
 */
@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : UserPreferencesRepository {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override val darkThemeOverride: Flow<Boolean?> = observe { current ->
        if (current.contains(KEY_DARK_THEME_OVERRIDE)) {
            current.getBoolean(KEY_DARK_THEME_OVERRIDE, false)
        } else {
            null
        }
    }

    override suspend fun setDarkThemeOverride(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_THEME_OVERRIDE, enabled) }
    }

    override val isGridLayout: Flow<Boolean> = observe { current ->
        current.getBoolean(KEY_GRID_LAYOUT, false)
    }

    override suspend fun setGridLayout(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_GRID_LAYOUT, enabled) }
    }

    /**
     * Emits the current value of [read] immediately, then again on every change to this
     * preferences file (any key, not just the one [read] cares about — harmless, since
     * [distinctUntilChanged] drops the re-emissions [read] doesn't actually change).
     */
    private fun <T> observe(read: (SharedPreferences) -> T): Flow<T> = callbackFlow {
        trySend(read(prefs))
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { current, _ ->
            trySend(read(current))
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.conflate().distinctUntilChanged()

    private companion object {
        const val PREFS_NAME = "spaceflight_user_preferences"
        const val KEY_DARK_THEME_OVERRIDE = "dark_theme_override"
        const val KEY_GRID_LAYOUT = "feed_grid_layout"
    }
}
