package com.example.ai

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

sealed class GeminiResponseResult {
    data class TextResponse(val text: String) : GeminiResponseResult()
    data class ToolCall(
        val functionName: String,
        val arguments: Map<String, Any?>
    ) : GeminiResponseResult()
    data class Failure(val errorMessage: String) : GeminiResponseResult()
}

class GeminiService {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val toolsList = listOf(
        GeminiTool(
            functionDeclarations = listOf(
                GeminiFunctionDecl(
                    name = "search_youtube",
                    description = "Search for a video or play music on YouTube",
                    parameters = GeminiParameters(
                        properties = mapOf(
                            "query" to GeminiSchemaProperty("STRING", "Song title, artist name, or search query")
                        ),
                        required = listOf("query")
                    )
                ),
                GeminiFunctionDecl(
                    name = "open_supported_app",
                    description = "Open an allowlisted Android app (e.g., youtube, whatsapp, instagram, chrome, calculator, settings, camera)",
                    parameters = GeminiParameters(
                        properties = mapOf(
                            "app_name" to GeminiSchemaProperty("STRING", "Name of the app: youtube, whatsapp, instagram, chrome, calculator, settings, camera")
                        ),
                        required = listOf("app_name")
                    )
                ),
                GeminiFunctionDecl(
                    name = "set_alarm",
                    description = "Set a wake-up alarm on the Android device",
                    parameters = GeminiParameters(
                        properties = mapOf(
                            "hour" to GeminiSchemaProperty("INTEGER", "Hour in 24-hour format (0-23)"),
                            "minute" to GeminiSchemaProperty("INTEGER", "Minute (0-59)"),
                            "label" to GeminiSchemaProperty("STRING", "Alarm label or message")
                        ),
                        required = listOf("hour", "minute")
                    )
                ),
                GeminiFunctionDecl(
                    name = "toggle_flashlight",
                    description = "Turn on or turn off the device camera flashlight / torch",
                    parameters = GeminiParameters(
                        properties = mapOf(
                            "enabled" to GeminiSchemaProperty("BOOLEAN", "True to turn on flashlight, False to turn off")
                        ),
                        required = listOf("enabled")
                    )
                ),
                GeminiFunctionDecl(
                    name = "search_web",
                    description = "Search Google on the web for a query",
                    parameters = GeminiParameters(
                        properties = mapOf(
                            "query" to GeminiSchemaProperty("STRING", "The search query")
                        ),
                        required = listOf("query")
                    )
                ),
                GeminiFunctionDecl(
                    name = "get_device_time",
                    description = "Get current date and time on device",
                    parameters = GeminiParameters(properties = emptyMap())
                ),
                GeminiFunctionDecl(
                    name = "stop_listening",
                    description = "Stop the assistant voice session and cease listening",
                    parameters = GeminiParameters(properties = emptyMap())
                )
            )
        )
    )

    private val systemPrompt = GeminiContent(
        parts = listOf(
            GeminiPart(
                text = "You are ZOYA AI, a friendly, confident, polite, and quick Indian native Android voice assistant. " +
                        "You fluently understand Hindi, Bengali, English, Hinglish, and mixed Bengali-English. " +
                        "When user asks you to do an action like searching or playing songs on YouTube, opening an app, " +
                        "setting an alarm, toggling flashlight, or searching the web, ALWAYS invoke the corresponding function call. " +
                        "When conversing, keep responses brief, natural, warm, and conversational for voice output."
            )
        )
    )

    suspend fun processUserSpeech(userInput: String, customApiKey: String? = null): GeminiResponseResult = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isBlank()) {
            return@withContext GeminiResponseResult.Failure("API_KEY_UNAVAILABLE")
        }

        try {
            val requestPayload = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = userInput))
                    )
                ),
                systemInstruction = systemPrompt,
                tools = toolsList,
                generationConfig = GeminiGenConfig(temperature = 0.5f, maxOutputTokens = 300)
            )

            val jsonAdapter = moshi.adapter(GeminiGenerateRequest::class.java)
            val jsonString = jsonAdapter.toJson(requestPayload)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonString.toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                return@withContext GeminiResponseResult.Failure("API call failed: ${response.code}")
            }

            val respAdapter = moshi.adapter(GeminiGenerateResponse::class.java)
            val parsed = respAdapter.fromJson(responseBody)
            val firstCandidate = parsed?.candidates?.firstOrNull()
            val parts = firstCandidate?.content?.parts

            if (parts.isNullOrEmpty()) {
                return@withContext GeminiResponseResult.Failure("Empty response from Gemini")
            }

            for (part in parts) {
                val fn = part.functionCall
                if (fn != null) {
                    return@withContext GeminiResponseResult.ToolCall(
                        functionName = fn.name,
                        arguments = fn.args ?: emptyMap()
                    )
                }
            }

            val text = parts.firstOrNull()?.text ?: "I am here to help you."
            return@withContext GeminiResponseResult.TextResponse(text)
        } catch (e: Exception) {
            return@withContext GeminiResponseResult.Failure(e.message ?: "Unknown error")
        }
    }
}
