package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActionPreview
import com.example.model.ActionStatus
import com.example.model.AssistantState
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.SpeakingWaveOrb
import com.example.ui.components.ZoyaTopBar
import com.example.ui.theme.ZoyaBackgroundBrush
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaStatusGreen
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary
import com.example.ui.theme.ZoyaYouTubeRed

@Composable
fun SpeakingScreen(
    state: AssistantState.Speaking,
    actionPreview: ActionPreview?,
    onCloseClick: () -> Unit,
    onStopSpeakingClick: () -> Unit,
    onToggleMuteClick: () -> Unit,
    onNavigateToYouTube: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZoyaBackgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            ZoyaTopBar(
                showCloseButton = true,
                showWaveIndicator = true,
                title = null,
                showSettings = false,
                onBackClick = onCloseClick
            )

            // Header & Silky Fluid Rose Orb
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Zoya is speaking...",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZoyaTextPrimary
                )

                Spacer(modifier = Modifier.height(28.dp))

                // The Central Fluid Silk Rose Orb from the photo!
                SpeakingWaveOrb(
                    amplitude = state.amplitude,
                    size = 210.dp
                )
            }

            // Cards Section (Zoya avatar message card + Action result card)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Zoya avatar speech card
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chic female avatar badge
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD3DE))
                        ) {
                            Text(
                                text = "👩‍💼",
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = state.speechText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZoyaTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Action Card (e.g. YouTube Searching)
                if (actionPreview != null) {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (actionPreview.targetApp == "YouTube") {
                                    onNavigateToYouTube()
                                }
                            },
                        cornerRadius = 20.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFEEF0))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Action",
                                    tint = ZoyaYouTubeRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = actionPreview.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZoyaTextPrimary
                                )
                                Text(
                                    text = actionPreview.subtitle,
                                    fontSize = 13.sp,
                                    color = ZoyaTextSecondary
                                )
                            }

                            if (actionPreview.status == ActionStatus.IN_PROGRESS) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.5.dp,
                                    color = ZoyaPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = ZoyaStatusGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom 3 Action Buttons (Mute, Red Stop Button, Keyboard)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker / Mute
                IconButton(
                    onClick = onToggleMuteClick,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = if (state.isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Toggle Mute",
                        tint = ZoyaTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Central Large Red Stop Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = ZoyaPrimary,
                            spotColor = ZoyaPrimary
                        )
                        .clip(CircleShape)
                        .background(Color(0xFFFF2E63))
                        .clickable { onStopSpeakingClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Speaking",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Keyboard / Transcript
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Keyboard",
                        tint = ZoyaTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
