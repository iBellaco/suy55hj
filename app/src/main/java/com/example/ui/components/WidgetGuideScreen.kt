package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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

@Composable
fun WidgetGuideScreen(
    onForceWidgetSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("widget_guide_screen")
            .padding(16.dp)
    ) {
        // Hero Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3B82F6).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Widgets,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Widget de Pantalla de Inicio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Monitoreo constante sin abrir la app",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Steps Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF22242B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "¿Cómo añadir el widget a tu pantalla?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(14.dp))

                GuideStepItem(
                    stepNumber = "1",
                    title = "Mantén pulsada la pantalla de inicio",
                    description = "Ve al escritorio de tu Android y presiona en cualquier espacio vacío."
                )
                Spacer(modifier = Modifier.height(10.dp))

                GuideStepItem(
                    stepNumber = "2",
                    title = "Selecciona 'Widgets'",
                    description = "Toca el icono de Widgets en el menú emergente inferior."
                )
                Spacer(modifier = Modifier.height(10.dp))

                GuideStepItem(
                    stepNumber = "3",
                    title = "Busca 'AI Quota Widget'",
                    description = "Desplázate hasta encontrar 'AI Quota Monitor' y arrástralo a tu pantalla."
                )
                Spacer(modifier = Modifier.height(10.dp))

                GuideStepItem(
                    stepNumber = "4",
                    title = "Disfruta de control directo",
                    description = "Pulsa el botón '+' en el widget cada vez que uses ChatGPT o Gemini para descontar consumo al instante, o pulsa el icono de recarga para sincronizar."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Auto-reset feature explanation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2638))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF93C5FD),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reinicio Automático Inteligente",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBFDBFE),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• ChatGPT Plus: Se reinicia automáticamente cada 3 horas a las 40 respuestas.\n" +
                            "• Gemini Free: Se reinicia automáticamente cada 24 horas a las 1,500 solicitudes.\n" +
                            "• El widget calcula el tiempo transcurrido en segundo plano incluso si reinicias el dispositivo.",
                    color = Color(0xFFD1D5DB),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onForceWidgetSync,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_force_widget_sync"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sincronizar y Enviar Datos al Widget Ahora", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun GuideStepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF374151)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                lineHeight = 16.sp
            )
        }
    }
}
