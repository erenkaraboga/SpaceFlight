package com.spaceflight.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.theme.LocalIsDarkTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

data class FloatingTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val BarShape = RoundedCornerShape(22.dp)
private val TabShape = RoundedCornerShape(17.dp)

/**
 * A floating glass bar of equally sized tabs. Colour is the only thing that animates, so the bar
 * does not reflow or fight the finger on every tap.
 *
 * Pass the same [HazeState] that is attached to the content behind the bar so the glass can blur
 * what scrolls underneath. When [hazeState] is null (previews), a solid frosted fill is used.
 */
@Composable
fun FloatingTabBar(
    tabs: List<FloatingTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    val isDark = LocalIsDarkTheme.current
    val colorSpec = spring<Color>(dampingRatio = 0.9f, stiffness = 380f)
    val glassBorder = if (isDark) {
        Color.White.copy(alpha = 0.20f)
    } else {
        Color.White.copy(alpha = 0.78f)
    }
    val fallbackFill = if (isDark) {
        Color(0xFF1C1C28).copy(alpha = 0.78f)
    } else {
        Color.White.copy(alpha = 0.82f)
    }

    Row(
        modifier = modifier
            .widthIn(max = 340.dp)
            .fillMaxWidth()
            .height(62.dp)
            .shadow(
                elevation = 18.dp,
                shape = BarShape,
                ambientColor = Color.Black.copy(alpha = 0.22f),
                spotColor = Color.Black.copy(alpha = 0.16f),
            )
            .clip(BarShape)
            .then(
                if (hazeState != null) {
                    Modifier.hazeEffect(state = hazeState) {
                        blurRadius = 28.dp
                        noiseFactor = 0.08f
                        tints = listOf(
                            HazeTint(
                                if (isDark) {
                                    Color.Black.copy(alpha = 0.34f)
                                } else {
                                    Color.White.copy(alpha = 0.52f)
                                },
                            ),
                            HazeTint(
                                if (isDark) {
                                    Color.White.copy(alpha = 0.08f)
                                } else {
                                    Color.White.copy(alpha = 0.22f)
                                },
                            ),
                        )
                    }
                } else {
                    Modifier.background(fallbackFill)
                },
            )
            .border(1.dp, glassBorder, BarShape)
            .selectableGroup()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            val container by animateColorAsState(
                targetValue = when {
                    !selected -> Color.Transparent
                    isDark -> Color.White.copy(alpha = 0.16f)
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                },
                animationSpec = colorSpec,
                label = "tabContainer",
            )
            val content by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = colorSpec,
                label = "tabContent",
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(TabShape)
                    .background(container)
                    .selectable(
                        selected = selected,
                        onClick = { onSelect(index) },
                        role = Role.Tab,
                    )
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.label,
                    tint = content,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
