package com.example.ai

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiGenerateRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val tools: List<GeminiTool>? = null,
    val generationConfig: GeminiGenConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null,
    val functionCall: GeminiFunctionCall? = null,
    val functionResponse: GeminiFunctionResponse? = null
)

@JsonClass(generateAdapter = true)
data class GeminiFunctionCall(
    val name: String,
    val args: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiFunctionResponse(
    val name: String,
    val response: Map<String, Any?>
)

@JsonClass(generateAdapter = true)
data class GeminiTool(
    val functionDeclarations: List<GeminiFunctionDecl>
)

@JsonClass(generateAdapter = true)
data class GeminiFunctionDecl(
    val name: String,
    val description: String,
    val parameters: GeminiParameters? = null
)

@JsonClass(generateAdapter = true)
data class GeminiParameters(
    val type: String = "OBJECT",
    val properties: Map<String, GeminiSchemaProperty>,
    val required: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiSchemaProperty(
    val type: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenConfig(
    val temperature: Float? = 0.7f,
    val maxOutputTokens: Int? = 512
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateResponse(
    val candidates: List<GeminiCandidate>? = null,
    val promptFeedback: Any? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)
