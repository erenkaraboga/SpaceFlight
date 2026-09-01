package com.spaceflight.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.spaceflight.designsystem.R

/**
 * Newsreader for anything that should read as a story - masthead, headlines, card titles.
 * Optical size is set per role so a 32sp title is a display cut, not a blown-up text face.
 *
 * Source Sans 3 for UI and body: a news grotesque that stays quiet next to the serif and
 * still carries Turkish (and the rest of Latin Plus) without falling back to Roboto.
 */
@OptIn(ExperimentalTextApi::class)
private fun newsreader(weight: FontWeight, opticalSize: Float) = Font(
    resId = R.font.newsreader,
    weight = weight,
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight.weight),
        FontVariation.Setting("opsz", opticalSize),
    ),
)

@OptIn(ExperimentalTextApi::class)
private fun sourceSans(weight: FontWeight) = Font(
    resId = R.font.source_sans_3,
    weight = weight,
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight.weight),
    ),
)

private val NewsreaderDisplay = FontFamily(
    newsreader(FontWeight.SemiBold, opticalSize = 72f),
    newsreader(FontWeight.Bold, opticalSize = 72f),
    newsreader(FontWeight.ExtraBold, opticalSize = 72f),
)

private val NewsreaderHeadline = FontFamily(
    newsreader(FontWeight.SemiBold, opticalSize = 36f),
    newsreader(FontWeight.Bold, opticalSize = 36f),
)

private val NewsreaderTitle = FontFamily(
    newsreader(FontWeight.Medium, opticalSize = 18f),
    newsreader(FontWeight.SemiBold, opticalSize = 18f),
    newsreader(FontWeight.Bold, opticalSize = 18f),
)

private val SourceSans = FontFamily(
    sourceSans(FontWeight.Normal),
    sourceSans(FontWeight.Medium),
    sourceSans(FontWeight.SemiBold),
    sourceSans(FontWeight.Bold),
)

internal val SpaceflightTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NewsreaderDisplay,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 44.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.6).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = NewsreaderDisplay,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.5).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = NewsreaderDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.4).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = NewsreaderHeadline,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NewsreaderHeadline,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.2).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = NewsreaderHeadline,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.1).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = NewsreaderTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.15).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = NewsreaderTitle,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.1).sp,
    ),
    titleSmall = TextStyle(
        fontFamily = NewsreaderTitle,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.1.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp,
    ),
)

/** Newspaper kicker: small caps-tracking sans, never the serif. */
val EyebrowTextStyle: TextStyle = TextStyle(
    fontFamily = SourceSans,
    fontSize = 11.sp,
    lineHeight = 13.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.2.sp,
    textAlign = TextAlign.Center,
)
