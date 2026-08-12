package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// High Density Material 3 Palette
val EsportsBackground = Color(0xFF1C1B1F)
val EsportsSurface = Color(0xFF211F26)
val EsportsSurfaceVariant = Color(0xFF332D41)
val EsportsCardBorder = Color(0xFF49454F)

val EsportsCyan = Color(0xFFD0BCFF) // High Density Primary Lavender Accent
val EsportsGold = Color(0xFFFFB800) // High Density Amber Gold Accent
val EsportsSilver = Color(0xFFCAC4D0)
val EsportsBronze = Color(0xFFE08A5D)

val EsportsLiveRed = Color(0xFFE46962)
val EsportsBooyahGreen = Color(0xFF81C784)
val EsportsPurple = Color(0xFFD0BCFF)

val EsportsTextPrimary = Color(0xFFE6E1E5)
val EsportsTextSecondary = Color(0xFFCAC4D0)
val EsportsTextMuted = Color(0xFF938F99)

private val EsportsDarkColorScheme = darkColorScheme(
    primary = EsportsCyan,
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4A4458),
    onPrimaryContainer = Color(0xFFE8DEF8),

    secondary = EsportsGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF4F3700),
    onSecondaryContainer = EsportsGold,

    tertiary = EsportsPurple,
    onTertiary = Color(0xFF381E72),

    background = EsportsBackground,
    onBackground = EsportsTextPrimary,

    surface = EsportsSurface,
    onSurface = EsportsTextPrimary,

    surfaceVariant = EsportsSurfaceVariant,
    onSurfaceVariant = EsportsTextSecondary,

    error = EsportsLiveRed,
    onError = Color.White,

    outline = EsportsCardBorder,
    outlineVariant = Color(0xFF49454F)
)

@Composable
fun EsportsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsportsDarkColorScheme,
        typography = Typography,
        content = content
    )
}
