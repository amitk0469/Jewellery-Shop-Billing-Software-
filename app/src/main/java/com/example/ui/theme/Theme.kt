package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LiquidGlassColorScheme = lightColorScheme(
    primary = GoldDeep,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = GoldDark,
    secondary = GoldPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E7CA),
    onSecondaryContainer = GoldDeep,
    tertiary = CharcoalDark,
    onTertiary = Color.White,
    background = MeshBaseStart,
    onBackground = CharcoalDark,
    surface = Color(0xE6FFFFFF),
    onSurface = CharcoalDark,
    surfaceVariant = Color(0xF2F7F8FA),
    onSurfaceVariant = GrayText,
    outline = GrayBorder
)

@Composable
fun AshaJewellersTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LiquidGlassColorScheme,
        typography = Typography,
        content = content
    )
}
