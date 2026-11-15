package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class LiveGeminiAiRepository(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) : AiRepository {

    override val isLiveFirebaseConnected: Boolean = true

    override suspend fun sendChatMessage(
        history: List<AiChatMessage>,
        userMessage: String,
        rolePreset: AiChatRolePreset,
        financialContextSummary: String
    ): AiTextResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext AiTextResult.Placeholder(
                reply = "API Key Gemini belum dikonfigurasi. Silakan masukkan GEMINI_API_KEY melalui panel Secrets di AI Studio agar asisten AI dapat menjawab secara live.",
                notice = "GEMINI_API_KEY belum diatur di panel Secrets."
            )
        }

        try {
            val systemPrompt = buildString {
                appendLine(rolePreset.systemInstruction)
                appendLine()
                appendLine("Konteks Data Keuangan Pengguna Saat Ini:")
                appendLine(financialContextSummary)
                appendLine()
                append("Jawablah dalam Bahasa Indonesia yang rapi, ramah, terstruktur, dan gunakan format mata uang Rupiah (Rp) saat memberikan simulasi angka.")
            }

            val contentsArray = JSONArray()

            // Sertakan riwayat percakapan sebelumnya (kecuali sapaan awal sistem jika di posisi pertama)
            val priorMessages = history.dropLast(1).filter { it.id != "welcome" }
            for (msg in priorMessages) {
                val role = if (msg.isUser) "user" else "model"
                val partObj = JSONObject().put("text", msg.text)
                val partsArr = JSONArray().put(partObj)
                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", partsArr)
                contentsArray.put(contentObj)
            }

            // Tambahkan pesan terbaru dari user
            val latestUserPart = JSONObject().put("text", userMessage)
            val latestUserContent = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(latestUserPart))
            contentsArray.put(latestUserContent)

            val systemInstructionObj = JSONObject().put(
                "parts",
                JSONArray().put(JSONObject().put("text", systemPrompt))
            )

            val payload = JSONObject()
                .put("system_instruction", systemInstructionObj)
                .put("contents", contentsArray)
                .put(
                    "generationConfig",
                    JSONObject()
                        .put("temperature", 0.7)
                        .put("maxOutputTokens", 1024)
                )

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            val requestBody = payload.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()

            httpClient.newCall(request).execute().use { response ->
                val rawBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val errMsg = runCatching {
                        JSONObject(rawBody).optJSONObject("error")?.optString("message")
                    }.getOrNull().takeUnless { it.isNullOrBlank() }
                        ?: "HTTP ${response.code}: ${response.message}"
                    return@withContext AiTextResult.Placeholder(
                        reply = "Gagal menghubungi Gemini API ($errMsg).",
                        notice = "Error koneksi Gemini API"
                    )
                }

                val rootJson = JSONObject(rawBody)
                val candidates = rootJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val parts = firstCandidate?.optJSONObject("content")?.optJSONArray("parts")
                val extractedText = buildString {
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val t = parts.optJSONObject(i)?.optString("text").orEmpty()
                            if (t.isNotEmpty()) append(t)
                        }
                    }
                }.trim()

                if (extractedText.isNotEmpty()) {
                    AiTextResult.Placeholder(
                        reply = extractedText,
                        notice = ""
                    )
                } else {
                    AiTextResult.Placeholder(
                        reply = "Model Gemini tidak mengembalikan teks jawaban. Silakan coba pertanyaan lain.",
                        notice = ""
                    )
                }
            }
        } catch (e: Exception) {
            AiTextResult.Placeholder(
                reply = "Terjadi kendala jaringan saat menghubungi Gemini API: ${e.localizedMessage ?: "Unknown error"}",
                notice = "Gagal terhubung ke server Gemini"
            )
        }
    }

    override suspend fun generateGoalImage(
        prompt: String,
        size: ImageSizeOption
    ): AiImageResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext AiImageResult.Placeholder(
                prompt = prompt,
                size = size,
                summary = "API Key Gemini belum dikonfigurasi di panel Secrets AI Studio.",
                notice = "Masukkan GEMINI_API_KEY di panel Secrets untuk mengaktifkan fitur AI."
            )
        }

        // Gunakan Gemini 2.5 Flash untuk merumuskan konsep visual & rencana tabungan target sesuai ukuran
        try {
            val payload = JSONObject().put(
                "contents",
                JSONArray().put(
                    JSONObject()
                        .put("role", "user")
                        .put(
                            "parts",
                            JSONArray().put(
                                JSONObject().put(
                                    "text",
                                    "Buatkan deskripsi visual inspiratif singkat (3 kalimat) dan langkah menabung praktis untuk mencapai target impian berikut: \"$prompt\" (Resolusi kanvas target: ${size.label} / ${size.pixelsLabel})."
                                )
                            )
                        )
                )
            )
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
                .addHeader("Content-Type", "application/json")
                .build()

            httpClient.newCall(request).execute().use { response ->
                val rawBody = response.body?.string().orEmpty()
                val text = if (response.isSuccessful) {
                    runCatching {
                        JSONObject(rawBody)
                            .optJSONArray("candidates")
                            ?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text")
                    }.getOrNull()
                } else null

                AiImageResult.Placeholder(
                    prompt = prompt,
                    size = size,
                    summary = text?.takeIf { it.isNotBlank() }
                        ?: "Target Visual (${size.label}): \"$prompt\"",
                    notice = "Dihasilkan secara kontekstual oleh Gemini ($MODEL_NAME) • Resolusi ${size.label} (${size.pixelsLabel})"
                )
            }
        } catch (e: Exception) {
            AiImageResult.Placeholder(
                prompt = prompt,
                size = size,
                summary = "Target Visual (${size.label}): \"$prompt\"",
                notice = "Gagal memuat deskripsi Gemini: ${e.localizedMessage ?: "Error"}"
            )
        }
    }

    companion object {
        private const val MODEL_NAME = "gemini-2.5-flash"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
