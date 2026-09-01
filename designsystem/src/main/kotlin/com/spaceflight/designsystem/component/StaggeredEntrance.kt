package com.spaceflight.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.theme.LocalReducedMotion
import com.spaceflight.designsystem.theme.SpaceflightMotion
import com.spaceflight.designsystem.theme.SpaceflightTheme
import kotlinx.coroutines.delay

/**
 * Remembers which items have already made their entrance. Lazy lists dispose off-screen items, so
 * without this an item would fade in again every time the user scrolled back to it. The set is
 * saveable so returning from another screen does not replay the cascade either.
 *
 * @param initial Initial collection of keys that have already played their entrance animation.
 */
@Stable
class StaggeredEntranceState internal constructor(
    initial: Collection<String> = emptyList(),
) {

    private val entered = initial.toMutableSet()

    internal fun shouldAnimate(key: Any): Boolean = entered.add(key.toString())

    internal fun snapshot(): List<String> = entered.toList()
}

/**
 * Creates and remembers a [StaggeredEntranceState] that survives configuration changes and process recreation.
 */
@Composable
fun rememberStaggeredEntranceState(): StaggeredEntranceState =
    rememberSaveable(saver = StaggeredEntranceSaver) { StaggeredEntranceState() }

private val StaggeredEntranceSaver = listSaver(
    save = { it.snapshot() },
    restore = { StaggeredEntranceState(it) },
)

/**
 * Fades and lifts a list item into place, offset by its position so a freshly loaded page — or a
 * newly scrolled-in row — cascades instead of snapping in as a block.
 *
 * @param index The item position in the list, used to calculate the animation delay step.
 * @param state The [StaggeredEntranceState] tracker ensuring each item only animates once.
 * @param key Unique identifier for the item. Defaults to [index].
 * @param play Controls whether the entrance animation should trigger. Defaults to `true`.
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

@Preview(name = "Staggered Entrance Animation", showBackground = true)
@Composable
private fun StaggeredEntrancePreview() {
    var resetKey by remember { mutableIntStateOf(0) }

    SpaceflightTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Staggered Cascade Test",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Button(onClick = { resetKey++ }) {
                        Text("Replay Animation")
                    }
                }

                val entranceState = remember(resetKey) { StaggeredEntranceState() }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    List(5) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .staggeredEntrance(
                                    index = index,
                                    state = entranceState,
                                    key = "item_${resetKey}_$index",
                                )
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = "Cascading Item #${index + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}