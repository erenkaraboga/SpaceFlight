package com.spaceflight.feature.news.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberGridLayout(): Pair<Boolean, () -> Unit> {
    val context = LocalContext.current
    val prefs = remember {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    var isGrid by remember { mutableStateOf(prefs.getBoolean(GRID_KEY, false)) }
    return isGrid to {
        val next = !isGrid
        prefs.edit().putBoolean(GRID_KEY, next).apply()
        isGrid = next
    }
}

private const val PREFS_NAME = "spaceflight_ui"
private const val GRID_KEY = "feed_grid"
