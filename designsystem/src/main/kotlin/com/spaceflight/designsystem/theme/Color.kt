package com.spaceflight.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * A fixed palette rather than dynamic colour: the app leans on a deep-space identity, and letting
 * the wallpaper repaint it would wash that away.
 *
 * The accents are named after what they evoke - solar fire over a night sky - and the neutrals are
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
    val Daylight = Color(0xFFFFFBF7)
    val Card = Color(0xFFFFFFFF)
    val CardSunk = Color(0xFFF7F1EB)
    val CardRaised = Color(0xFFF0E8E0)
    val DividerLight = Color(0xFFE8DDD4)
    val MutedInk = Color(0xFF56536B)
    val Ink = Color(0xFF13111F)

    // Accents. Solar orange is the brand; teal stays as the secondary cool note.
    val Solar = Color(0xFFFF8F4A)
    val SolarDeep = Color(0xFF8A3A12)
    val SolarInk = Color(0xFF3A1400)
    val SolarPale = Color(0xFFFFE0CC)
    val SolarStrong = Color(0xFFD4520B)

    val Ion = Color(0xFF5FEAD4)
    val IonDeep = Color(0xFF0B5F55)
    val IonInk = Color(0xFF00332B)
    val IonPale = Color(0xFFB7FFF1)
    val IonStrong = Color(0xFF00786A)

    val Alert = Color(0xFFFF8D8D)
    val AlertDeep = Color(0xFF7C1D1D)
    val AlertInk = Color(0xFF400C0C)
    val AlertPale = Color(0xFFFFDAD6)
    val AlertStrong = Color(0xFFB3261E)
}
