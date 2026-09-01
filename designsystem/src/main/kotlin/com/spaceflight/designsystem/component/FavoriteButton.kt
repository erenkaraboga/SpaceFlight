package com.spaceflight.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.R
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.designsystem.theme.SpaceflightTheme

/**
 * A heart that springs when it fills in so the action registers without a toast.
 *
 * @param isFavorite Controls whether the button is in a active (favorited) state.
 * @param onClick Callback to be invoked when the button is clicked.
 * @param modifier The [Modifier] to be applied to the button layout.
 * @param containerColor The background color of the circular container.
 * @param inactiveTint Icon color used when [isFavorite] is false.
 * @param size Total diameter of the touch target and background shape.
 */
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    inactiveTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    size: Dp = 40.dp,
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            scale.animateTo(1.35f, spring(stiffness = Spring.StiffnessHigh))
            scale.animateTo(1f, SpaceflightMotion.bouncy())
        } else {
            scale.snapTo(1f)
        }
    }

    val tint by animateColorAsState(
        targetValue = if (isFavorite) MaterialTheme.colorScheme.primary else inactiveTint,
        label = "favoriteTint",
    )
    val contentDescription = stringResource(
        if (isFavorite) R.string.ds_remove_from_favorites else R.string.ds_add_to_favorites
    )

    GlassIconButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        size = size,
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier
                .size(size * 0.5f)
                .scale(scale.value),
        )
    }
}

/**
 * Frosted heart for sitting on a thumbnail.
 *
 * @param isFavorite Controls whether the button is in an active (favorited) state.
 * @param onClick Callback to be invoked when the button is clicked.
 * @param modifier The [Modifier] to be applied to the button layout.
 */

@Composable
fun PhotoFavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FavoriteButton(
        isFavorite = isFavorite,
        onClick = onClick,
        modifier = modifier,
        containerColor = Color.Black.copy(alpha = 0.35f),
        inactiveTint = Color.White,
        size = 36.dp,
    )
}

@Preview(name = "Favorite Button States", showBackground = true)
@Composable
private fun FavoriteButtonPreview() {
    SpaceflightTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FavoriteButton(
                    isFavorite = true,
                    onClick = {}
                )

                FavoriteButton(
                    isFavorite = false,
                    onClick = {}
                )
            }
        }
    }
}