package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiQuotaConfig
import com.example.model.ProviderType
import com.example.model.ResetPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlanDialog(
    config: AiQuotaConfig,
    isTestingApi: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        planName: String,
        maxQuota: Double,
        unit: String,
        resetPeriodMinutes: Long,
        apiKey: String
    ) -> Unit,
    onTestApi: () -> Unit,
    onApplyPreset: (index: Int) -> Unit
) {
    val isGemini = config.provider == ProviderType.GEMINI
    val primaryColor = if (isGemini) Color(0xFF4E82EE) else Color(0xFF10A37F)

    var planName by remember { mutableStateOf(config.planName) }
    var maxQuotaStr by remember { mutableStateOf(config.maxQuota.toString()) }
    var unit by remember { mutableStateOf(config.unit) }
    var apiKey by remember { mutableStateOf(config.apiKey) }
    var selectedPeriod by remember {
        mutableStateOf(
            ResetPeriod.entries.firstOrNull { it.minutes == config.resetPeriodMinutes }
                ?: ResetPeriod.DAILY
        )
    }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Configurar Cuota: ${if (isGemini) "Gemini" else "ChatGPT"}",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Presets bar
                Text(
                    text = "Ajustes rápidos predefinidos:",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isGemini) {
                        OutlinedButton(
                            onClick = {
                                onApplyPreset(0)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Gratis 1.5K", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                onApplyPreset(1)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Advanced", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                onApplyPreset(2)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("API $20", fontSize = 11.sp)
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                onApplyPreset(0)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Plus 40/3h", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                onApplyPreset(1)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Team 100", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                onApplyPreset(2)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("API $15", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Plan Name
                OutlinedTextField(
                    value = planName,
                    onValueChange = { planName = it },
                    label = { Text("Nombre del plan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Max Quota & Unit
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = maxQuotaStr,
                        onValueChange = { maxQuotaStr = it },
                        label = { Text("Límite / Cuota") },
                        singleLine = true,
                        modifier = Modifier.weight(1.3f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidad") },
                        placeholder = { Text("reqs, msgs, $") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reset period selector
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedPeriod.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ciclo de reinicio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        ResetPeriod.entries.forEach { period ->
                            DropdownMenuItem(
                                text = { Text(period.label) },
                                onClick = {
                                    selectedPeriod = period
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // API Key field
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("Clave API (Opcional)") },
                    placeholder = {
                        Text(if (isGemini) "AIzaSy..." else "sk-...")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Test API Button
                OutlinedButton(
                    onClick = onTestApi,
                    enabled = !isTestingApi,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_test_api_connection"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                ) {
                    if (isTestingApi) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .width(16.dp)
                                .height(16.dp),
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verificando API en vivo...")
                    } else {
                        Text("Probar conexión de API en vivo")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val maxVal = maxQuotaStr.toDoubleOrNull() ?: config.maxQuota
                    onSave(planName, maxVal, unit, selectedPeriod.minutes, apiKey)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                modifier = Modifier.testTag("btn_save_plan_config")
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF9CA3AF))
            }
        },
        containerColor = Color(0xFF22242B)
    )
}
