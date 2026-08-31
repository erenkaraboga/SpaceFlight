@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.spaceflight.core.designsystem.motion

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Shared-element plumbing.
 *
 * The two scopes Compose needs live far apart - the transition scope wraps a whole screen while the
 * visibility scope belongs to an individual pane - so they travel through composition locals. Both
 * default to `null`, which makes every helper below degrade into a plain modifier when a component
 * is used outside a shared-transition tree (previews, tests, or a screen that opts out).
 */
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

val LocalPaneAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

fun articleImageKey(articleId: Int): String = "article-image-$articleId"

fun articleTitleKey(articleId: Int): String = "article-title-$articleId"

/** Flies an image from its list card into the detail hero. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedArticleImage(articleId: Int): Modifier {
    val transitionScope = LocalSharedTransitionScope.current
    val visibilityScope = LocalPaneAnimatedVisibilityScope.current
    if (transitionScope == null || visibilityScope == null) return this

    return with(transitionScope) {
        this@sharedArticleImage.sharedElement(
            sharedContentState = rememberSharedContentState(articleImageKey(articleId)),
            animatedVisibilityScope = visibilityScope,
        )
    }
}

/**
 * Titles use `sharedBounds` rather than `sharedElement`: the text reflows between the card and the
 * hero, so its bounds are what should be interpolated, not a snapshot of the pixels.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedArticleTitle(articleId: Int): Modifier {
    val transitionScope = LocalSharedTransitionScope.current
    val visibilityScope = LocalPaneAnimatedVisibilityScope.current
    if (transitionScope == null || visibilityScope == null) return this

    return with(transitionScope) {
        this@sharedArticleTitle.sharedBounds(
            sharedContentState = rememberSharedContentState(articleTitleKey(articleId)),
            animatedVisibilityScope = visibilityScope,
            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
        )
    }
}
