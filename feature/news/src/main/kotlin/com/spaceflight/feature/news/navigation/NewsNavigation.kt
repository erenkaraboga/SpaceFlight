package com.spaceflight.feature.news.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.spaceflight.feature.news.presentation.NewsScreen

fun NavGraphBuilder.newsScreen(
    onArticleClick: (Int) -> Unit,
    wrapper: @Composable AnimatedVisibilityScope.(@Composable () -> Unit) -> Unit = { it() },
) {
    composable<NewsRoute> {
        wrapper {
            NewsScreen(onArticleClick = onArticleClick)
        }
    }
}
