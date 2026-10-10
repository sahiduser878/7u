package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary

@Composable
fun ZoyaTopBar(
    showBackButton: Boolean = false,
    showCloseButton: Boolean = false,
    title: String? = null,
    showSettings: Boolean = true,
    showWaveIndicator: Boolean = false,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Icon
        if (showCloseButton) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = ZoyaTextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        } else if (showBackButton) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZoyaTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            IconButton(onClick = {}, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = ZoyaTextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Center Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (title != null) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZoyaTextPrimary
                )
            } else {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = ZoyaTextPrimary, fontWeight = FontWeight.Bold)) {
                            append("Zoya ")
                        }
                        withStyle(SpanStyle(color = ZoyaPrimary, fontWeight = FontWeight.Bold)) {
                            append("AI")
                        }
                    },
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Your Personal Voice Assistant",
                    fontSize = 12.sp,
                    color = ZoyaTextSecondary
                )
            }
        }

        // Right Icon
        if (showWaveIndicator) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Audio Wave Active",
                    tint = ZoyaPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        } else if (showSettings) {
            IconButton(onClick = onSettingsClick, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = ZoyaTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}
