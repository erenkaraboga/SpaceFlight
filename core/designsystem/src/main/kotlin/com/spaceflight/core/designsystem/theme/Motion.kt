package com.spaceflight.core.designsystem.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Every animation in the app pulls its timing from here, so the whole motion language can be
 * retuned - or switched off - from a single place.
 */
object SpaceflightMotion {

    const val StaggerStepMillis = 40
    const val MaxStaggeredItems = 8

    fun <T> emphasized(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.85f,
        stiffness = Spring.StiffnessMediumLow,
    )

    fun <T> bouncy(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    fun <T> standard(): FiniteAnimationSpec<T> = tween(durationMillis = 320)

    fun <T> fadeThrough(): FiniteAnimationSpec<T> = tween(durationMillis = 220)
}

/**
 * True when the system animation scale is off. Components read this instead of querying settings
 * themselves, and previews can flip it freely.
 */
val LocalReducedMotion = staticCompositionLocalOf { false }

/** Collapses [spec] to an instant jump when the user has asked for reduced motion. */
@Composable
@ReadOnlyComposable
fun <T> respectingMotionPreference(spec: FiniteAnimationSpec<T>): FiniteAnimationSpec<T> =
    if (LocalReducedMotion.current) snap() else spec
