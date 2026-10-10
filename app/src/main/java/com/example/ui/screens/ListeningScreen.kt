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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantState
import com.example.ui.components.ListeningWaveOrb
import com.example.ui.components.ProcessStepItem
import com.example.ui.components.ZOYA_STEPS
import com.example.ui.components.ZoyaTopBar
import com.example.ui.theme.ZoyaBackgroundBrush
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary

@Composable
fun ListeningScreen(
    state: AssistantState,
    onCloseClick: () -> Unit,
    onStopListeningClick: () -> Unit
) {
    val amplitude = when (state) {
        is AssistantState.Listening -> state.amplitude
        else -> 0.4f
    }

    val recognizedText = when (state) {
        is AssistantState.Listening -> state.recognizedText
        is AssistantState.Thinking -> state.query
        else -> ""
    }

    val activeStep = when (state) {
        is AssistantState.Thinking -> state.currentStep
        is AssistantState.Listening -> 0
        else -> 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZoyaBackgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Close button and Audio Wave indicator
            ZoyaTopBar(
                showCloseButton = true,
                showWaveIndicator = true,
                title = null,
                showSettings = false,
                onBackClick = onCloseClick
            )

            // Central Listening Header and Animated Orb
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = if (state is AssistantState.Thinking) "Thinking..." else "Listening...",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZoyaTextPrimary
                )

                if (recognizedText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"$recognizedText\"",
                        fontSize = 15.sp,
                        color = ZoyaPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // The Central Glowing Pink Wave Orb from the photo!
                ListeningWaveOrb(
                    amplitude = amplitude,
                    size = 210.dp
                )
            }

            // The 4 Process Step Checklist Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ZOYA_STEPS.forEach { step ->
                    ProcessStepItem(
                        step = step,
                        activeStepIndex = activeStep
                    )
                }
            }

            // Bottom "Stop Listening" Red Pill Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0xFFFF2E63))
                    .clickable { onStopListeningClick() }
                    .padding(horizontal = 28.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stop Listening",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
