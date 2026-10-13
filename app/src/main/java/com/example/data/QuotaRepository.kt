package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AiQuotaConfig
import com.example.model.ProviderType
import com.example.model.ResetPeriod
import com.example.model.UsageLogEntry
import com.example.widget.AiQuotaWidgetProvider
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuotaRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ai_quota_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_GEMINI_PLAN = "gemini_plan"
        private const val KEY_GEMINI_MAX = "gemini_max"
        private const val KEY_GEMINI_USED = "gemini_used"
        private const val KEY_GEMINI_UNIT = "gemini_unit"
        private const val KEY_GEMINI_PERIOD = "gemini_period"
        private const val KEY_GEMINI_LAST_RESET = "gemini_last_reset"
        private const val KEY_GEMINI_KEY = "gemini_api_key"

        private const val KEY_GPT_PLAN = "gpt_plan"
        private const val KEY_GPT_MAX = "gpt_max"
        private const val KEY_GPT_USED = "gpt_used"
        private const val KEY_GPT_UNIT = "gpt_unit"
        private const val KEY_GPT_PERIOD = "gpt_period"
        private const val KEY_GPT_LAST_RESET = "gpt_last_reset"
        private const val KEY_GPT_KEY = "gpt_api_key"

        private const val KEY_USAGE_LOGS = "usage_logs_json"
        private const val KEY_LAST_SYNC_TIME = "last_sync_timestamp"
    }

    init {
        // Initialize defaults if first launch
        if (!prefs.contains(KEY_GEMINI_PLAN)) {
            val now = System.currentTimeMillis()
            prefs.edit().apply {
                // Gemini Free Tier: 1500 req/day
                putString(KEY_GEMINI_PLAN, "Gemini Free (1,500 req/día)")
                putString(KEY_GEMINI_MAX, "1500.0")
                putString(KEY_GEMINI_USED, "320.0")
                putString(KEY_GEMINI_UNIT, "reqs")
                putLong(KEY_GEMINI_PERIOD, ResetPeriod.DAILY.minutes)
                putLong(KEY_GEMINI_LAST_RESET, now)
                putString(KEY_GEMINI_KEY, "")

                // ChatGPT Plus Subscription: 40 msgs / 3 hours
                putString(KEY_GPT_PLAN, "ChatGPT Plus (40 msgs / 3h)")
                putString(KEY_GPT_MAX, "40.0")
                putString(KEY_GPT_USED, "14.0")
                putString(KEY_GPT_UNIT, "msgs")
                putLong(KEY_GPT_PERIOD, ResetPeriod.THREE_HOURS.minutes)
                putLong(KEY_GPT_LAST_RESET, now)
                putString(KEY_GPT_KEY, "")

                putLong(KEY_LAST_SYNC_TIME, now)
                apply()
            }
        }
    }

    fun getGeminiConfig(): AiQuotaConfig {
        checkAndApplyAutoResetInternal(ProviderType.GEMINI)
        return AiQuotaConfig(
            provider = ProviderType.GEMINI,
            planName = prefs.getString(KEY_GEMINI_PLAN, "Gemini Free") ?: "Gemini Free",
            maxQuota = prefs.getString(KEY_GEMINI_MAX, "1500.0")?.toDoubleOrNull() ?: 1500.0,
            currentUsed = prefs.getString(KEY_GEMINI_USED, "0.0")?.toDoubleOrNull() ?: 0.0,
            unit = prefs.getString(KEY_GEMINI_UNIT, "reqs") ?: "reqs",
            resetPeriodMinutes = prefs.getLong(KEY_GEMINI_PERIOD, ResetPeriod.DAILY.minutes),
            lastResetTimestamp = prefs.getLong(KEY_GEMINI_LAST_RESET, System.currentTimeMillis()),
            apiKey = prefs.getString(KEY_GEMINI_KEY, "") ?: ""
        )
    }

    fun getGptConfig(): AiQuotaConfig {
        checkAndApplyAutoResetInternal(ProviderType.GPT)
        return AiQuotaConfig(
            provider = ProviderType.GPT,
            planName = prefs.getString(KEY_GPT_PLAN, "ChatGPT Plus") ?: "ChatGPT Plus",
            maxQuota = prefs.getString(KEY_GPT_MAX, "40.0")?.toDoubleOrNull() ?: 40.0,
            currentUsed = prefs.getString(KEY_GPT_USED, "0.0")?.toDoubleOrNull() ?: 0.0,
            unit = prefs.getString(KEY_GPT_UNIT, "msgs") ?: "msgs",
            resetPeriodMinutes = prefs.getLong(KEY_GPT_PERIOD, ResetPeriod.THREE_HOURS.minutes),
            lastResetTimestamp = prefs.getLong(KEY_GPT_LAST_RESET, System.currentTimeMillis()),
            apiKey = prefs.getString(KEY_GPT_KEY, "") ?: ""
        )
    }

    fun saveGeminiConfig(config: AiQuotaConfig) {
        prefs.edit().apply {
            putString(KEY_GEMINI_PLAN, config.planName)
            putString(KEY_GEMINI_MAX, config.maxQuota.toString())
            putString(KEY_GEMINI_USED, config.currentUsed.toString())
            putString(KEY_GEMINI_UNIT, config.unit)
            putLong(KEY_GEMINI_PERIOD, config.resetPeriodMinutes)
            putLong(KEY_GEMINI_LAST_RESET, config.lastResetTimestamp)
            putString(KEY_GEMINI_KEY, config.apiKey)
            putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
            apply()
        }
        notifyWidgetUpdate()
    }

    fun saveGptConfig(config: AiQuotaConfig) {
        prefs.edit().apply {
            putString(KEY_GPT_PLAN, config.planName)
            putString(KEY_GPT_MAX, config.maxQuota.toString())
            putString(KEY_GPT_USED, config.currentUsed.toString())
            putString(KEY_GPT_UNIT, config.unit)
            putLong(KEY_GPT_PERIOD, config.resetPeriodMinutes)
            putLong(KEY_GPT_LAST_RESET, config.lastResetTimestamp)
            putString(KEY_GPT_KEY, config.apiKey)
            putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
            apply()
        }
        notifyWidgetUpdate()
    }

    fun incrementUsage(provider: ProviderType, amount: Double = 1.0, modelName: String = "") {
        when (provider) {
            ProviderType.GEMINI -> {
                val current = prefs.getString(KEY_GEMINI_USED, "0.0")?.toDoubleOrNull() ?: 0.0
                val max = prefs.getString(KEY_GEMINI_MAX, "1500.0")?.toDoubleOrNull() ?: 1500.0
                val unit = prefs.getString(KEY_GEMINI_UNIT, "reqs") ?: "reqs"
                val newUsed = (current + amount).coerceAtMost(max)
                prefs.edit().putString(KEY_GEMINI_USED, newUsed.toString())
                    .putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
                    .apply()

                addLog(UsageLogEntry(
                    provider = ProviderType.GEMINI,
                    modelName = modelName.ifEmpty { "Gemini 2.5 Flash" },
                    amount = amount,
                    unit = unit,
                    note = "+$amount $unit consumidos"
                ))
            }
            ProviderType.GPT -> {
                val current = prefs.getString(KEY_GPT_USED, "0.0")?.toDoubleOrNull() ?: 0.0
                val max = prefs.getString(KEY_GPT_MAX, "40.0")?.toDoubleOrNull() ?: 40.0
                val unit = prefs.getString(KEY_GPT_UNIT, "msgs") ?: "msgs"
                val newUsed = (current + amount).coerceAtMost(max)
                prefs.edit().putString(KEY_GPT_USED, newUsed.toString())
                    .putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
                    .apply()

                addLog(UsageLogEntry(
                    provider = ProviderType.GPT,
                    modelName = modelName.ifEmpty { "GPT-4o" },
                    amount = amount,
                    unit = unit,
                    note = "+$amount $unit consumidos"
                ))
            }
        }
        notifyWidgetUpdate()
    }

    fun resetUsage(provider: ProviderType) {
        val now = System.currentTimeMillis()
        when (provider) {
            ProviderType.GEMINI -> {
                prefs.edit()
                    .putString(KEY_GEMINI_USED, "0.0")
                    .putLong(KEY_GEMINI_LAST_RESET, now)
                    .putLong(KEY_LAST_SYNC_TIME, now)
                    .apply()
            }
            ProviderType.GPT -> {
                prefs.edit()
                    .putString(KEY_GPT_USED, "0.0")
                    .putLong(KEY_GPT_LAST_RESET, now)
                    .putLong(KEY_LAST_SYNC_TIME, now)
                    .apply()
            }
        }
        notifyWidgetUpdate()
    }

    fun checkAndApplyAutoReset(): Boolean {
        val geminiReset = checkAndApplyAutoResetInternal(ProviderType.GEMINI)
        val gptReset = checkAndApplyAutoResetInternal(ProviderType.GPT)
        if (geminiReset || gptReset) {
            notifyWidgetUpdate()
            return true
        }
        return false
    }

    private fun checkAndApplyAutoResetInternal(provider: ProviderType): Boolean {
        val now = System.currentTimeMillis()
        val periodKey = if (provider == ProviderType.GEMINI) KEY_GEMINI_PERIOD else KEY_GPT_PERIOD
        val resetKey = if (provider == ProviderType.GEMINI) KEY_GEMINI_LAST_RESET else KEY_GPT_LAST_RESET
        val usedKey = if (provider == ProviderType.GEMINI) KEY_GEMINI_USED else KEY_GPT_USED

        val periodMinutes = prefs.getLong(periodKey, 180)
        val lastReset = prefs.getLong(resetKey, now)
        val elapsed = now - lastReset
        val cycleDurationMillis = periodMinutes * 60 * 1000

        if (elapsed >= cycleDurationMillis && cycleDurationMillis > 0) {
            // Quota cycle expired, auto-reset
            prefs.edit()
                .putString(usedKey, "0.0")
                .putLong(resetKey, now)
                .putLong(KEY_LAST_SYNC_TIME, now)
                .apply()
            return true
        }
        return false
    }

    fun getLastSyncTimeString(): String {
        val time = prefs.getLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun addLog(entry: UsageLogEntry) {
        val list = getRecentLogs().toMutableList()
        list.add(0, entry)
        if (list.size > 50) {
            list.subList(50, list.size).clear()
        }
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("provider", item.provider.name)
                put("modelName", item.modelName)
                put("amount", item.amount)
                put("unit", item.unit)
                put("timestamp", item.timestamp)
                put("note", item.note)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_USAGE_LOGS, jsonArray.toString()).apply()
    }

    fun getRecentLogs(): List<UsageLogEntry> {
        val jsonStr = prefs.getString(KEY_USAGE_LOGS, null) ?: return emptyList()
        val list = mutableListOf<UsageLogEntry>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    UsageLogEntry(
                        id = obj.optString("id", i.toString()),
                        provider = ProviderType.valueOf(obj.optString("provider", "GEMINI")),
                        modelName = obj.optString("modelName", ""),
                        amount = obj.optDouble("amount", 1.0),
                        unit = obj.optString("unit", "reqs"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun clearLogs() {
        prefs.edit().remove(KEY_USAGE_LOGS).apply()
    }

    private fun notifyWidgetUpdate() {
        AiQuotaWidgetProvider.updateAllWidgets(context)
    }
}
