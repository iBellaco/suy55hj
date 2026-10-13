package com.example.network

import com.example.model.ApiHealthStatus
import com.example.model.ProviderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class AiApiChecker {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    suspend fun checkApi(provider: ProviderType, apiKey: String): ApiHealthStatus = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext ApiHealthStatus(
                isValid = false,
                latencyMs = 0,
                message = "Clave API no configurada (Modo simulación / contador local activo)",
                checkedAt = System.currentTimeMillis()
            )
        }

        val startTime = System.currentTimeMillis()
        try {
            val request = when (provider) {
                ProviderType.GEMINI -> {
                    Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey")
                        .get()
                        .build()
                }
                ProviderType.GPT -> {
                    Request.Builder()
                        .url("https://api.openai.com/v1/models")
                        .addHeader("Authorization", "Bearer $apiKey")
                        .get()
                        .build()
                }
            }

            client.newCall(request).execute().use { response ->
                val latency = System.currentTimeMillis() - startTime
                if (response.isSuccessful) {
                    ApiHealthStatus(
                        isValid = true,
                        latencyMs = latency,
                        message = "Conexión exitosa (${latency}ms) - Clave válida y cuota activa",
                        checkedAt = System.currentTimeMillis()
                    )
                } else {
                    val code = response.code
                    val errorMsg = when (code) {
                        401 -> "Error 401: Clave API no autorizada o inválida"
                        429 -> "Error 429: Cuota o límite de tasa excedido en el proveedor"
                        403 -> "Error 403: Permisos denegados para esta API"
                        else -> "Error HTTP $code recibido del servidor"
                    }
                    ApiHealthStatus(
                        isValid = false,
                        latencyMs = latency,
                        message = errorMsg,
                        checkedAt = System.currentTimeMillis()
                    )
                }
            }
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            ApiHealthStatus(
                isValid = false,
                latencyMs = latency,
                message = "Fallo de conexión: ${e.localizedMessage ?: "Comprueba tu conexión a internet"}",
                checkedAt = System.currentTimeMillis()
            )
        }
    }
}
