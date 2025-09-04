package com.yarhooshmand.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF00695C),
    secondary = Color(0xFF26A69A),
    surface = Color(0xFFF7F7F7),
    background = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF4DB6AC),
    secondary = Color(0xFF80CBC4),
    surface = Color(0xFF121212),
    background = Color(0xFF0A0A0A),
    onPrimary = Color.Black,
    onSecondary = Color.Black
)

@Composable
fun YarHooshmandTheme(content: @Composable () -> Unit) {
    val useDark = when (ThemeController.mode) {
        ThemeController.Mode.SYSTEM -> isSystemInDarkTheme()
        ThemeController.Mode.DARK -> true
        ThemeController.Mode.LIGHT -> false
    }
    MaterialTheme(
        colorScheme = if (useDark) DarkColors else LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
