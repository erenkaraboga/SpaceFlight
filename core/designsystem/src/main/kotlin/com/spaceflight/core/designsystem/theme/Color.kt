package com.spaceflight.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * A fixed palette rather than dynamic colour: the app leans on a deep-space identity, and letting
 * the wallpaper repaint it would wash that away.
 */
internal object SpaceColors {
    val DeepSpace = Color(0xFF05070F)
    val Midnight = Color(0xFF0B1020)
    val Orbit = Color(0xFF151B2E)
    val OrbitHigh = Color(0xFF1D2540)
    val Outline = Color(0xFF2C3550)

    val Nebula = Color(0xFF89A5FF)
    val NebulaDark = Color(0xFF2C46B8)
    val Violet = Color(0xFFB98CFF)
    val VioletDark = Color(0xFF6134B5)
    val Amber = Color(0xFFFFB454)
    val AmberDark = Color(0xFF9A5A00)

    val Starlight = Color(0xFFE9EDF9)
    val StarlightDim = Color(0xFFA7B0C9)

    val Paper = Color(0xFFF6F7FC)
    val PaperElevated = Color(0xFFFFFFFF)
    val PaperVariant = Color(0xFFE7EAF4)
    val Ink = Color(0xFF0B1020)
    val InkDim = Color(0xFF4A5268)

    val Danger = Color(0xFFFF6B6B)
    val DangerDark = Color(0xFFB3261E)
}
