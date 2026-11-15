package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiChatMessage
import com.example.data.ai.AiChatRolePreset
import com.example.data.ai.AiImageResult
import com.example.data.ai.AiRepository
import com.example.data.ai.AiTextResult
import com.example.data.ai.ImageSizeOption
import com.example.data.ai.LiveGeminiAiRepository
import com.example.data.local.FinSmartDatabase
import com.example.data.local.TransactionRepository
import com.example.domain.model.CategoryBreakdownItem
import com.example.domain.model.CategoryCatalog
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.MonthlyTrendPoint
import com.example.domain.model.SmartSavingAdvice
import com.example.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FinSmartUiState(
    val selectedYearMonth: String = "2026-11",
    val availableMonths: List<String> = DEFAULT_MONTHS_2026,
    val monthlyTransactions: List<FinancialTransaction> = emptyList(),
    val allTransactionsCount: Int = 0,
    val monthlyIncome: Long = 0L,
    val monthlyExpense: Long = 0L,
    val monthlyBalance: Long = 0L,
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val monthlyTrends: List<MonthlyTrendPoint> = emptyList(),
    val smartAdvice: SmartSavingAdvice? = null,
    val snackbarMessage: String? = null
) {
    companion object {
        val DEFAULT_MONTHS_2026 = (1..12).map { month ->
            "2026-${month.toString().padStart(2, '0')}"
        }
    }
}

sealed interface AiImageUiState {
    data object Idle : AiImageUiState
    data object Loading : AiImageUiState
    data class PlaceholderPreview(
        val prompt: String,
        val size: ImageSizeOption,
        val summary: String,
        val notice: String
    ) : AiImageUiState
}

class FinSmartViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository = TransactionRepository(
        dao = FinSmartDatabase.getInstance(application).transactionDao(),
        context = application
    )
    private val aiRepository: AiRepository = LiveGeminiAiRepository()

    private val _selectedYearMonth = MutableStateFlow("2026-11")
    private val _advisorSliderPercent = MutableStateFlow(30)
    private val _selectedAdvisorCategory = MutableStateFlow<String?>(null)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    private val _chatRole = MutableStateFlow(AiChatRolePreset.GENERAL_COACH)
    val chatRole: StateFlow<AiChatRolePreset> = _chatRole.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            AiChatMessage(
                id = "welcome",
                isUser = false,
                text = "Halo! Saya Asisten AI Keuangan FinSmart Edu (Gemini). Tanyakan strategi hemat uang jajan, evaluasi pengeluaranmu bulan ini, atau simulasi target tabunganmu!",
                isPlaceholder = false,
                roleLabel = AiChatRolePreset.GENERAL_COACH.title
            )
        )
    )
    val chatMessages: StateFlow<List<AiChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _selectedImageSize = MutableStateFlow(ImageSizeOption.SIZE_1K)
    val selectedImageSize: StateFlow<ImageSizeOption> = _selectedImageSize.asStateFlow()

    private val _imageUiState = MutableStateFlow<AiImageUiState>(AiImageUiState.Idle)
    val imageUiState: StateFlow<AiImageUiState> = _imageUiState.asStateFlow()

    val isLiveAiConnected: Boolean = aiRepository.isLiveFirebaseConnected

    val uiState: StateFlow<FinSmartUiState> = combine(
        repository.allTransactions,
        _selectedYearMonth,
        _advisorSliderPercent,
        _selectedAdvisorCategory,
        _snackbarMessage
    ) { allTx, selectedYm, sliderPct, chosenCat, snack ->
        buildUiState(allTx, selectedYm, sliderPct, chosenCat, snack)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinSmartUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureInitialDemoLoadedIfFirstLaunch()
        }
    }

    fun selectMonth(yearMonth: String) {
        _selectedYearMonth.value = yearMonth
        _selectedAdvisorCategory.value = null
    }

    fun stepMonth(delta: Int) {
        val months = FinSmartUiState.DEFAULT_MONTHS_2026
        val currentIdx = months.indexOf(_selectedYearMonth.value).takeIf { it >= 0 } ?: 10
        val nextIdx = (currentIdx + delta).coerceIn(0, months.lastIndex)
        selectMonth(months[nextIdx])
    }

    fun updateAdvisorSlider(percent: Int) {
        _advisorSliderPercent.value = percent.coerceIn(10, 50)
    }

    fun selectAdvisorCategory(categoryName: String?) {
        _selectedAdvisorCategory.value = categoryName
    }

    fun addTransaction(
        title: String,
        amount: Long,
        type: TransactionType,
        category: String,
        dateIso: String
    ) {
        if (title.isBlank() || amount <= 0L) return
        val ym = if (dateIso.length >= 7) dateIso.substring(0, 7) else _selectedYearMonth.value
        viewModelScope.launch {
            repository.addTransaction(
                FinancialTransaction(
                    title = title.trim(),
                    amount = amount,
                    type = type,
                    category = category,
                    dateIso = dateIso,
                    yearMonth = ym
                )
            )
            _selectedYearMonth.value = ym
            _snackbarMessage.value = "Transaksi \"${title.trim()}\" berhasil dicatat"
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            _snackbarMessage.value = "Transaksi dihapus"
        }
    }

    fun loadSixMonthDemo() {
        viewModelScope.launch {
            repository.loadSixMonthDemoData()
            _selectedYearMonth.value = "2026-11"
            _selectedAdvisorCategory.value = null
            _advisorSliderPercent.value = 30
            _snackbarMessage.value = "Data simulasi 6 bulan (Jun–Nov 2026) berhasil dimuat"
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            _selectedAdvisorCategory.value = null
            _snackbarMessage.value = "Semua data transaksi berhasil direset"
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun selectChatRole(role: AiChatRolePreset) {
        _chatRole.value = role
    }

    fun sendChatMessage(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || _isChatLoading.value) return
        val currentState = uiState.value
        val categoryDetails = if (currentState.categoryBreakdown.isNotEmpty()) {
            currentState.categoryBreakdown.joinToString("; ") { item ->
                "${item.category.name}: ${CurrencyFormatter.formatRupiah(item.totalAmount)} (${
                    String.format(java.util.Locale.US, "%.1f%%", item.percentageOfExpense)
                })"
            }
        } else {
            "Belum ada pengeluaran tercatat"
        }
        val contextSummary = buildString {
            appendLine("- Periode Aktif: ${CurrencyFormatter.formatYearMonthFull(currentState.selectedYearMonth)}")
            appendLine("- Total Pemasukan: ${CurrencyFormatter.formatRupiah(currentState.monthlyIncome)}")
            appendLine("- Total Pengeluaran: ${CurrencyFormatter.formatRupiah(currentState.monthlyExpense)}")
            appendLine("- Sisa Saldo / Tabungan: ${CurrencyFormatter.formatRupiah(currentState.monthlyBalance)}")
            append("- Rincian Pengeluaran per Kategori: $categoryDetails")
        }

        val userMsg = AiChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isUser = true,
            text = trimmed
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val result = aiRepository.sendChatMessage(
                history = _chatMessages.value,
                userMessage = trimmed,
                rolePreset = _chatRole.value,
                financialContextSummary = contextSummary
            )
            val replyText = when (result) {
                is AiTextResult.Placeholder -> result.reply
            }
            _chatMessages.value = _chatMessages.value + AiChatMessage(
                id = "m_${System.currentTimeMillis()}",
                isUser = false,
                text = replyText,
                isPlaceholder = !aiRepository.isLiveFirebaseConnected,
                roleLabel = _chatRole.value.title
            )
            _isChatLoading.value = false
        }
    }

    fun selectImageSize(size: ImageSizeOption) {
        _selectedImageSize.value = size
    }

    fun generateGoalImage(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return
        val size = _selectedImageSize.value
        _imageUiState.value = AiImageUiState.Loading
        viewModelScope.launch {
            when (val result = aiRepository.generateGoalImage(trimmed, size)) {
                is AiImageResult.Placeholder -> {
                    _imageUiState.value = AiImageUiState.PlaceholderPreview(
                        prompt = result.prompt,
                        size = result.size,
                        summary = result.summary,
                        notice = result.notice
                    )
                }
            }
        }
    }

    private fun buildUiState(
        allTransactions: List<FinancialTransaction>,
        selectedYearMonth: String,
        sliderPercent: Int,
        chosenAdvisorCategory: String?,
        snackbarMessage: String?
    ): FinSmartUiState {
        val monthlyTx = allTransactions.filter { it.yearMonth == selectedYearMonth }
        val monthlyIncome = monthlyTx
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val monthlyExpenses = monthlyTx.filter { it.type == TransactionType.EXPENSE }
        val monthlyExpense = monthlyExpenses.sumOf { it.amount }
        val monthlyBalance = monthlyIncome - monthlyExpense

        // Category breakdown for selected month
        val breakdown = monthlyExpenses
            .groupBy { it.category }
            .map { (catName, txs) ->
                val catTotal = txs.sumOf { it.amount }
                val pct = if (monthlyExpense > 0L) {
                    (catTotal.toFloat() / monthlyExpense.toFloat()) * 100f
                } else 0f
                CategoryBreakdownItem(
                    category = CategoryCatalog.findInfo(catName, TransactionType.EXPENSE),
                    totalAmount = catTotal,
                    percentageOfExpense = pct,
                    transactionCount = txs.size
                )
            }
            .sortedByDescending { it.totalAmount }

        // Build 6-month trend window ending on selectedYearMonth (or shifted if early in 2026)
        val allMonths = FinSmartUiState.DEFAULT_MONTHS_2026
        val selIdx = allMonths.indexOf(selectedYearMonth).takeIf { it >= 0 } ?: 10
        val windowStart = (selIdx - 5).coerceAtLeast(0)
        val windowEnd = (windowStart + 5).coerceAtMost(allMonths.lastIndex)
        val trendMonths = allMonths.subList(windowStart, windowEnd + 1)

        val trends = trendMonths.map { ym ->
            val txsInMonth = allTransactions.filter { it.yearMonth == ym }
            MonthlyTrendPoint(
                yearMonth = ym,
                shortLabel = CurrencyFormatter.formatYearMonthShort(ym),
                totalIncome = txsInMonth.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                totalExpense = txsInMonth.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            )
        }

        // Smart Saving Advisor analysis
        val topCategory = breakdown.firstOrNull()
        val topDiscretionary = breakdown.firstOrNull { it.category.isNonEssential } ?: topCategory
        val selectedTarget = chosenAdvisorCategory?.let { name ->
            breakdown.firstOrNull { it.category.name == name }
        } ?: topDiscretionary

        val savings = if (selectedTarget != null) {
            (selectedTarget.totalAmount * sliderPercent) / 100L
        } else 0L

        val advice = if (breakdown.isNotEmpty() && selectedTarget != null) {
            val headline = if (topDiscretionary != null) {
                "Potong pengeluaran ${selectedTarget.category.name} sebesar $sliderPercent% untuk menghemat ${CurrencyFormatter.formatRupiah(savings)} bulan depan!"
            } else {
                "Kurangi ${selectedTarget.category.name} sebesar $sliderPercent% untuk menghemat ${CurrencyFormatter.formatRupiah(savings)} bulan depan."
            }
            val tip = when (selectedTarget.category.name) {
                "Nongkrong & Hiburan" -> "Batasi frekuensi ngopi atau bioskop akhir pekan dan alihkan ke kegiatan gratis bersama teman."
                "Belanja Online" -> "Terapkan aturan tunggu 48 jam sebelum checkout keranjang agar terhindar dari pembelian impulsif."
                "Makan & Jajan" -> "Bawa bekal atau botol minum dari rumah minimal 3 hari seminggu untuk menekan biaya jajan."
                "Kuota & Langganan" -> "Evaluasi langganan streaming atau aplikasi yang jarang terpakai dan manfaatkan paket Wi-Fi kampus/sekolah."
                else -> "Catat batas maksimal mingguan untuk kategori ${selectedTarget.category.name} sebelum berbelanja."
            }
            SmartSavingAdvice(
                topCategory = topCategory,
                topDiscretionaryCategory = topDiscretionary,
                rankedExpenses = breakdown,
                sliderPercent = sliderPercent,
                selectedTargetCategory = selectedTarget,
                estimatedMonthlySavings = savings,
                estimatedSixMonthSavings = savings * 6L,
                projectedNewBalance = monthlyBalance + savings,
                headlineAdvice = headline,
                actionTip = tip
            )
        } else null

        return FinSmartUiState(
            selectedYearMonth = selectedYearMonth,
            availableMonths = allMonths,
            monthlyTransactions = monthlyTx,
            allTransactionsCount = allTransactions.size,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            monthlyBalance = monthlyBalance,
            categoryBreakdown = breakdown,
            monthlyTrends = trends,
            smartAdvice = advice,
            snackbarMessage = snackbarMessage
        )
    }
}
