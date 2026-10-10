package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ZoyaPrimary

@Composable
fun ListeningWaveOrb(
    amplitude: Float,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "listeningOrb")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Outer fluid ripple
        Box(
            modifier = Modifier
                .size(size)
                .scale(waveScale * (1f + amplitude * 0.15f))
                .clip(CircleShape)
                .background(Color(0xFFFF3366).copy(alpha = 0.12f))
        )

        // Middle soft rose ring
        Box(
            modifier = Modifier
                .size(size * 0.82f)
                .scale(waveScale)
                .clip(CircleShape)
                .background(Color(0xFFFF3366).copy(alpha = 0.22f))
        )

        // Inner glowing orb
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size * 0.65f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF6B8B),
                            Color(0xFFFF3366),
                            Color(0xFFE91E63)
                        )
                    )
                )
        ) {
            // White vertical soundwave bars inside the orb
            WaveBarsInsideOrb(amplitude = amplitude)
        }
    }
}

@Composable
fun WaveBarsInsideOrb(amplitude: Float) {
    val heights = listOf(14.dp, 28.dp, 42.dp, 56.dp, 48.dp, 34.dp, 20.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.5.dp)
    ) {
        heights.forEachIndexed { index, baseHeight ->
            val factor = 0.6f + ((amplitude * 1.2f + (index % 3) * 0.15f) % 1.0f)
            val barHeight = (baseHeight.value * factor).coerceIn(10f, 60f).dp
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.92f))
            )
        }
    }
}

@Composable
fun SpeakingWaveOrb(
    amplitude: Float,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "speakingOrb")
    val fluidRot by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fluidRot"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Multi-layered organic silk fluid orb
        Box(
            modifier = Modifier
                .size(size)
                .scale(fluidRot)
                .clip(CircleShape)
                .background(Color(0xFFFFE1E8).copy(alpha = 0.7f))
        )

        Box(
            modifier = Modifier
                .size(size * 0.82f)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF85A1),
                            Color(0xFFFF3366),
                            Color(0xFFE02456)
                        )
                    )
                )
        )

        // Inner silky swirl highlight
        Box(
            modifier = Modifier
                .size(size * 0.55f)
                .scale(fluidRot * 0.95f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color(0xFFFF527A).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun HorizontalVoiceWaveform(
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val bars = 22
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until bars) {
            val distFromCenter = kotlin.math.abs(i - (bars / 2))
            val baseH = (28 - distFromCenter * 1.8f).coerceAtLeast(6f)
            val dynH = (baseH * (0.5f + amplitude * 1.1f)).coerceIn(4f, 36f).dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(3.dp)
                    .height(dynH)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ZoyaPrimary.copy(alpha = 0.8f))
            )
        }
    }
}
