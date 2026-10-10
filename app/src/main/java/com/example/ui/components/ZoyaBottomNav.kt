package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary

@Composable
fun ZoyaBottomNav(
    currentScreen: String,
    onTabSelected: (String) -> Unit,
    onMicClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // Nav bar container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .background(Color.White.copy(alpha = 0.94f))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentScreen == "home",
                    onClick = { onTabSelected("home") }
                )
                NavItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    label = "Chats",
                    isSelected = currentScreen == "chats",
                    onClick = { onTabSelected("chats") }
                )

                // Placeholder space for floating center mic orb
                Spacer(modifier = Modifier.size(54.dp))

                NavItem(
                    icon = Icons.Default.GridView,
                    label = "Tools",
                    isSelected = currentScreen == "commands",
                    onClick = { onTabSelected("commands") }
                )
                NavItem(
                    icon = Icons.Default.PersonOutline,
                    label = "Profile",
                    isSelected = currentScreen == "settings",
                    onClick = { onTabSelected("settings") }
                )
            }
        }

        // Center Floating Glowing Mic Orb
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .size(58.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = ZoyaPrimary,
                    spotColor = ZoyaPrimary
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF527A),
                            Color(0xFFFF2E63),
                            Color(0xFFE02456)
                        )
                    )
                )
                .clickable { onMicClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Speak to Zoya",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ZoyaPrimary else ZoyaTextSecondary.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) ZoyaPrimary else ZoyaTextSecondary.copy(alpha = 0.7f)
        )
    }
}
