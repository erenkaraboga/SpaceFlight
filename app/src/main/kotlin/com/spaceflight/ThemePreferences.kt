package com.spaceflight

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberDarkTheme(): Pair<Boolean, () -> Unit> {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()
    val prefs = remember { themePreferences(context) }
    var override by remember {
        mutableStateOf(
            if (prefs.contains(DARK_THEME_KEY)) {
                prefs.getBoolean(DARK_THEME_KEY, systemDark)
            } else {
                null
            },
        )
    }
    val dark = override ?: systemDark
    return dark to {
        val next = !dark
        prefs.edit().putBoolean(DARK_THEME_KEY, next).apply()
        override = next
    }
}

private fun themePreferences(context: Context): SharedPreferences =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

private const val PREFS_NAME = "spaceflight_ui"
private const val DARK_THEME_KEY = "dark_theme"
