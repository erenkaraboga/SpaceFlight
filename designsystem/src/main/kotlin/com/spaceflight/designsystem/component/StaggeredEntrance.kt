package com.spaceflight.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.theme.LocalReducedMotion
import com.spaceflight.designsystem.theme.SpaceflightMotion
import kotlinx.coroutines.delay

/**
 * Remembers which items have already made their entrance. Lazy lists dispose off-screen items, so
 * without this an item would fade in again every time the user scrolled back to it. The set is
 * saveable so returning from another screen does not replay the cascade either.
 */
@Stable
class StaggeredEntranceState internal constructor(
    initial: Collection<String> = emptyList(),
) {

    private val entered = initial.toMutableSet()

    internal fun shouldAnimate(key: Any): Boolean = entered.add(key.toString())

    internal fun snapshot(): List<String> = entered.toList()
}

@Composable
fun rememberStaggeredEntranceState(): StaggeredEntranceState =
    rememberSaveable(saver = StaggeredEntranceSaver) { StaggeredEntranceState() }

private val StaggeredEntranceSaver = listSaver<StaggeredEntranceState, String>(
    save = { it.snapshot() },
    restore = { StaggeredEntranceState(it) },
)

/**
 * Fades and lifts a list item into place, offset by its position so a freshly loaded page — or a
 * newly scrolled-in row — cascades instead of snapping in as a block.
 */
@Composable
fun Modifier.staggeredEntrance(
    index: Int,
    state: StaggeredEntranceState,
    key: Any = index,
    play: Boolean = true,
): Modifier {
    val reducedMotion = LocalReducedMotion.current
    val animateIn = remember(key, play) {
        play && !reducedMotion && state.shouldAnimate(key)
    }
    val progress = remember(key) { Animatable(if (animateIn) 0f else 1f) }

    LaunchedEffect(key, animateIn) {
        if (!animateIn) {
            progress.snapTo(1f)
            return@LaunchedEffect
        }
        val step = index.coerceIn(0, SpaceflightMotion.MaxStaggeredItems)
        delay(step * SpaceflightMotion.StaggerStepMillis.toLong())
        progress.animateTo(1f, SpaceflightMotion.emphasized())
    }

    return if (progress.value == 1f) {
        this
    } else {
        this.graphicsLayer {
            alpha = progress.value
            translationY = (1f - progress.value) * 18.dp.toPx()
        }
    }
}
