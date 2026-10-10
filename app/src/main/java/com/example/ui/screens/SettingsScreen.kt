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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.ZoyaSettings
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.ZoyaBackgroundBrush
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaStatusGreen
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary

@Composable
fun SettingsScreen(
    settings: ZoyaSettings,
    onBackClick: () -> Unit,
    onToggleWakeWord: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onVoiceSelected: (String) -> Unit,
    onApiKeySaved: (String) -> Unit,
    onRequestPermissions: () -> Unit
) {
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var tempApiKey by remember { mutableStateOf(settings.customApiKey) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZoyaBackgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ZoyaTextPrimary
                        )
                    }
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZoyaTextPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // General Voice Settings
            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Wake Word Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SettingIconCircle(icon = Icons.Default.Mic)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Wake Word",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ZoyaTextPrimary
                                    )
                                    Text(
                                        text = "Listen for \"Hey Zoya\"",
                                        fontSize = 12.sp,
                                        color = ZoyaTextSecondary
                                    )
                                }
                            }
                            Switch(
                                checked = settings.wakeWordEnabled,
                                onCheckedChange = onToggleWakeWord,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ZoyaPrimary
                                )
                            )
                        }

                        // Voice Selection
                        SettingNavRow(
                            icon = Icons.Default.RecordVoiceOver,
                            title = "Voice",
                            value = settings.assistantVoice,
                            onClick = { showVoiceDialog = true }
                        )

                        // Language Selection
                        SettingNavRow(
                            icon = Icons.Default.Language,
                            title = "Language",
                            value = settings.selectedLanguage,
                            onClick = { showLanguageDialog = true }
                        )

                        // Gemini API Key Configuration
                        SettingNavRow(
                            icon = Icons.Default.Key,
                            title = "Gemini AI Key",
                            value = if (settings.customApiKey.isNotBlank()) "Configured" else "Auto / Injected",
                            onClick = { showApiKeyDialog = true }
                        )
                    }
                }
            }

            // Permissions Section
            item {
                Text(
                    text = "Permissions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZoyaTextPrimary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PermissionStatusRow(
                            icon = Icons.Default.Mic,
                            name = "Microphone",
                            description = "Required for voice assistant",
                            isGranted = settings.isMicPermissionGranted,
                            onClick = onRequestPermissions
                        )

                        PermissionStatusRow(
                            icon = Icons.Default.Notifications,
                            name = "Notifications",
                            description = "For background listening",
                            isGranted = settings.isNotificationPermissionGranted,
                            onClick = onRequestPermissions
                        )

                        PermissionStatusRow(
                            icon = Icons.Default.Accessibility,
                            name = "Accessibility",
                            description = "For advanced device actions",
                            isGranted = settings.isAccessibilityGranted,
                            isOptional = true,
                            onClick = {}
                        )
                    }
                }
            }

            // Appearance Section
            item {
                Text(
                    text = "Appearance",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZoyaTextPrimary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        SettingNavRow(
                            icon = Icons.Default.Palette,
                            title = "Theme",
                            value = settings.themeName,
                            onClick = {}
                        )
                    }
                }
            }

            // About Section
            item {
                Text(
                    text = "About",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZoyaTextPrimary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        SettingNavRow(
                            icon = Icons.Default.Info,
                            title = "Version",
                            value = "1.0.0",
                            onClick = {}
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }

    // API Key Dialog
    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("Gemini API Key") },
            text = {
                Column {
                    Text(
                        "AI Studio injects keys via the Secrets panel. You can also provide an override API key below:",
                        fontSize = 13.sp,
                        color = ZoyaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempApiKey,
                        onValueChange = { tempApiKey = it },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onApiKeySaved(tempApiKey)
                        showApiKeyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZoyaPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        val langs = listOf(
            "Auto detect (Hindi, Bengali, English, Hinglish)",
            "Hindi (हिन्दी)",
            "Bengali (বাংলা)",
            "English (India)",
            "Hinglish"
        )
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Spoken Language") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    langs.forEach { lang ->
                        Text(
                            text = lang,
                            fontSize = 14.sp,
                            color = if (settings.selectedLanguage == lang) ZoyaPrimary else ZoyaTextPrimary,
                            fontWeight = if (settings.selectedLanguage == lang) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageSelected(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Voice Dialog
    if (showVoiceDialog) {
        val voices = listOf(
            "Zoya (Female) - Warm & Confident",
            "Zoya Expressive - High Pitch",
            "Zoya Soft - Gentle Indian Tone"
        )
        AlertDialog(
            onDismissRequest = { showVoiceDialog = false },
            title = { Text("Select Assistant Voice") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    voices.forEach { voice ->
                        Text(
                            text = voice,
                            fontSize = 14.sp,
                            color = if (settings.assistantVoice.contains(voice.split(" ")[0])) ZoyaPrimary else ZoyaTextPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onVoiceSelected(voice)
                                    showVoiceDialog = false
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVoiceDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingNavRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            SettingIconCircle(icon = icon)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZoyaTextPrimary
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = ZoyaTextSecondary,
                    maxLines = 1
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Open",
            tint = Color(0xFFA59DA9),
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
fun PermissionStatusRow(
    icon: ImageVector,
    name: String,
    description: String,
    isGranted: Boolean,
    isOptional: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            SettingIconCircle(icon = icon)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = if (isOptional) "$name (Optional)" else name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZoyaTextPrimary
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = ZoyaTextSecondary
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isGranted) "Granted" else "Not granted",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isGranted) ZoyaStatusGreen else Color.Gray
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isGranted) ZoyaStatusGreen else Color.Gray)
            )
        }
    }
}

@Composable
fun SettingIconCircle(icon: ImageVector) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFF2F5))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ZoyaPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}
