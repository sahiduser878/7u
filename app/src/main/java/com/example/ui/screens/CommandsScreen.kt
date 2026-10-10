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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommandCategory
import com.example.model.VoiceCommandItem
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.ZoyaBackgroundBrush
import com.example.ui.theme.ZoyaChromeBlue
import com.example.ui.theme.ZoyaInstagramPink
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary
import com.example.ui.theme.ZoyaWhatsAppGreen
import com.example.ui.theme.ZoyaYouTubeRed

@Composable
fun CommandsScreen(
    commands: List<VoiceCommandItem>,
    onBackClick: () -> Unit,
    onCommandClick: (VoiceCommandItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(CommandCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredCommands = commands.filter { item ->
        val matchesCategory = selectedCategory == CommandCategory.ALL || item.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                item.commandText.contains(searchQuery, ignoreCase = true) ||
                item.translatedHint.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZoyaBackgroundBrush)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ZoyaTextPrimary
                    )
                }

                Text(
                    text = "Popular Commands",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZoyaTextPrimary
                )

                IconButton(onClick = { isSearchActive = !isSearchActive }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ZoyaTextPrimary
                    )
                }
            }

            // Search bar (if active)
            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search commands in Hindi, Bengali, English...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZoyaPrimary,
                        unfocusedBorderColor = Color(0xFFFFD3DE),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Filter Chips Row (All, Apps, Music, Daily, System)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CommandCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ZoyaPrimary else Color.White.copy(alpha = 0.9f))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else ZoyaTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Command Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCommands) { cmd ->
                    CommandRowCard(
                        command = cmd,
                        onClick = { onCommandClick(cmd) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}

@Composable
fun CommandRowCard(
    command: VoiceCommandItem,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            val (icon, tint) = getCommandIconAndColor(command.iconType)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF2F5))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = command.commandText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZoyaTextPrimary
                )
                Text(
                    text = command.translatedHint,
                    fontSize = 12.sp,
                    color = ZoyaTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Execute",
                tint = Color(0xFFA59DA9),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun getCommandIconAndColor(iconType: String): Pair<ImageVector, Color> {
    return when (iconType.lowercase()) {
        "youtube" -> Icons.Default.PlayArrow to ZoyaYouTubeRed
        "whatsapp" -> Icons.AutoMirrored.Filled.Chat to ZoyaWhatsAppGreen
        "instagram" -> Icons.Default.MusicNote to ZoyaInstagramPink
        "chrome" -> Icons.Default.Language to ZoyaChromeBlue
        "alarm" -> Icons.Default.Alarm to Color(0xFFE91E63)
        "calculator" -> Icons.Default.Calculate to Color(0xFF5F6368)
        "settings" -> Icons.Default.Settings to Color(0xFF607D8B)
        "google" -> Icons.Default.Search to ZoyaChromeBlue
        "flashlight" -> Icons.Default.FlashlightOn to Color(0xFF0288D1)
        else -> Icons.Default.PlayArrow to ZoyaPrimary
    }
}
