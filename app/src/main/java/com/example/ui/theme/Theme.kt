package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ZoyaLightColorScheme = lightColorScheme(
    primary = ZoyaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF3F0015),
    secondary = ZoyaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECF0),
    onSecondaryContainer = Color(0xFF3B0716),
    tertiary = ZoyaTertiary,
    onTertiary = Color.White,
    background = ZoyaBgCenter,
    onBackground = ZoyaTextPrimary,
    surface = Color.White,
    onSurface = ZoyaTextPrimary,
    surfaceVariant = Color(0xFFFFF2F5),
    onSurfaceVariant = ZoyaTextSecondary,
    outline = ZoyaCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep signature compatible
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZoyaLightColorScheme,
        typography = Typography,
        content = content
    )
}
