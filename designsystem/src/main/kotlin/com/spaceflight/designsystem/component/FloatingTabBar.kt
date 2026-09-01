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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.theme.LocalIsDarkTheme
import com.spaceflight.designsystem.theme.SpaceColors
import com.spaceflight.designsystem.theme.SpaceflightTheme
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
 * Sizing shared between [FloatingTabBar] and any screen content that needs to clear it -- most
 * notably a Scaffold's own [androidx.compose.material3.SnackbarHost], which floats inside the
 * screen's own content while the tab bar is drawn as a separate overlay on top of it. Without
 * this clearance a snackbar surfaces right at the bottom of the screen and ends up hidden behind
 * the bar instead of appearing above it.
 */
object FloatingTabBarDefaults {
    val Height = 62.dp
    val BottomSpacing = 12.dp

    /**
     * Total vertical space the bar occupies above the screen's bottom edge, excluding the system
     * navigation bar inset -- add [androidx.compose.foundation.layout.navigationBarsPadding]
     * separately, the same way [FloatingTabBar] itself does.
     */
    val ClearanceHeight = Height + BottomSpacing
}

/**
 * A floating glass bar of equally sized tabs. Colour is the only thing that animates, so the bar
 * does not reflow or fight the finger on every tap.
 *
 * Pass the same [HazeState] that is attached to the content behind the bar so the glass can blur
 * what scrolls underneath. When [hazeState] is null (previews), a solid frosted fill is used.
 *
 * @param tabs The list of [FloatingTab] items to be rendered in the bar.
 * @param selectedIndex The index of the currently active tab.
 * @param onSelect Callback invoked with the tab index when a user selects a tab.
 * @param modifier The [Modifier] to be applied to the tab bar container.
 * @param hazeState Optional [HazeState] used to apply a frosted glass blur effect over background content.
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
        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.78f)
    } else {
        Color.White.copy(alpha = 0.82f)
    }

    Row(
        modifier = modifier
            .widthIn(max = 340.dp)
            .fillMaxWidth()
            .height(FloatingTabBarDefaults.Height)
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

@Preview(name = "Floating Tab Bar", showBackground = true)
@Composable
private fun FloatingTabBarPreview() {
    val sampleTabs = remember {
        listOf(
            FloatingTab(
                label = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
            ),
            FloatingTab(
                label = "Search",
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search,
            ),
            FloatingTab(
                label = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
            ),
        )
    }
    var selectedIndex by remember { mutableIntStateOf(0) }

    SpaceflightTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            FloatingTabBar(
                tabs = sampleTabs,
                selectedIndex = selectedIndex,
                onSelect = { selectedIndex = it },
            )
        }
    }
}