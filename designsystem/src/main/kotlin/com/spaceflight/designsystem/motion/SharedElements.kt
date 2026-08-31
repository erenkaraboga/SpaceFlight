@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.spaceflight.designsystem.motion

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.spaceflight.designsystem.theme.LocalReducedMotion
import com.spaceflight.designsystem.theme.SpaceflightMotion

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

val LocalNavAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

fun sharedImageKey(id: Any): String = "shared-image-$id"

@Composable
fun Modifier.sharedContent(
    key: String,
    clipShape: Shape = RectangleShape,
): Modifier {
    val shared = LocalSharedTransitionScope.current ?: return this
    val visibility = LocalNavAnimatedVisibilityScope.current ?: return this
    if (LocalReducedMotion.current) return this

    return with(shared) {
        sharedElement(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = visibility,
            boundsTransform = { _, _ -> SpaceflightMotion.sharedBounds() },
            clipInOverlayDuringTransition = OverlayClip(clipShape),
        )
    }
}
