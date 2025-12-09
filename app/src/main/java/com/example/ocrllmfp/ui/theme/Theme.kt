package com.example.ocrllmfp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFF5F5F5),
    surface = Color(0xCCFFFFFF),
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF0A0A0A),
    surface = Color(0xCC1A1A1A),
    onBackground = Color(0xFFEEEEEE),
    onSurface = Color(0xFFEEEEEE)
)

private val BlueColorScheme = darkColorScheme(
    primary = Color(0xFF00B4D8),
    background = Color(0xFF0D1B2A),
    surface = Color(0xCC1B263B),
    onBackground = Color.White,
    onSurface = Color.White
)

private val GreenColorScheme = darkColorScheme(
    primary = Color(0xFF40916C),
    background = Color(0xFF081C15),
    surface = Color(0xCC1B4332),
    onBackground = Color.White,
    onSurface = Color.White
)

private val PurpleColorScheme = darkColorScheme(
    primary = Color(0xFF9D4EDD),
    background = Color(0xFF10002B),
    surface = Color(0xCC240046),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun OCRAIATheme(
    theme: AppTheme = AppTheme.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.DARK -> DarkColorScheme
        AppTheme.BLUE -> BlueColorScheme
        AppTheme.GREEN -> GreenColorScheme
        AppTheme.PURPLE -> PurpleColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}