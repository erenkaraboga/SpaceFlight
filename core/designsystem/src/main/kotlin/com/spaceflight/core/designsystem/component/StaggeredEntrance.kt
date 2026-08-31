package com.spaceflight.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.spaceflight.core.designsystem.theme.LocalReducedMotion
import com.spaceflight.core.designsystem.theme.SpaceflightMotion
import kotlinx.coroutines.delay

/**
 * Remembers which items have already made their entrance. Lazy lists dispose off-screen items, so
 * without this an item would fade in again every time the user scrolled back to it.
 */
@Stable
class StaggeredEntranceState internal constructor() {

    private val entered = mutableSetOf<Any>()

    internal fun shouldAnimate(key: Any): Boolean = entered.add(key)
}

@Composable
fun rememberStaggeredEntranceState(): StaggeredEntranceState = remember { StaggeredEntranceState() }

/**
 * Fades and lifts a list item into place, offset by its position so a freshly loaded page cascades
 * instead of snapping in as a block. The delay is capped so later pages do not feel sluggish.
 */
@Composable
fun Modifier.staggeredEntrance(
    index: Int,
    state: StaggeredEntranceState,
    key: Any = index,
): Modifier {
    val reducedMotion = LocalReducedMotion.current
    val animateIn = remember(key) { !reducedMotion && state.shouldAnimate(key) }
    val progress = remember(key) { Animatable(if (animateIn) 0f else 1f) }

    LaunchedEffect(key) {
        if (!animateIn) return@LaunchedEffect
        val step = index.coerceIn(0, SpaceflightMotion.MaxStaggeredItems)
        delay(step * SpaceflightMotion.StaggerStepMillis.toLong())
        progress.animateTo(1f, SpaceflightMotion.emphasized())
    }

    // Once settled the layer is a no-op, so it is dropped rather than left compositing every frame.
    return if (progress.value == 1f) {
        this
    } else {
        this.graphicsLayer {
            alpha = progress.value
            translationY = (1f - progress.value) * 32.dp.toPx()
        }
    }
}
