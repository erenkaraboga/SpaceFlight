package com.spaceflight.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * A fixed palette rather than dynamic colour: the app leans on a deep-space identity, and letting
 * the wallpaper repaint it would wash that away.
 *
 * The accents are named after what they evoke - an aurora over a night sky - and the neutrals are
 * layered from near-black upwards so cards separate by elevation alone, without borders or shadows.
 */
internal object SpaceColors {

    // Dark neutrals, from the void up to the highest card surface.
    val Void = Color(0xFF07070C)
    val Abyss = Color(0xFF0C0C13)
    val Slate = Color(0xFF14141E)
    val SlateHigh = Color(0xFF1C1C28)
    val SlateHighest = Color(0xFF252533)
    val Divider = Color(0xFF2E2D3C)
    val Muted = Color(0xFF9E9BB4)
    val Bright = Color(0xFFEDEBF7)

    // Light neutrals.
    val Daylight = Color(0xFFFBFAFF)
    val Card = Color(0xFFFFFFFF)
    val CardSunk = Color(0xFFF2F0FA)
    val CardRaised = Color(0xFFE9E6F6)
    val DividerLight = Color(0xFFDFDBEE)
    val MutedInk = Color(0xFF56536B)
    val Ink = Color(0xFF13111F)

    // Accents.
    val Aurora = Color(0xFFA88BFF)
    val AuroraDeep = Color(0xFF4B2CA8)
    val AuroraInk = Color(0xFF1B0B45)
    val AuroraPale = Color(0xFFE8DEFF)
    val AuroraStrong = Color(0xFF6A3CE0)

    val Ion = Color(0xFF5FEAD4)
    val IonDeep = Color(0xFF0B5F55)
    val IonInk = Color(0xFF00332B)
    val IonPale = Color(0xFFB7FFF1)
    val IonStrong = Color(0xFF00786A)

    val Solar = Color(0xFFFFA574)
    val SolarDeep = Color(0xFF8A3A12)
    val SolarInk = Color(0xFF441700)
    val SolarPale = Color(0xFFFFDCC9)
    val SolarStrong = Color(0xFFB0491C)

    val Alert = Color(0xFFFF8D8D)
    val AlertDeep = Color(0xFF7C1D1D)
    val AlertInk = Color(0xFF400C0C)
    val AlertPale = Color(0xFFFFDAD6)
    val AlertStrong = Color(0xFFB3261E)
}
