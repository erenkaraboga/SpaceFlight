package com.spaceflight.designsystem.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect

/**
 * Every animation in the app pulls its timing from here, so the whole motion language can be
 * retuned - or switched off - from a single place.
 */
object SpaceflightMotion {

    const val StaggerStepMillis = 45
    const val MaxStaggeredItems = 8
    const val FadeThroughEnterMillis = 320
    const val FadeThroughExitMillis = 110
    const val SharedEnterMillis = 360

    fun <T> emphasized(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.9f,
        stiffness = 380f,
    )

    fun <T> bouncy(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    fun <T> fadeThrough(): FiniteAnimationSpec<T> = tween(durationMillis = FadeThroughEnterMillis)

    /** Shared-element morph: a finite tween so clip/shape actually finishes instead of springing. */
    fun sharedBounds(): FiniteAnimationSpec<Rect> = tween(
        durationMillis = 280,
        easing = FastOutSlowInEasing,
    )
}

/**
 * True when the system animation scale is off. Components read this instead of querying settings
 * themselves, and previews can flip it freely.
 */
val LocalReducedMotion = staticCompositionLocalOf { false }

/** Lets tokens that are not part of the Material colour scheme vary by theme. */
val LocalIsDarkTheme = staticCompositionLocalOf { true }

/** Optional override from the host; headers show a sun/moon control when this is set. */
val LocalToggleTheme = staticCompositionLocalOf<(() -> Unit)?> { null }

/** Collapses [spec] to an instant jump when the user has asked for reduced motion. */
@Composable
@ReadOnlyComposable
fun <T> respectingMotionPreference(spec: FiniteAnimationSpec<T>): FiniteAnimationSpec<T> =
    if (LocalReducedMotion.current) snap() else spec
