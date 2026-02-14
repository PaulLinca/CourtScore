package com.linca.courtscorewear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme

val LocalColorScheme = staticCompositionLocalOf { ColorSchemes.BlueOrange }

@Composable
fun CourtScoreTheme(
    colorScheme: ColorScheme = ColorSchemes.BlueOrange,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalColorScheme provides colorScheme) {
        MaterialTheme(
            content = content
        )
    }
}

val BackgroundColor = Color(0xFF121214)
val PrimaryTextColor = Color(0xFFfbfbfb)
val SecondaryTextColor = Color(0xFFaaaab1)
val AccentColor = Color(0xFFAEF864)
val ElevatedBackgroundColor = Color(0xFF222327)

val PadelBlue = Color(0xFF1E8FD5)
val Yellow = Color(0xFFF8DF64)
val Orange = Color(0xFFF8A464)
val Red = Color(0xFFF86464)
