package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZoyaPrimary
import com.example.ui.theme.ZoyaStatusGreen
import com.example.ui.theme.ZoyaTextPrimary
import com.example.ui.theme.ZoyaTextSecondary

data class StepInfo(
    val stepIndex: Int,
    val title: String,
    val icon: ImageVector
)

val ZOYA_STEPS = listOf(
    StepInfo(0, "Understanding your request", Icons.Default.AutoAwesome),
    StepInfo(1, "Processing with Gemini", Icons.Default.Psychology),
    StepInfo(2, "Executing action", Icons.Default.Settings),
    StepInfo(3, "Speaking response", Icons.AutoMirrored.Filled.VolumeUp)
)

@Composable
fun ProcessStepItem(
    step: StepInfo,
    activeStepIndex: Int,
    modifier: Modifier = Modifier
) {
    val isCompleted = step.stepIndex < activeStepIndex
    val isCurrent = step.stepIndex == activeStepIndex
    val isPending = step.stepIndex > activeStepIndex

    val cardBg = if (isCurrent) Color.White else Color.White.copy(alpha = 0.85f)
    val textColor by animateColorAsState(
        targetValue = when {
            isCurrent -> ZoyaTextPrimary
            isCompleted -> ZoyaTextPrimary
            else -> ZoyaTextSecondary.copy(alpha = 0.6f)
        },
        label = "textColor"
    )

    GlassmorphicCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        backgroundColor = cardBg,
        borderColor = if (isCurrent) ZoyaPrimary.copy(alpha = 0.45f) else Color(0x33FFD3DE),
        elevation = if (isCurrent) 4.dp else 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Icon in circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> Color(0xFFE8F5E9)
                            isCurrent -> Color(0xFFFFE6EC)
                            else -> Color(0xFFF5F5F7)
                        }
                    )
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = when {
                        isCompleted -> ZoyaStatusGreen
                        isCurrent -> ZoyaPrimary
                        else -> Color(0xFFA59DA9)
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = step.title,
                fontSize = 15.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            // Right status mark
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = ZoyaStatusGreen,
                    modifier = Modifier.size(18.dp)
                )
            } else if (isCurrent) {
                CircularProgressIndicator(
                    strokeWidth = 2.5.dp,
                    color = ZoyaPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
