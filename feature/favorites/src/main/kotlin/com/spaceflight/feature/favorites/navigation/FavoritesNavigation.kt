package com.spaceflight.feature.favorites.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.spaceflight.feature.favorites.presentation.FavoritesScreen
import kotlinx.serialization.Serializable

@Serializable
data object FavoritesRoute

fun NavGraphBuilder.favoritesScreen(
    onArticleClick: (Int) -> Unit,
    wrapper: @Composable AnimatedVisibilityScope.(@Composable () -> Unit) -> Unit = { it() },
) {
    composable<FavoritesRoute> {
        wrapper {
            FavoritesScreen(onArticleClick = onArticleClick)
        }
    }
}
