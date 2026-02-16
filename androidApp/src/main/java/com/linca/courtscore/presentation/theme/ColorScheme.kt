package com.linca.courtscore.presentation.theme

import androidx.compose.ui.graphics.Color

data class ColorScheme(
    val playerOneColor: Color,
    val playerTwoColor: Color,
    val name: String
)

object ColorSchemes {
    val TealCoral = ColorScheme(
        playerOneColor = Color(0xFF008080),
        playerTwoColor = Color(0xFFFF7F50),
        name = "Teal & Coral"
    )

    val BlueOrange = ColorScheme(
        playerOneColor = Color(0xFF5076FF),
        playerTwoColor = Color(0xFFF8A464),
        name = "Blue & Orange"
    )

    val Vintage = ColorScheme(
        playerOneColor = Color(0xFFA4193D),
        playerTwoColor = Color(0xFFFFDFB9),
        name = "Red & Cream"
    )

    val PurpleYellow = ColorScheme(
        playerOneColor = Color(0xFFd3687f),
        playerTwoColor = Color(0xFFCBCE91),
        name = "Pink & Yellow"
    )

    val CyanMagenta = ColorScheme(
        playerOneColor = Color(0xFF00BCD4),
        playerTwoColor = Color(0xFFE91E63),
        name = "Cyan & Magenta"
    )

    val TealAmber = ColorScheme(
        playerOneColor = Color(0xFFFDD20E),
        playerTwoColor = Color(0xFFc72d1b),
        name = "Yellow & Red"
    )

    val IndigoLime = ColorScheme(
        playerOneColor = Color(0xFF4831D4),
        playerTwoColor = Color(0xFFCCF381),
        name = "Indigo & Lime"
    )

    val all = listOf(
        TealCoral,
        BlueOrange,
        Vintage,
        PurpleYellow,
        CyanMagenta,
        TealAmber,
        IndigoLime
    )

    fun findByName(name: String): ColorScheme {
        return all.find { it.name == name } ?: TealCoral
    }
}
