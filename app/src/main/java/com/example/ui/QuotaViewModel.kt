package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuotaRepository
import com.example.model.AiQuotaConfig
import com.example.model.ApiHealthStatus
import com.example.model.ProviderType
import com.example.model.ResetPeriod
import com.example.model.UsageLogEntry
import com.example.network.AiApiChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class QuotaTab(val label: String) {
    DASHBOARD("Monitoreo"),
    PLANS("Planes y API"),
    HISTORY("Historial"),
    WIDGET("Widget")
}

data class QuotaUiState(
    val geminiConfig: AiQuotaConfig,
    val gptConfig: AiQuotaConfig,
    val logs: List<UsageLogEntry> = emptyList(),
    val geminiHealth: ApiHealthStatus = ApiHealthStatus(),
    val gptHealth: ApiHealthStatus = ApiHealthStatus(),
    val isCheckingGemini: Boolean = false,
    val isCheckingGpt: Boolean = false,
    val lastSyncTime: String = "",
    val activeTab: QuotaTab = QuotaTab.DASHBOARD,
    val userNotificationMessage: String? = null
)

class QuotaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuotaRepository(application)
    private val apiChecker = AiApiChecker()

    private val _uiState = MutableStateFlow(
        QuotaUiState(
            geminiConfig = repository.getGeminiConfig(),
            gptConfig = repository.getGptConfig(),
            logs = repository.getRecentLogs(),
            lastSyncTime = repository.getLastSyncTimeString()
        )
    )
    val uiState: StateFlow<QuotaUiState> = _uiState.asStateFlow()

    init {
        refreshState()
    }

    fun selectTab(tab: QuotaTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun clearNotification() {
        _uiState.update { it.copy(userNotificationMessage = null) }
    }

    fun refreshState() {
        repository.checkAndApplyAutoReset()
        _uiState.update {
            it.copy(
                geminiConfig = repository.getGeminiConfig(),
                gptConfig = repository.getGptConfig(),
                logs = repository.getRecentLogs(),
                lastSyncTime = repository.getLastSyncTimeString()
            )
        }
    }

    fun logUsage(provider: ProviderType, amount: Double, modelName: String) {
        repository.incrementUsage(provider, amount, modelName)
        refreshState()
        _uiState.update {
            it.copy(
                userNotificationMessage = "+$amount consumido en ${if (provider == ProviderType.GEMINI) "Gemini" else "ChatGPT"}"
            )
        }
    }

    fun resetCycle(provider: ProviderType) {
        repository.resetUsage(provider)
        refreshState()
        _uiState.update {
            it.copy(
                userNotificationMessage = "Cuota reiniciada con éxito para ${if (provider == ProviderType.GEMINI) "Gemini" else "ChatGPT"}"
            )
        }
    }

    fun saveGeminiConfig(
        planName: String,
        maxQuota: Double,
        unit: String,
        resetPeriodMinutes: Long,
        apiKey: String
    ) {
        val current = repository.getGeminiConfig()
        val updated = current.copy(
            planName = planName,
            maxQuota = maxQuota,
            unit = unit,
            resetPeriodMinutes = resetPeriodMinutes,
            apiKey = apiKey
        )
        repository.saveGeminiConfig(updated)
        refreshState()
        _uiState.update { it.copy(userNotificationMessage = "Configuración de Gemini actualizada") }
    }

    fun saveGptConfig(
        planName: String,
        maxQuota: Double,
        unit: String,
        resetPeriodMinutes: Long,
        apiKey: String
    ) {
        val current = repository.getGptConfig()
        val updated = current.copy(
            planName = planName,
            maxQuota = maxQuota,
            unit = unit,
            resetPeriodMinutes = resetPeriodMinutes,
            apiKey = apiKey
        )
        repository.saveGptConfig(updated)
        refreshState()
        _uiState.update { it.copy(userNotificationMessage = "Configuración de GPT actualizada") }
    }

    fun checkGeminiApi() {
        val key = _uiState.value.geminiConfig.apiKey
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingGemini = true) }
            val result = apiChecker.checkApi(ProviderType.GEMINI, key)
            _uiState.update {
                it.copy(
                    isCheckingGemini = false,
                    geminiHealth = result,
                    userNotificationMessage = result.message
                )
            }
        }
    }

    fun checkGptApi() {
        val key = _uiState.value.gptConfig.apiKey
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingGpt = true) }
            val result = apiChecker.checkApi(ProviderType.GPT, key)
            _uiState.update {
                it.copy(
                    isCheckingGpt = false,
                    gptHealth = result,
                    userNotificationMessage = result.message
                )
            }
        }
    }

    fun applyGeminiPreset(presetIndex: Int) {
        val now = System.currentTimeMillis()
        val current = _uiState.value.geminiConfig
        val newConfig = when (presetIndex) {
            0 -> current.copy(
                planName = "Gemini Free (1,500 req/día)",
                maxQuota = 1500.0,
                unit = "reqs",
                resetPeriodMinutes = ResetPeriod.DAILY.minutes,
                currentUsed = 0.0,
                lastResetTimestamp = now
            )
            1 -> current.copy(
                planName = "Gemini Advanced (Google One)",
                maxQuota = 200.0,
                unit = "prompts",
                resetPeriodMinutes = ResetPeriod.DAILY.minutes,
                currentUsed = 0.0,
                lastResetTimestamp = now
            )
            2 -> current.copy(
                planName = "Gemini API Pay-as-you-go ($20/mes)",
                maxQuota = 20.0,
                unit = "$",
                resetPeriodMinutes = ResetPeriod.MONTHLY.minutes,
                currentUsed = 0.0,
                lastResetTimestamp = now
            )
            else -> current
        }
        repository.saveGeminiConfig(newConfig)
        refreshState()
    }

    fun applyGptPreset(presetIndex: Int) {
        val now = System.currentTimeMillis()
        val current = _uiState.value.gptConfig
        val newConfig = when (presetIndex) {
            0 -> current.copy(
                planName = "ChatGPT Plus (40 msgs / 3h)",
                maxQuota = 40.0,
                unit = "msgs",
                resetPeriodMinutes = ResetPeriod.THREE_HOURS.minutes,
                currentUsed = 0.0,
                lastResetTimestamp = now
            )
            1 -> current.copy(
                planName = "ChatGPT Team / Pro (100 msgs / 3h)",
                maxQuota = 100.0,
                unit = "msgs",
                resetPeriodMinutes = ResetPeriod.THREE_HOURS.minutes,
                currentUsed = 0.0,
                lastResetTimestamp = now
            )
            2 -> current.copy(
                planName = "OpenAI API Prepago ($15/mes)",
                maxQuota = 15.0,
                unit = "$",
                resetPeriodMinutes = ResetPeriod.MONTHLY.minutes,
                currentUsed = 0.0,
                lastResetTimestamp = now
            )
            else -> current
        }
        repository.saveGptConfig(newConfig)
        refreshState()
    }

    fun clearHistory() {
        repository.clearLogs()
        refreshState()
        _uiState.update { it.copy(userNotificationMessage = "Historial limpiado") }
    }
}
