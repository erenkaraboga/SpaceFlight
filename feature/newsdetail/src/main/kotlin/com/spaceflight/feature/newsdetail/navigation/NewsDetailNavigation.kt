package com.spaceflight.feature.newsdetail.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.feature.newsdetail.presentation.NewsDetailScreen

fun NavGraphBuilder.newsDetailScreen(
    onBack: () -> Unit,
    wrapper: @Composable AnimatedVisibilityScope.(@Composable () -> Unit) -> Unit = { it() },
) {
    composable<NewsDetailRoute>(
        enterTransition = {
            fadeIn(
                tween(
                    durationMillis = SpaceflightMotion.SharedEnterMillis,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        exitTransition = {
            fadeOut(tween(220, easing = LinearOutSlowInEasing))
        },
        popEnterTransition = {
            fadeIn(tween(durationMillis = 300, easing = FastOutSlowInEasing))
        },
        popExitTransition = {
            fadeOut(tween(durationMillis = 300, easing = FastOutSlowInEasing))
        },
    ) {
        wrapper {
            NewsDetailScreen(onBack = onBack)
        }
    }
}
