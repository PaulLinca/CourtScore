package com.linca.courtscore.presentation.theme

import androidx.compose.ui.graphics.Color

data class ColorScheme(
    val playerOneColor: Color,
    val playerTwoColor: Color,
    val name: String
)

object ColorSchemes {

    val SunsetOcean = ColorScheme(
        playerOneColor = Color(0xFFFF7E5F),
        playerTwoColor = Color(0xFF00D4FF),
        name = "Sunset & Ocean"
    )

    private val PinkYellow = ColorScheme(
        playerOneColor = Color(0xFFd3687f),
        playerTwoColor = Color(0xFFCBCE91),
        name = "Pink & Yellow"
    )

    private val LavenderPeach = ColorScheme(
        playerOneColor = Color(0xFF9B59B6),
        playerTwoColor = Color(0xFFFFB347),
        name = "Lavender & Peach"
    )

    private val CyanMagenta = ColorScheme(
        playerOneColor = Color(0xFF00BCD4),
        playerTwoColor = Color(0xFFE91E63),
        name = "Cyan & Magenta"
    )

    private val YellowRed = ColorScheme(
        playerOneColor = Color(0xFFFDD20E),
        playerTwoColor = Color(0xFFc72d1b),
        name = "Yellow & Red"
    )

    private val MintCoral = ColorScheme(
        playerOneColor = Color(0xFF3EECAC),
        playerTwoColor = Color(0xFFFF6B6B),
        name = "Mint & Coral"
    )

    private val ForestSky = ColorScheme(
        playerOneColor = Color(0xFF27AE60),
        playerTwoColor = Color(0xFF3498DB),
        name = "Forest & Sky"
    )

    val all = listOf(
        SunsetOcean,
        PinkYellow,
        LavenderPeach,
        CyanMagenta,
        YellowRed,
        MintCoral,
        ForestSky
    )

    fun findByName(name: String): ColorScheme {
        return all.find { it.name == name } ?: SunsetOcean
    }
}
