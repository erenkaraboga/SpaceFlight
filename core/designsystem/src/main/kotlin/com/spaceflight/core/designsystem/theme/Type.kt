package com.spaceflight.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * Editorial hierarchy: headlines are tight and heavy so featured cards read like a magazine cover,
 * while body copy stays loose enough to skim a summary comfortably.
 */
internal val SpaceflightTypography = Typography().run {
    copy(
        displaySmall = displaySmall.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.5).sp,
        ),
        headlineMedium = headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 27.sp,
            lineHeight = 33.sp,
            letterSpacing = (-0.4).sp,
        ),
        headlineSmall = headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.2).sp,
        ),
        titleLarge = titleLarge.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.2).sp,
        ),
        titleMedium = titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 23.sp,
            letterSpacing = (-0.1).sp,
        ),
        bodyMedium = bodyMedium.copy(
            fontSize = 15.sp,
            lineHeight = 22.sp,
        ),
        bodyLarge = bodyLarge.copy(
            fontSize = 17.sp,
            lineHeight = 27.sp,
        ),
        labelLarge = labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.2.sp,
        ),
        labelSmall = labelSmall.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.6.sp,
        ),
    )
}

/** Used for the small uppercase source badge on cards. */
val SourceBadgeTextStyle: TextStyle = TextStyle(
    fontSize = 11.sp,
    lineHeight = 13.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.8.sp,
    textAlign = TextAlign.Center,
)
