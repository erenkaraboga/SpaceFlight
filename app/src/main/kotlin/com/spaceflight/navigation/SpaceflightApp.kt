package com.spaceflight.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spaceflight.feature.favorites.FavoritesScreen
import com.spaceflight.feature.news.NewsScreen

/**
 * The navigation suite picks the right chrome for the window on its own: a bottom bar on a phone, a
 * rail on a tablet, so the two screens do not need to know which one they are living in.
 */
@Composable
fun SpaceflightApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                val selected = currentDestination.isOn(destination)
                item(
                    selected = selected,
                    onClick = {
                        navController.navigate(destination.route) {
                            // Tabs are peers: switching should not stack screens, and returning to
                            // a tab should restore where the reader left off.
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) {
                                destination.selectedIcon
                            } else {
                                destination.unselectedIcon
                            },
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(destination.labelResId)) },
                )
            }
        },
    ) {
        NavHost(
            navController = navController,
            startDestination = NewsRoute,
            // Material's fade-through: tabs are siblings, so they cross-dissolve instead of sliding
            // in a direction that would imply a hierarchy.
            enterTransition = {
                fadeIn(tween(FadeThroughEnterMillis, delayMillis = FadeThroughExitMillis)) +
                    scaleIn(
                        animationSpec = tween(
                            durationMillis = FadeThroughEnterMillis,
                            delayMillis = FadeThroughExitMillis,
                        ),
                        initialScale = 0.94f,
                    )
            },
            exitTransition = { fadeOut(tween(FadeThroughExitMillis)) },
            popEnterTransition = {
                fadeIn(tween(FadeThroughEnterMillis, delayMillis = FadeThroughExitMillis))
            },
            popExitTransition = { fadeOut(tween(FadeThroughExitMillis)) },
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<NewsRoute> { NewsScreen() }
            composable<FavoritesRoute> { FavoritesScreen() }
        }
    }
}

private fun NavDestination?.isOn(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { current ->
        when (destination) {
            TopLevelDestination.News -> current.hasRoute<NewsRoute>()
            TopLevelDestination.Favorites -> current.hasRoute<FavoritesRoute>()
        }
    } == true

private const val FadeThroughEnterMillis = 210
private const val FadeThroughExitMillis = 90
