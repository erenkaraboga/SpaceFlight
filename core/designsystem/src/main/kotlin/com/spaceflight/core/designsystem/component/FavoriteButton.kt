package com.spaceflight.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import com.spaceflight.core.designsystem.R
import com.spaceflight.core.designsystem.theme.LocalReducedMotion
import com.spaceflight.core.designsystem.theme.SpaceflightMotion

/**
 * The heart gives a springy "punch" when it becomes a favourite so the action registers without a
 * toast; removing it just settles back with no bounce.
 */
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = LocalReducedMotion.current
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isFavorite) {
        if (isFavorite && !reducedMotion) {
            scale.animateTo(1.35f, spring(stiffness = Spring.StiffnessHigh))
            scale.animateTo(1f, SpaceflightMotion.bouncy())
        } else {
            scale.snapTo(1f)
        }
    }

    val tint by animateColorAsState(
        targetValue = if (isFavorite) {
            MaterialTheme.colorScheme.tertiary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "favoriteTint",
    )

    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = stringResource(
                if (isFavorite) R.string.ds_remove_from_favorites else R.string.ds_add_to_favorites
            ),
            tint = tint,
            modifier = Modifier.scale(scale.value),
        )
    }
}
