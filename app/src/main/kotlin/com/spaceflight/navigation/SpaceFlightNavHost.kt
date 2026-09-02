package com.spaceflight.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.feature.favorites.navigation.favoritesScreen
import com.spaceflight.feature.news.navigation.NewsRoute
import com.spaceflight.feature.news.navigation.newsScreen
import com.spaceflight.feature.newsdetail.navigation.NewsDetailRoute
import com.spaceflight.feature.newsdetail.navigation.newsDetailScreen

@Composable
fun SpaceflightNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NewsRoute,
        modifier = modifier,
        enterTransition = {
            fadeIn(
                tween(
                    durationMillis = SpaceflightMotion.FadeThroughEnterMillis,
                    delayMillis = SpaceflightMotion.FadeThroughExitMillis,
                    easing = FastOutSlowInEasing,
                ),
            ) + scaleIn(
                animationSpec = tween(
                    durationMillis = SpaceflightMotion.FadeThroughEnterMillis,
                    delayMillis = SpaceflightMotion.FadeThroughExitMillis,
                    easing = FastOutSlowInEasing,
                ),
                initialScale = 0.97f,
            )
        },
        exitTransition = {
            fadeOut(
                tween(
                    durationMillis = SpaceflightMotion.FadeThroughExitMillis,
                    easing = LinearOutSlowInEasing,
                ),
            )
        },
        popEnterTransition = {
            fadeIn(
                tween(
                    durationMillis = SpaceflightMotion.FadeThroughEnterMillis,
                    delayMillis = SpaceflightMotion.FadeThroughExitMillis,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        popExitTransition = {
            fadeOut(
                tween(
                    durationMillis = SpaceflightMotion.FadeThroughExitMillis + 40,
                    easing = LinearOutSlowInEasing,
                ),
            )
        },
    ) {
        newsScreen(
            onArticleClick = { id -> navController.navigate(NewsDetailRoute(id)) },
            wrapper = { content -> SharedPane { content() } },
        )
        favoritesScreen(
            onArticleClick = { id -> navController.navigate(NewsDetailRoute(id)) },
            wrapper = { content -> SharedPane { content() } },
        )
        newsDetailScreen(
            onBack = { navController.popBackStack() },
            wrapper = { content -> SharedPane { content() } },
        )
    }
}
