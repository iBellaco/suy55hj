package com.example.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AiQuotaConfig
import com.example.model.ProviderType
import com.example.ui.components.EditPlanDialog
import com.example.ui.components.HistoryScreen
import com.example.ui.components.LiveWidgetPreviewCard
import com.example.ui.components.QuotaGaugeCard
import com.example.ui.components.WidgetGuideScreen
import com.example.widget.AiQuotaWidgetProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotaMainScreen(
    viewModel: QuotaViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var editingConfig by remember { mutableStateOf<AiQuotaConfig?>(null) }

    LaunchedEffect(uiState.userNotificationMessage) {
        uiState.userNotificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF141518),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Quota Widget",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Gemini & ChatGPT en tiempo real",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.refreshState()
                            AiQuotaWidgetProvider.updateAllWidgets(context)
                        },
                        modifier = Modifier.testTag("btn_top_refresh")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar Todo",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF181A1F)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF181A1F),
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == QuotaTab.DASHBOARD,
                    onClick = { viewModel.selectTab(QuotaTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    label = { Text("Monitoreo", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF3B82F6),
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF)
                    )
                )

                NavigationBarItem(
                    selected = uiState.activeTab == QuotaTab.PLANS,
                    onClick = { viewModel.selectTab(QuotaTab.PLANS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Planes/API", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF10A37F),
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF)
                    )
                )

                NavigationBarItem(
                    selected = uiState.activeTab == QuotaTab.HISTORY,
                    onClick = { viewModel.selectTab(QuotaTab.HISTORY) },
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Historial", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF7C4DFF),
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF)
                    )
                )

                NavigationBarItem(
                    selected = uiState.activeTab == QuotaTab.WIDGET,
                    onClick = { viewModel.selectTab(QuotaTab.WIDGET) },
                    icon = { Icon(Icons.Default.Widgets, contentDescription = null) },
                    label = { Text("Widget", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF2563EB),
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                QuotaTab.DASHBOARD -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Live Widget Preview card
                        LiveWidgetPreviewCard(
                            geminiConfig = uiState.geminiConfig,
                            gptConfig = uiState.gptConfig,
                            syncTime = uiState.lastSyncTime,
                            onRefresh = {
                                viewModel.refreshState()
                                AiQuotaWidgetProvider.updateAllWidgets(context)
                            },
                            onAddGemini = {
                                viewModel.logUsage(ProviderType.GEMINI, 1.0, "Widget Preview")
                            },
                            onAddGpt = {
                                viewModel.logUsage(ProviderType.GPT, 1.0, "Widget Preview")
                            }
                        )

                        // Gemini Monitor Card
                        QuotaGaugeCard(
                            config = uiState.geminiConfig,
                            healthStatus = uiState.geminiHealth,
                            onLogUsage = { amount, model ->
                                viewModel.logUsage(ProviderType.GEMINI, amount, model)
                            },
                            onResetCycle = {
                                viewModel.resetCycle(ProviderType.GEMINI)
                            },
                            onOpenEdit = {
                                editingConfig = uiState.geminiConfig
                            }
                        )

                        // GPT Monitor Card
                        QuotaGaugeCard(
                            config = uiState.gptConfig,
                            healthStatus = uiState.gptHealth,
                            onLogUsage = { amount, model ->
                                viewModel.logUsage(ProviderType.GPT, amount, model)
                            },
                            onResetCycle = {
                                viewModel.resetCycle(ProviderType.GPT)
                            },
                            onOpenEdit = {
                                editingConfig = uiState.gptConfig
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                QuotaTab.PLANS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Configuración de Planes y Claves",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Personaliza tus suscripciones (ChatGPT Plus, Team, Gemini Advanced, o cuotas API prepago) para que el widget muestre el balance exacto.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9CA3AF)
                        )

                        // Quick card to configure Gemini
                        QuotaGaugeCard(
                            config = uiState.geminiConfig,
                            healthStatus = uiState.geminiHealth,
                            onLogUsage = { amt, mod -> viewModel.logUsage(ProviderType.GEMINI, amt, mod) },
                            onResetCycle = { viewModel.resetCycle(ProviderType.GEMINI) },
                            onOpenEdit = { editingConfig = uiState.geminiConfig }
                        )

                        // Quick card to configure GPT
                        QuotaGaugeCard(
                            config = uiState.gptConfig,
                            healthStatus = uiState.gptHealth,
                            onLogUsage = { amt, mod -> viewModel.logUsage(ProviderType.GPT, amt, mod) },
                            onResetCycle = { viewModel.resetCycle(ProviderType.GPT) },
                            onOpenEdit = { editingConfig = uiState.gptConfig }
                        )

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

                QuotaTab.HISTORY -> {
                    HistoryScreen(
                        logs = uiState.logs,
                        onClearLogs = { viewModel.clearHistory() }
                    )
                }

                QuotaTab.WIDGET -> {
                    WidgetGuideScreen(
                        onForceWidgetSync = {
                            viewModel.refreshState()
                            AiQuotaWidgetProvider.updateAllWidgets(context)
                        }
                    )
                }
            }
        }

        // Edit Dialog
        editingConfig?.let { config ->
            val isGemini = config.provider == ProviderType.GEMINI
            EditPlanDialog(
                config = config,
                isTestingApi = if (isGemini) uiState.isCheckingGemini else uiState.isCheckingGpt,
                onDismiss = { editingConfig = null },
                onSave = { planName, maxQuota, unit, periodMinutes, apiKey ->
                    if (isGemini) {
                        viewModel.saveGeminiConfig(planName, maxQuota, unit, periodMinutes, apiKey)
                    } else {
                        viewModel.saveGptConfig(planName, maxQuota, unit, periodMinutes, apiKey)
                    }
                },
                onTestApi = {
                    if (isGemini) {
                        viewModel.checkGeminiApi()
                    } else {
                        viewModel.checkGptApi()
                    }
                },
                onApplyPreset = { presetIndex ->
                    if (isGemini) {
                        viewModel.applyGeminiPreset(presetIndex)
                    } else {
                        viewModel.applyGptPreset(presetIndex)
                    }
                }
            )
        }
    }
}
