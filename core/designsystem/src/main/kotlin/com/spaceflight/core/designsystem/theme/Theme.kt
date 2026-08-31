package com.spaceflight.core.designsystem.theme

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = SpaceColors.Nebula,
    onPrimary = SpaceColors.DeepSpace,
    primaryContainer = SpaceColors.NebulaDark,
    onPrimaryContainer = SpaceColors.Starlight,
    secondary = SpaceColors.Violet,
    onSecondary = SpaceColors.DeepSpace,
    secondaryContainer = SpaceColors.VioletDark,
    onSecondaryContainer = SpaceColors.Starlight,
    tertiary = SpaceColors.Amber,
    onTertiary = SpaceColors.DeepSpace,
    tertiaryContainer = SpaceColors.AmberDark,
    onTertiaryContainer = SpaceColors.Starlight,
    background = SpaceColors.DeepSpace,
    onBackground = SpaceColors.Starlight,
    surface = SpaceColors.Midnight,
    onSurface = SpaceColors.Starlight,
    surfaceVariant = SpaceColors.Orbit,
    onSurfaceVariant = SpaceColors.StarlightDim,
    surfaceContainer = SpaceColors.Orbit,
    surfaceContainerHigh = SpaceColors.OrbitHigh,
    outline = SpaceColors.Outline,
    outlineVariant = SpaceColors.Orbit,
    error = SpaceColors.Danger,
    onError = SpaceColors.DeepSpace,
)

private val LightColors = lightColorScheme(
    primary = SpaceColors.NebulaDark,
    onPrimary = SpaceColors.PaperElevated,
    primaryContainer = SpaceColors.Nebula,
    onPrimaryContainer = SpaceColors.Ink,
    secondary = SpaceColors.VioletDark,
    onSecondary = SpaceColors.PaperElevated,
    secondaryContainer = SpaceColors.Violet,
    onSecondaryContainer = SpaceColors.Ink,
    tertiary = SpaceColors.AmberDark,
    onTertiary = SpaceColors.PaperElevated,
    tertiaryContainer = SpaceColors.Amber,
    onTertiaryContainer = SpaceColors.Ink,
    background = SpaceColors.Paper,
    onBackground = SpaceColors.Ink,
    surface = SpaceColors.PaperElevated,
    onSurface = SpaceColors.Ink,
    surfaceVariant = SpaceColors.PaperVariant,
    onSurfaceVariant = SpaceColors.InkDim,
    surfaceContainer = SpaceColors.PaperVariant,
    surfaceContainerHigh = SpaceColors.PaperElevated,
    outline = SpaceColors.InkDim,
    outlineVariant = SpaceColors.PaperVariant,
    error = SpaceColors.DangerDark,
    onError = SpaceColors.PaperElevated,
)

@Composable
fun SpaceflightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val reducedMotion = remember(context) {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }

    CompositionLocalProvider(LocalReducedMotion provides reducedMotion) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = SpaceflightTypography,
            shapes = SpaceflightShapes,
            content = content,
        )
    }
}
