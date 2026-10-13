package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiQuotaConfig
import com.example.model.ApiHealthStatus
import com.example.model.ProviderType

@Composable
fun QuotaGaugeCard(
    config: AiQuotaConfig,
    healthStatus: ApiHealthStatus,
    onLogUsage: (amount: Double, model: String) -> Unit,
    onResetCycle: () -> Unit,
    onOpenEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isGemini = config.provider == ProviderType.GEMINI
    val primaryColor = if (isGemini) Color(0xFF4E82EE) else Color(0xFF10A37F)
    val secondaryColor = if (isGemini) Color(0xFF7C4DFF) else Color(0xFF0D8A6C)
    val surfaceGradient = Brush.linearGradient(
        colors = listOf(
            if (isGemini) Color(0xFF192238) else Color(0xFF142921),
            Color(0xFF1B1D22)
        )
    )

    val progressAnimated by animateFloatAsState(
        targetValue = (config.percentageRemaining / 100f).coerceIn(0f, 1f),
        label = "progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(if (isGemini) "gemini_quota_card" else "gpt_quota_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22242B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(surfaceGradient)
                .padding(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isGemini) "✦" else "⚙",
                            color = primaryColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isGemini) "Google Gemini" else "ChatGPT (OpenAI)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = config.planName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                // Percentage remaining badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                config.percentageRemaining > 40 -> primaryColor.copy(alpha = 0.2f)
                                config.percentageRemaining > 15 -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${config.percentageRemaining}% libre",
                        color = when {
                            config.percentageRemaining > 40 -> primaryColor
                            config.percentageRemaining > 15 -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Saldo restante",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )
                    Text(
                        text = if (config.unit == "$") {
                            "$${String.format("%.2f", config.remainingQuota)} USD"
                        } else {
                            "${config.remainingQuota.toInt()} ${config.unit}"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Consumido: " + if (config.unit == "$") {
                            "$${String.format("%.2f", config.currentUsed)} / $${String.format("%.2f", config.maxQuota)}"
                        } else {
                            "${config.currentUsed.toInt()} / ${config.maxQuota.toInt()} ${config.unit}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD1D5DB)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Tiempo de reinicio",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reinicio: ${config.formattedRemainingTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progressAnimated },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = primaryColor,
                trackColor = Color(0xFF333742),
            )

            // API health tag if tested
            if (healthStatus.checkedAt > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (healthStatus.isValid) Color(0xFF064E3B).copy(alpha = 0.5f)
                            else Color(0xFF450A0A).copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (healthStatus.isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = "Estado API",
                        tint = if (healthStatus.isValid) Color(0xFF34D399) else Color(0xFFF87171),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = healthStatus.message,
                        fontSize = 11.sp,
                        color = if (healthStatus.isValid) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onLogUsage(1.0, if (isGemini) "Gemini 2.5 Flash" else "GPT-4o")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(if (isGemini) "btn_log_gemini_1" else "btn_log_gpt_1"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+1 ${if (config.unit == "$") "USD" else config.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        onLogUsage(5.0, if (isGemini) "Gemini 1.5 Pro" else "o3-mini")
                    },
                    modifier = Modifier.weight(0.7f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("+5", fontSize = 13.sp)
                }

                IconButton(
                    onClick = onResetCycle,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2C2F38))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reiniciar ciclo",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                }

                OutlinedButton(
                    onClick = onOpenEdit,
                    modifier = Modifier.weight(0.9f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF93C5FD))
                ) {
                    Text("Ajustar", fontSize = 12.sp)
                }
            }
        }
    }
}
