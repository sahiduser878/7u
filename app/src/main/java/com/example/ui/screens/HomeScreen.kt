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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RecentCommandRecord
import com.example.model.ZoyaSettings
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GlowingOrbButton
import com.example.ui.components.ZoyaTopBar
import com.example.ui.theme.ZoyaBackgroundBrush
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaStatusGreen
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary
import com.example.ui.theme.ZoyaYouTubeRed

@Composable
fun HomeScreen(
    settings: ZoyaSettings,
    recentCommands: List<RecentCommandRecord>,
    onMicClick: () -> Unit,
    onQuickActionClick: (String) -> Unit,
    onNavigateToCommands: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToYouTube: () -> Unit,
    onToggleWakeWord: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZoyaBackgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Bar
            item {
                ZoyaTopBar(
                    showBackButton = false,
                    onSettingsClick = onNavigateToSettings
                )
            }

            // Central Glowing Orb
            item {
                GlowingOrbButton(
                    isListening = false,
                    amplitude = 0.35f,
                    onClick = onMicClick
                )
            }

            // Quick Action Cards (YouTube, Apps, Alarm, More)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionPill(
                        icon = Icons.Default.PlayArrow,
                        label = "YouTube",
                        iconColor = ZoyaYouTubeRed,
                        onClick = onNavigateToYouTube
                    )
                    QuickActionPill(
                        icon = Icons.Default.GridView,
                        label = "Apps",
                        iconColor = ZoyaPrimary,
                        onClick = onNavigateToCommands
                    )
                    QuickActionPill(
                        icon = Icons.Default.Alarm,
                        label = "Alarm",
                        iconColor = Color(0xFFE91E63),
                        onClick = { onQuickActionClick("Kal subah 6 baje alarm lagao") }
                    )
                    QuickActionPill(
                        icon = Icons.Default.MoreHoriz,
                        label = "More",
                        iconColor = Color(0xFF5F6368),
                        onClick = onNavigateToCommands
                    )
                }
            }

            // "I'm ready" Status Card
            item {
                Spacer(modifier = Modifier.height(20.dp))
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clickable { onMicClick() },
                    cornerRadius = 20.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Green Dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(ZoyaStatusGreen)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "I'm ready",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZoyaTextPrimary
                            )
                            Text(
                                text = "Say \"Hey Zoya\" or tap the mic",
                                fontSize = 12.sp,
                                color = ZoyaTextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Go",
                            tint = Color(0xFFA59DA9),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Info Widgets Row (Weather & Wake Word Status)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Weather Card
                    GlassmorphicCard(
                        modifier = Modifier.weight(1f),
                        cornerRadius = 20.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = "Weather",
                                    tint = Color(0xFFFFA000),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Good Morning",
                                    fontSize = 11.sp,
                                    color = ZoyaTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "28°C",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZoyaTextPrimary
                            )
                            Text(
                                text = "Mostly Clear • Kolkata",
                                fontSize = 11.sp,
                                color = ZoyaTextSecondary
                            )
                        }
                    }

                    // Wake Word Status Card
                    GlassmorphicCard(
                        modifier = Modifier.weight(1f),
                        cornerRadius = 20.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Wake Word",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZoyaTextPrimary
                                )
                                Switch(
                                    checked = settings.wakeWordEnabled,
                                    onCheckedChange = onToggleWakeWord,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = ZoyaPrimary
                                    ),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (settings.wakeWordEnabled) ZoyaPrimary else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (settings.wakeWordEnabled) "Hey Zoya Active" else "Wake Word Off",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (settings.wakeWordEnabled) ZoyaPrimary else ZoyaTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Recent Commands Section
            item {
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Commands",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZoyaTextPrimary
                    )
                    Text(
                        text = "View all",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ZoyaPrimary,
                        modifier = Modifier.clickable { onNavigateToCommands() }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(recentCommands.size) { index ->
                val cmd = recentCommands[index]
                RecentCommandCard(
                    command = cmd,
                    onClick = { onQuickActionClick(cmd.prompt) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun QuickActionPill(
    icon: ImageVector,
    label: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .size(72.dp)
            .clickable(onClick = onClick),
        cornerRadius = 20.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ZoyaTextPrimary
            )
        }
    }
}

@Composable
fun RecentCommandCard(
    command: RecentCommandRecord,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEEF2))
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = ZoyaPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = command.prompt,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZoyaTextPrimary
                )
                Text(
                    text = command.timeAgo,
                    fontSize = 11.sp,
                    color = ZoyaTextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = ZoyaStatusGreen,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
