package com.example.data.ai

enum class AiChatRolePreset(
    val title: String,
    val modelLabel: String,
    val systemInstruction: String
) {
    GENERAL_COACH(
        title = "Pelatih Keuangan",
        modelLabel = "Mode Umum",
        systemInstruction = "Kamu adalah asisten keuangan pelajar FinSmart Edu yang ramah, praktis, dan membantu menganalisis pengeluaran bulanan dalam Rupiah."
    ),
    DEEP_ANALYST(
        title = "Analisis Mendalam",
        modelLabel = "Mode Analisis",
        systemInstruction = "Kamu adalah analis anggaran pendidikan yang mengevaluasi rasio tabungan, kebocoran pengeluaran non-esensial, dan strategi target jangka panjang."
    ),
    QUICK_TIPS(
        title = "Tips Kilat",
        modelLabel = "Mode Cepat",
        systemInstruction = "Berikan tips penghematan harian singkat dan langsung dapat diterapkan oleh siswa atau mahasiswa."
    )
}

enum class ImageSizeOption(val label: String, val pixelsLabel: String) {
    SIZE_1K("1K", "1024 × 1024 px"),
    SIZE_2K("2K", "2048 × 2048 px"),
    SIZE_4K("4K", "4096 × 4096 px")
}

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val isPlaceholder: Boolean = false,
    val roleLabel: String = ""
)

sealed interface AiTextResult {
    data class Placeholder(val reply: String, val notice: String) : AiTextResult
}

sealed interface AiImageResult {
    data class Placeholder(
        val prompt: String,
        val size: ImageSizeOption,
        val summary: String,
        val notice: String
    ) : AiImageResult
}

interface AiRepository {
    val isLiveFirebaseConnected: Boolean

    suspend fun sendChatMessage(
        history: List<AiChatMessage>,
        userMessage: String,
        rolePreset: AiChatRolePreset,
        financialContextSummary: String
    ): AiTextResult

    suspend fun generateGoalImage(
        prompt: String,
        size: ImageSizeOption
    ): AiImageResult
}
