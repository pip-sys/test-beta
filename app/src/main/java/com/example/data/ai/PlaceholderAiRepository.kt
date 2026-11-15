package com.example.data.ai

import kotlinx.coroutines.delay

class PlaceholderAiRepository : AiRepository {
    override val isLiveFirebaseConnected: Boolean = false

    override suspend fun sendChatMessage(
        history: List<AiChatMessage>,
        userMessage: String,
        rolePreset: AiChatRolePreset,
        financialContextSummary: String
    ): AiTextResult {
        delay(450L)
        val lower = userMessage.lowercase()
        val contextualTip = when {
            lower.contains("hemat") || lower.contains("potong") || lower.contains("boros") -> {
                "Berdasarkan ringkasan datamu ($financialContextSummary), coba terapkan aturan 24 jam sebelum checkout belanja online dan batasi jajan/nongkrong maksimal 2 kali seminggu."
            }
            lower.contains("tabung") || lower.contains("saldo") || lower.contains("target") -> {
                "Dengan kondisi saat ini ($financialContextSummary), sisihkan minimal 20% uang saku di awal bulan ke rekening atau celengan terpisah sebelum dipakai belanja."
            }
            else -> {
                "Ringkasan konteks bulan ini: $financialContextSummary. Gunakan tab Saran Hemat untuk menggeser slider 10%-50% dan lihat simulasi tabunganmu bulan depan."
            }
        }

        val sampleReply = buildString {
            appendLine("[Respons Contoh — ${rolePreset.title} (${rolePreset.modelLabel})]")
            appendLine()
            appendLine("Pertanyaanmu: \"$userMessage\"")
            appendLine()
            appendLine(contextualTip)
            appendLine()
            append("Catatan: Respons ini adalah contoh lokal karena Firebase AI Logic belum diaktifkan.")
        }

        return AiTextResult.Placeholder(
            reply = sampleReply,
            notice = "Memerlukan Firebase: Aktifkan Firebase AI Logic untuk terhubung ke Gemini secara langsung."
        )
    }

    override suspend fun generateGoalImage(
        prompt: String,
        size: ImageSizeOption
    ): AiImageResult {
        delay(500L)
        return AiImageResult.Placeholder(
            prompt = prompt,
            size = size,
            summary = "[Pratinjau Contoh ${size.label} (${size.pixelsLabel})]\nTarget Visual: \"$prompt\"\n\nPembuatan gambar AI beresolusi ${size.label} dengan model Gemini Image memerlukan Firebase AI Logic yang aktif.",
            notice = "Memerlukan Firebase: Aktifkan Firebase AI Logic untuk menghasilkan gambar resolusi ${size.label} secara langsung."
        )
    }
}
