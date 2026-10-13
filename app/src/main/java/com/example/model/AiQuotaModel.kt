package com.example.model

enum class ProviderType {
    GEMINI,
    GPT
}

enum class ResetPeriod(val minutes: Long, val label: String) {
    THREE_HOURS(180, "Cada 3 horas (ChatGPT Plus)"),
    DAILY(1440, "Diario (00:00 - Gemini Free)"),
    WEEKLY(10080, "Semanal"),
    MONTHLY(43200, "Mensual (Facturación API)")
}

data class AiQuotaConfig(
    val provider: ProviderType,
    val planName: String,
    val maxQuota: Double,
    val currentUsed: Double,
    val unit: String, // "reqs", "msgs", "$", "tokens"
    val resetPeriodMinutes: Long,
    val lastResetTimestamp: Long,
    val apiKey: String = "",
    val isAutoResetEnabled: Boolean = true
) {
    val remainingQuota: Double
        get() = (maxQuota - currentUsed).coerceAtLeast(0.0)

    val percentageRemaining: Int
        get() = if (maxQuota <= 0) 0 else ((remainingQuota / maxQuota) * 100).toInt().coerceIn(0, 100)

    val percentageUsed: Int
        get() = if (maxQuota <= 0) 0 else ((currentUsed / maxQuota) * 100).toInt().coerceIn(0, 100)

    val millisUntilReset: Long
        get() {
            val periodMillis = resetPeriodMinutes * 60 * 1000
            val elapsed = System.currentTimeMillis() - lastResetTimestamp
            return (periodMillis - elapsed).coerceAtLeast(0L)
        }

    val formattedRemainingTime: String
        get() {
            val ms = millisUntilReset
            if (ms <= 0) return "Listo / Reiniciado"
            val totalMinutes = ms / (1000 * 60)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val days = hours / 24
            return when {
                days > 0 -> "${days}d ${hours % 24}h"
                hours > 0 -> "${hours}h ${minutes}m"
                else -> "${minutes}m"
            }
        }

    val isNearLimit: Boolean
        get() = percentageRemaining <= 20

    val isExhausted: Boolean
        get() = remainingQuota <= 0.001
}

data class UsageLogEntry(
    val id: String = System.currentTimeMillis().toString(),
    val provider: ProviderType,
    val modelName: String,
    val amount: Double,
    val unit: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

data class ApiHealthStatus(
    val isValid: Boolean = false,
    val latencyMs: Long = 0,
    val message: String = "",
    val checkedAt: Long = 0
)
