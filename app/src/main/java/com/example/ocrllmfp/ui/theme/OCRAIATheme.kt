package com.example.ocrllmfp.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light Theme Colors
private val md_theme_light_primary = Color(0xFF6200EE)
private val md_theme_light_background = Color(0xFFF5F5F5)
private val md_theme_light_surface = Color(0xFFFFFFFF)
private val md_theme_light_onPrimary = Color.White
private val md_theme_light_onBackground = Color(0xFF1A1A1A)
private val md_theme_light_onSurface = Color(0xFF1A1A1A)

// Dark Theme Colors
private val md_theme_dark_primary = Color(0xFFBB86FC)
private val md_theme_dark_background = Color(0xFF0A0A0A)
private val md_theme_dark_surface = Color(0xFF1E1E1E)
private val md_theme_dark_onPrimary = Color.Black
private val md_theme_dark_onBackground = Color(0xFFEEEEEE)
private val md_theme_dark_onSurface = Color(0xFFEEEEEE)

// Blue Theme Colors
private val md_theme_blue_primary = Color(0xFF00B4D8)
private val md_theme_blue_background = Color(0xFF0D1B2A)
private val md_theme_blue_surface = Color(0xFF1B263B)
private val md_theme_blue_accent = Color(0xFF90E0EF)

// Green Theme Colors
private val md_theme_green_primary = Color(0xFF40916C)
private val md_theme_green_background = Color(0xFF081C15)
private val md_theme_green_surface = Color(0xFF1B4332)
private val md_theme_green_accent = Color(0xFF95D5B2)

// Purple Theme Colors
private val md_theme_purple_primary = Color(0xFF9D4EDD)
private val md_theme_purple_background = Color(0xFF10002B)
private val md_theme_purple_surface = Color(0xFF240046)
private val md_theme_purple_accent = Color(0xFFC77DFF)

@Composable
fun OCRAIATheme(
    theme: AppTheme = AppTheme.DARK,
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        // Dynamic colors solo para LIGHT y DARK
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                (theme == AppTheme.LIGHT || theme == AppTheme.DARK) -> {
            if (theme == AppTheme.LIGHT) {
                dynamicLightColorScheme(context)
            } else {
                dynamicDarkColorScheme(context)
            }
        }

        // Temas estáticos
        theme == AppTheme.LIGHT -> lightColorScheme(
            primary = md_theme_light_primary,
            onPrimary = md_theme_light_onPrimary,
            background = md_theme_light_background,
            onBackground = md_theme_light_onBackground,
            surface = md_theme_light_surface,
            onSurface = md_theme_light_onSurface,
            secondary = Color(0xFF03DAC6)
        )

        theme == AppTheme.DARK -> darkColorScheme(
            primary = md_theme_dark_primary,
            onPrimary = md_theme_dark_onPrimary,
            background = md_theme_dark_background,
            onBackground = md_theme_dark_onBackground,
            surface = md_theme_dark_surface,
            onSurface = md_theme_dark_onSurface,
            secondary = Color(0xFF03DAC6)
        )

        theme == AppTheme.BLUE -> darkColorScheme(
            primary = md_theme_blue_primary,
            onPrimary = Color.White,
            background = md_theme_blue_background,
            onBackground = Color.White,
            surface = md_theme_blue_surface,
            onSurface = Color.White,
            secondary = md_theme_blue_accent
        )

        theme == AppTheme.GREEN -> darkColorScheme(
            primary = md_theme_green_primary,
            onPrimary = Color.White,
            background = md_theme_green_background,
            onBackground = Color.White,
            surface = md_theme_green_surface,
            onSurface = Color.White,
            secondary = md_theme_green_accent
        )

        theme == AppTheme.PURPLE -> darkColorScheme(
            primary = md_theme_purple_primary,
            onPrimary = Color.White,
            background = md_theme_purple_background,
            onBackground = Color.White,
            surface = md_theme_purple_surface,
            onSurface = Color.White,
            secondary = md_theme_purple_accent
        )

        else -> darkColorScheme(
            primary = md_theme_dark_primary,
            background = md_theme_dark_background,
            surface = md_theme_dark_surface
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}