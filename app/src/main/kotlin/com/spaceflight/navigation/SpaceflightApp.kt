package com.spaceflight.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spaceflight.designsystem.component.FloatingTab
import com.spaceflight.designsystem.component.FloatingTabBar
import com.spaceflight.designsystem.motion.LocalNavAnimatedVisibilityScope
import com.spaceflight.designsystem.motion.LocalSharedTransitionScope
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.feature.favorites.ui.FavoritesScreen
import com.spaceflight.feature.news.ui.NewsScreen
import com.spaceflight.feature.newsdetail.navigation.NewsDetailRoute
import com.spaceflight.feature.newsdetail.ui.NewsDetailScreen
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SpaceflightApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val isDetail = currentDestination?.hasRoute<NewsDetailRoute>() == true

    val routeIndex = TopLevelDestination.entries.indexOfFirst { currentDestination.isOn(it) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    if (routeIndex >= 0) selectedTab = routeIndex

    val tabs = TopLevelDestination.entries.map { destination ->
        FloatingTab(
            label = stringResource(destination.labelResId),
            selectedIcon = destination.selectedIcon,
            unselectedIcon = destination.unselectedIcon,
        )
    }

    val barProgress by animateFloatAsState(
        targetValue = if (isDetail) 0f else 1f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 280f),
        label = "barVisibility",
    )
    val hazeState = rememberHazeState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        SharedTransitionLayout(
            Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState),
        ) {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = navController,
                    startDestination = NewsRoute,
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
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable<NewsRoute> {
                        SharedPane {
                            NewsScreen(
                                onArticleClick = { id ->
                                    navController.navigate(NewsDetailRoute(id))
                                },
                            )
                        }
                    }
                    composable<FavoritesRoute> {
                        SharedPane {
                            FavoritesScreen(
                                onArticleClick = { id ->
                                    navController.navigate(NewsDetailRoute(id))
                                },
                            )
                        }
                    }
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
                            fadeIn(
                                tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing,
                                ),
                            )
                        },
                        popExitTransition = {
                            fadeOut(
                                tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing,
                                ),
                            )
                        },
                    ) {
                        SharedPane {
                            NewsDetailScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }

        val destinations = remember { TopLevelDestination.entries }
        FloatingTabBar(
            tabs = tabs,
            selectedIndex = selectedTab,
            hazeState = hazeState,
            onSelect = { index ->
                val destination = destinations[index]
                navController.navigate(destination.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                .graphicsLayer {
                    alpha = barProgress
                    translationY = (1f - barProgress) * 120.dp.toPx()
                },
        )
    }
}

@Composable
private fun AnimatedVisibilityScope.SharedPane(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
        content()
    }
}

private fun NavDestination?.isOn(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { current ->
        when (destination) {
            TopLevelDestination.News -> current.hasRoute<NewsRoute>()
            TopLevelDestination.Favorites -> current.hasRoute<FavoritesRoute>()
        }
    } == true
