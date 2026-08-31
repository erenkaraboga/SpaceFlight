package com.spaceflight.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * Editorial hierarchy. Headlines are large, heavy and tightly tracked so a card reads like a
 * magazine cover; body copy stays loose enough to skim a summary comfortably. Negative tracking
 * grows with size, which is what keeps the big type from looking like it was simply scaled up.
 */
internal val SpaceflightTypography = Typography().run {
    copy(
        displayMedium = displayMedium.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 40.sp,
            lineHeight = 44.sp,
            letterSpacing = (-1.2).sp,
        ),
        displaySmall = displaySmall.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            lineHeight = 37.sp,
            letterSpacing = (-0.9).sp,
        ),
        headlineLarge = headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.7).sp,
        ),
        headlineMedium = headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            letterSpacing = (-0.5).sp,
        ),
        headlineSmall = headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            lineHeight = 27.sp,
            letterSpacing = (-0.3).sp,
        ),
        titleLarge = titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            lineHeight = 25.sp,
            letterSpacing = (-0.3).sp,
        ),
        titleMedium = titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = (-0.1).sp,
        ),
        titleSmall = titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 19.sp,
        ),
        bodyLarge = bodyLarge.copy(
            fontSize = 16.sp,
            lineHeight = 26.sp,
        ),
        bodyMedium = bodyMedium.copy(
            fontSize = 14.sp,
            lineHeight = 21.sp,
        ),
        bodySmall = bodySmall.copy(
            fontSize = 12.sp,
            lineHeight = 17.sp,
        ),
        labelLarge = labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            letterSpacing = 0.1.sp,
        ),
        labelMedium = labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 0.3.sp,
        ),
        labelSmall = labelSmall.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            letterSpacing = 0.4.sp,
        ),
    )
}

/** The small all-caps label used for source badges and section eyebrows. */
val EyebrowTextStyle: TextStyle = TextStyle(
    fontSize = 11.sp,
    lineHeight = 13.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.1.sp,
    textAlign = TextAlign.Center,
)
