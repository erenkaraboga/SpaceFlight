package com.spaceflight.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.ui.graphics.vector.ImageVector
import com.spaceflight.R
import com.spaceflight.feature.favorites.navigation.FavoritesRoute
import com.spaceflight.feature.news.navigation.NewsRoute

enum class TopLevelDestination(
    val route: Any,
    @param:StringRes val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    News(
        route = NewsRoute,
        labelResId = R.string.destination_news,
        selectedIcon = Icons.Rounded.Newspaper,
        unselectedIcon = Icons.Outlined.Newspaper,
    ),
    Favorites(
        route = FavoritesRoute,
        labelResId = R.string.destination_favorites,
        selectedIcon = Icons.Rounded.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
    ),
}
