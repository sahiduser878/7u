package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaPrimaryGradient
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary

@Composable
fun GlowingOrbButton(
    isListening: Boolean,
    amplitude: Float = 0.3f,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.14f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 900 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = if (isListening) 0.35f else 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 900 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        // Main Orb with side soundwave bars
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Left soundwave bars
            SideSoundWaves(amplitude = amplitude, isMirrored = true)

            Spacer(modifier = Modifier.width(16.dp))

            // Concentric Glowing Orb
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .testTag("main_mic_orb")
            ) {
                // Outermost translucent ring
                Box(
                    modifier = Modifier
                        .size(195.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3366).copy(alpha = haloAlpha * 0.35f))
                )

                // Middle frosted glow ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(1f + (pulseScale - 1f) * 0.6f)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3366).copy(alpha = haloAlpha * 0.75f))
                )

                // Inner soft white frosted rim
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                )

                // Central Vibrant Glowing Sphere Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(112.dp)
                        .shadow(
                            elevation = 18.dp,
                            shape = CircleShape,
                            ambientColor = ZoyaPrimary,
                            spotColor = ZoyaPrimary
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF527A),
                                    Color(0xFFFF2E63),
                                    Color(0xFFE02456)
                                )
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = Color.White),
                            onClick = onClick
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Assistant Microphone",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right soundwave bars
            SideSoundWaves(amplitude = amplitude, isMirrored = false)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Tap to speak",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = ZoyaTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "or say",
            fontSize = 13.sp,
            color = ZoyaTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // "Hey Zoya" Pill Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0xFFFFE6EC))
                .padding(horizontal = 20.dp, vertical = 7.dp)
                .clickable { onClick() }
        ) {
            Text(
                text = "Hey Zoya",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ZoyaPrimary
            )
        }
    }
}

@Composable
fun SideSoundWaves(amplitude: Float, isMirrored: Boolean) {
    val barHeights = if (isMirrored) listOf(14.dp, 24.dp, 36.dp, 18.dp) else listOf(18.dp, 36.dp, 24.dp, 14.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        barHeights.forEach { baseHeight ->
            val dynamicHeight = (baseHeight.value * (0.8f + amplitude * 0.8f)).coerceIn(8f, 44f).dp
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(dynamicHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFFF9BB3).copy(alpha = 0.75f))
            )
        }
    }
}
