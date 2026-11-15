package com.example.domain.model

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val indonesianLocale = Locale.forLanguageTag("id-ID")

    fun formatRupiah(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(indonesianLocale)
        val absValue = kotlin.math.abs(amount)
        val formatted = formatter.format(absValue)
        return if (amount < 0) "-Rp $formatted" else "Rp $formatted"
    }

    fun formatCompactRupiah(amount: Long): String {
        val absValue = kotlin.math.abs(amount)
        val prefix = if (amount < 0) "-Rp " else "Rp "
        return when {
            absValue >= 1_000_000L -> {
                val millions = absValue / 1_000_000.0
                val rounded = if (millions % 1.0 == 0.0) {
                    millions.toInt().toString()
                } else {
                    String.format(indonesianLocale, "%.1f", millions)
                }
                "${prefix}${rounded}jt"
            }
            absValue >= 1_000L -> "${prefix}${absValue / 1_000}rb"
            else -> "${prefix}${absValue}"
        }
    }

    private val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    private val shortMonthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    )

    fun formatYearMonthFull(yearMonth: String): String {
        val parts = yearMonth.split("-")
        if (parts.size != 2) return yearMonth
        val year = parts[0]
        val monthIdx = (parts[1].toIntOrNull() ?: 1).coerceIn(1, 12) - 1
        return "${monthNames[monthIdx]} $year"
    }

    fun formatYearMonthShort(yearMonth: String): String {
        val parts = yearMonth.split("-")
        if (parts.size != 2) return yearMonth
        val monthIdx = (parts[1].toIntOrNull() ?: 1).coerceIn(1, 12) - 1
        return shortMonthNames[monthIdx]
    }

    fun formatDateDisplay(dateIso: String): String {
        val parts = dateIso.split("-")
        if (parts.size != 3) return dateIso
        val year = parts[0]
        val monthIdx = (parts[1].toIntOrNull() ?: 1).coerceIn(1, 12) - 1
        val day = parts[2].toIntOrNull() ?: 1
        return "$day ${shortMonthNames[monthIdx]} $year"
    }
}
