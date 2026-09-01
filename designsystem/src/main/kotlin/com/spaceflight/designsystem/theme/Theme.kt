package com.spaceflight.designsystem.theme

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = SpaceColors.Solar,
    onPrimary = SpaceColors.SolarInk,
    primaryContainer = SpaceColors.SolarDeep,
    onPrimaryContainer = SpaceColors.SolarPale,
    secondary = SpaceColors.Ion,
    onSecondary = SpaceColors.IonInk,
    secondaryContainer = SpaceColors.IonDeep,
    onSecondaryContainer = SpaceColors.IonPale,
    tertiary = SpaceColors.Ion,
    onTertiary = SpaceColors.IonInk,
    tertiaryContainer = SpaceColors.IonDeep,
    onTertiaryContainer = SpaceColors.IonPale,
    background = SpaceColors.Void,
    onBackground = SpaceColors.Bright,
    surface = SpaceColors.Void,
    onSurface = SpaceColors.Bright,
    surfaceVariant = SpaceColors.SlateHigh,
    onSurfaceVariant = SpaceColors.Muted,
    surfaceContainerLowest = SpaceColors.Void,
    surfaceContainerLow = SpaceColors.Abyss,
    surfaceContainer = SpaceColors.Slate,
    surfaceContainerHigh = SpaceColors.SlateHigh,
    surfaceContainerHighest = SpaceColors.SlateHighest,
    outline = SpaceColors.Muted,
    outlineVariant = SpaceColors.Divider,
    error = SpaceColors.Alert,
    onError = SpaceColors.AlertInk,
    errorContainer = SpaceColors.AlertDeep,
    onErrorContainer = SpaceColors.AlertPale,
    scrim = Color.Black,
)

private val LightColors = lightColorScheme(
    primary = SpaceColors.SolarStrong,
    onPrimary = Color.White,
    primaryContainer = SpaceColors.SolarPale,
    onPrimaryContainer = SpaceColors.SolarInk,
    secondary = SpaceColors.IonStrong,
    onSecondary = Color.White,
    secondaryContainer = SpaceColors.IonPale,
    onSecondaryContainer = SpaceColors.IonInk,
    tertiary = SpaceColors.IonStrong,
    onTertiary = Color.White,
    tertiaryContainer = SpaceColors.IonPale,
    onTertiaryContainer = SpaceColors.IonInk,
    background = SpaceColors.Daylight,
    onBackground = SpaceColors.Ink,
    surface = SpaceColors.Daylight,
    onSurface = SpaceColors.Ink,
    surfaceVariant = SpaceColors.CardRaised,
    onSurfaceVariant = SpaceColors.MutedInk,
    surfaceContainerLowest = SpaceColors.Card,
    surfaceContainerLow = SpaceColors.Daylight,
    surfaceContainer = SpaceColors.CardSunk,
    surfaceContainerHigh = SpaceColors.CardRaised,
    surfaceContainerHighest = SpaceColors.DividerLight,
    outline = SpaceColors.MutedInk,
    outlineVariant = SpaceColors.DividerLight,
    error = SpaceColors.AlertStrong,
    onError = Color.White,
    errorContainer = SpaceColors.AlertPale,
    onErrorContainer = SpaceColors.AlertInk,
    scrim = Color.Black,
)

@Composable
fun SpaceflightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    onToggleTheme: (() -> Unit)? = null,
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

    CompositionLocalProvider(
        LocalReducedMotion provides reducedMotion,
        LocalIsDarkTheme provides darkTheme,
        LocalToggleTheme provides onToggleTheme,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = SpaceflightTypography,
            shapes = SpaceflightShapes,
            content = content,
        )
    }
}

/**
 * A wash of solar light behind the top of each screen. It is what stops the near-black background
 * from reading as flat grey, and it is the one place the brand colour appears unmixed.
 */
val MaterialTheme.auroraWash: Brush
    @Composable
    get() = if (LocalIsDarkTheme.current) {
        Brush.verticalGradient(
            0f to SpaceColors.Solar.copy(alpha = 0.22f),
            0.55f to SpaceColors.Ion.copy(alpha = 0.05f),
            1f to Color.Transparent,
        )
    } else {
        Brush.verticalGradient(
            0f to SpaceColors.Solar.copy(alpha = 0.24f),
            0.55f to SpaceColors.Ion.copy(alpha = 0.10f),
            1f to Color.Transparent,
        )
    }

/**
 * Bottom-up scrim for text sitting on a photo. Two stops would band visibly over a bright sky, so
 * this eases in over four.
 */
val imageScrim: Brush
    get() = Brush.verticalGradient(
        0.35f to Color.Transparent,
        0.60f to Color.Black.copy(alpha = 0.28f),
        0.82f to Color.Black.copy(alpha = 0.66f),
        1f to Color.Black.copy(alpha = 0.88f),
    )
