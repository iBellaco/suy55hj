package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiQuotaConfig

@Composable
fun LiveWidgetPreviewCard(
    geminiConfig: AiQuotaConfig,
    gptConfig: AiQuotaConfig,
    syncTime: String,
    onRefresh: () -> Unit,
    onAddGemini: () -> Unit,
    onAddGpt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_widget_preview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141518)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, Color(0xFF333742), RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            // Widget Title & Sync bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF22C55E))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VISTA PREVIA DEL WIDGET (4x2)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Act: $syncTime",
                        fontSize = 10.sp,
                        color = Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF242730))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar Widget",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Gemini Subcard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1F2229))
                    .border(1.dp, Color(0xFF2C303B), RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✦",
                            color = Color(0xFF4E82EE),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gemini",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2B3349))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${geminiConfig.percentageRemaining}% libre",
                                color = Color(0xFF93C5FD),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onAddGemini,
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF323642))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Log Gemini",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { (geminiConfig.percentageRemaining / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF4E82EE),
                        trackColor = Color(0xFF2C303B)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (geminiConfig.unit == "$") {
                                "$${String.format("%.2f", geminiConfig.remainingQuota)} / $${String.format("%.2f", geminiConfig.maxQuota)} USD"
                            } else {
                                "${geminiConfig.remainingQuota.toInt()} / ${geminiConfig.maxQuota.toInt()} ${geminiConfig.unit}"
                            },
                            color = Color(0xFF9CA3AF),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Reset: ${geminiConfig.formattedRemainingTime}",
                            color = Color(0xFF9CA3AF),
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // GPT Subcard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1F2229))
                    .border(1.dp, Color(0xFF2C303B), RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚙",
                            color = Color(0xFF10A37F),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ChatGPT (Plus)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1C3A30))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${gptConfig.percentageRemaining}% libre",
                                color = Color(0xFF6EE7B7),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onAddGpt,
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF323642))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Log GPT",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { (gptConfig.percentageRemaining / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF10A37F),
                        trackColor = Color(0xFF2C303B)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (gptConfig.unit == "$") {
                                "$${String.format("%.2f", gptConfig.remainingQuota)} / $${String.format("%.2f", gptConfig.maxQuota)} USD"
                            } else {
                                "${gptConfig.remainingQuota.toInt()} / ${gptConfig.maxQuota.toInt()} ${gptConfig.unit}"
                            },
                            color = Color(0xFF9CA3AF),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Reset: ${gptConfig.formattedRemainingTime}",
                            color = Color(0xFF9CA3AF),
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
