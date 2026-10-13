package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.example.MainActivity
import com.example.R
import com.example.data.QuotaRepository
import com.example.model.ProviderType

class AiQuotaWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.ACTION_REFRESH_WIDGET"
        const val ACTION_LOG_GEMINI = "com.example.ACTION_LOG_GEMINI"
        const val ACTION_LOG_GPT = "com.example.ACTION_LOG_GPT"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, AiQuotaWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, AiQuotaWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val repository = QuotaRepository(context)
        repository.checkAndApplyAutoReset()

        val gemini = repository.getGeminiConfig()
        val gpt = repository.getGptConfig()
        val syncTime = repository.getLastSyncTimeString()

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_ai_quota)

            // Header info
            views.setTextViewText(R.id.widget_last_sync_text, "Act: $syncTime")

            // Gemini card
            views.setTextViewText(
                R.id.widget_gemini_percent,
                "${gemini.percentageRemaining}% libre"
            )
            views.setProgressBar(
                R.id.widget_gemini_progress,
                100,
                gemini.percentageRemaining,
                false
            )
            val geminiUsageFormatted = if (gemini.unit == "$") {
                val rem = String.format("%.2f", gemini.remainingQuota)
                val max = String.format("%.2f", gemini.maxQuota)
                "$$rem / $$max USD"
            } else {
                "${gemini.remainingQuota.toInt()} / ${gemini.maxQuota.toInt()} ${gemini.unit}"
            }
            views.setTextViewText(R.id.widget_gemini_usage, geminiUsageFormatted)
            views.setTextViewText(
                R.id.widget_gemini_reset,
                "Reset: ${gemini.formattedRemainingTime}"
            )

            // GPT card
            views.setTextViewText(
                R.id.widget_gpt_percent,
                "${gpt.percentageRemaining}% libre"
            )
            views.setProgressBar(
                R.id.widget_gpt_progress,
                100,
                gpt.percentageRemaining,
                false
            )
            val gptUsageFormatted = if (gpt.unit == "$") {
                val rem = String.format("%.2f", gpt.remainingQuota)
                val max = String.format("%.2f", gpt.maxQuota)
                "$$rem / $$max USD"
            } else {
                "${gpt.remainingQuota.toInt()} / ${gpt.maxQuota.toInt()} ${gpt.unit}"
            }
            views.setTextViewText(R.id.widget_gpt_usage, gptUsageFormatted)
            views.setTextViewText(
                R.id.widget_gpt_reset,
                "Reset: ${gpt.formattedRemainingTime}"
            )

            // Pending Intent: Open App when tapping root or cards
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_gemini_card, openAppPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_gpt_card, openAppPendingIntent)

            // Pending Intent: Refresh button
            val refreshIntent = Intent(context, AiQuotaWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)

            // Pending Intent: Quick +1 Gemini log
            val addGeminiIntent = Intent(context, AiQuotaWidgetProvider::class.java).apply {
                action = ACTION_LOG_GEMINI
            }
            val addGeminiPendingIntent = PendingIntent.getBroadcast(
                context,
                2,
                addGeminiIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_add_gemini, addGeminiPendingIntent)

            // Pending Intent: Quick +1 GPT log
            val addGptIntent = Intent(context, AiQuotaWidgetProvider::class.java).apply {
                action = ACTION_LOG_GPT
            }
            val addGptPendingIntent = PendingIntent.getBroadcast(
                context,
                3,
                addGptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_add_gpt, addGptPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val repository = QuotaRepository(context)

        when (intent.action) {
            ACTION_REFRESH_WIDGET -> {
                repository.checkAndApplyAutoReset()
                updateAllWidgets(context)
                Toast.makeText(context, "Sincronización de cuotas actualizada", Toast.LENGTH_SHORT).show()
            }
            ACTION_LOG_GEMINI -> {
                repository.incrementUsage(ProviderType.GEMINI, 1.0, "Widget acceso rápido")
                updateAllWidgets(context)
                Toast.makeText(context, "+1 consulta Gemini registrada", Toast.LENGTH_SHORT).show()
            }
            ACTION_LOG_GPT -> {
                repository.incrementUsage(ProviderType.GPT, 1.0, "Widget acceso rápido")
                updateAllWidgets(context)
                Toast.makeText(context, "+1 mensaje ChatGPT registrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
