package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Zoya AI Palette: Glassmorphism (Pink & Rose Soft White)
val ZoyaPrimary = Color(0xFFFF3366)          // Radiant Coral Rose
val ZoyaPrimaryDark = Color(0xFFE02456)
val ZoyaPrimaryLight = Color(0xFFFF6584)
val ZoyaPrimarySoft = Color(0xFFFF85A1)

val ZoyaAccent = Color(0xFFFF2A6D)
val ZoyaSecondary = Color(0xFFFF7597)
val ZoyaTertiary = Color(0xFFFFA4B8)

val ZoyaBgStart = Color(0xFFFFF7F9)
val ZoyaBgCenter = Color(0xFFFFF0F3)
val ZoyaBgEnd = Color(0xFFFFE6EC)

val ZoyaCardBg = Color(0xFFFFFFFF)
val ZoyaCardBgTranslucent = Color(0xCCFFFFFF)
val ZoyaCardBorder = Color(0x66FFD3DE)
val ZoyaGlassGlow = Color(0x33FF3366)

val ZoyaTextPrimary = Color(0xFF1E1B24)
val ZoyaTextSecondary = Color(0xFF756D7A)
val ZoyaTextMuted = Color(0xFFA59DA9)

val ZoyaStatusGreen = Color(0xFF10B981)
val ZoyaYouTubeRed = Color(0xFFFF0000)
val ZoyaWhatsAppGreen = Color(0xFF25D366)
val ZoyaInstagramPink = Color(0xFFE1306C)
val ZoyaChromeBlue = Color(0xFF4285F4)

// Gradient Brushes
val ZoyaPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFF2E63), Color(0xFFFF6584))
)

val ZoyaOrbGradient = Brush.radialGradient(
    colors = listOf(
        Color(0xFFFF5277),
        Color(0xFFFF2E63),
        Color(0xFFE02456)
    )
)

val ZoyaBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFF7F8),
        Color(0xFFFFF0F4),
        Color(0xFFFFE9EE)
    )
)
