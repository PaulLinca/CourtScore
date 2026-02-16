package com.linca.courtscore.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme

val LocalColorScheme = staticCompositionLocalOf { ColorSchemes.SunsetOcean }

@Composable
fun CourtScoreTheme(
    colorScheme: ColorScheme = ColorSchemes.SunsetOcean,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalColorScheme provides colorScheme) {
        MaterialTheme(
            content = content
        )
    }
}

val PadelBlue = Color(0xFF1E8FD5)
val Orange = Color(0xFFF8A464)

val BackgroundColor = Color(0xFF000000)
val PrimaryTextColor = Color(0xFFfbfbfb)
val SecondaryTextColor = Color(0xFFaaaab1)
val ElevatedBackgroundColor = Color(0xFF222327)


