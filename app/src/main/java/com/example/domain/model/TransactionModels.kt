package com.example.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.CatBelanjaColor
import com.example.ui.theme.CatIncomeColor
import com.example.ui.theme.CatKuotaColor
import com.example.ui.theme.CatLainnyaColor
import com.example.ui.theme.CatMakanColor
import com.example.ui.theme.CatNongkrongColor
import com.example.ui.theme.CatSekolahColor
import com.example.ui.theme.CatTransportColor
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SkyAdvisor

enum class TransactionType(val label: String) {
    INCOME("Pemasukan"),
    EXPENSE("Pengeluaran")
}

data class CategoryInfo(
    val name: String,
    val color: Color,
    val icon: ImageVector,
    val isNonEssential: Boolean
)

object CategoryCatalog {
    val expenseCategories: List<CategoryInfo> = listOf(
        CategoryInfo("Makan & Jajan", CatMakanColor, Icons.Rounded.Fastfood, isNonEssential = true),
        CategoryInfo("Nongkrong & Hiburan", CatNongkrongColor, Icons.Rounded.LocalCafe, isNonEssential = true),
        CategoryInfo("Transportasi & Bensin", CatTransportColor, Icons.Rounded.DirectionsCar, isNonEssential = false),
        CategoryInfo("Kuota & Langganan", CatKuotaColor, Icons.Rounded.Wifi, isNonEssential = true),
        CategoryInfo("Kebutuhan Sekolah/Tugas", CatSekolahColor, Icons.Rounded.School, isNonEssential = false),
        CategoryInfo("Belanja Online", CatBelanjaColor, Icons.Rounded.ShoppingBag, isNonEssential = true),
        CategoryInfo("Lainnya", CatLainnyaColor, Icons.Rounded.MoreHoriz, isNonEssential = false)
    )

    val incomeCategories: List<CategoryInfo> = listOf(
        CategoryInfo("Uang Saku", EmeraldPrimary, Icons.Rounded.AccountBalanceWallet, isNonEssential = false),
        CategoryInfo("Beasiswa/Prestasi", SkyAdvisor, Icons.Rounded.School, isNonEssential = false),
        CategoryInfo("Freelance/Part-time", AmberHighlight, Icons.Rounded.Work, isNonEssential = false),
        CategoryInfo("Hadiah/Lainnya", CatIncomeColor, Icons.Rounded.CardGiftcard, isNonEssential = false)
    )

    fun findInfo(categoryName: String, type: TransactionType): CategoryInfo {
        val pool = if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories
        return pool.firstOrNull { it.name == categoryName }
            ?: CategoryInfo(
                name = categoryName,
                color = if (type == TransactionType.EXPENSE) CatLainnyaColor else CatIncomeColor,
                icon = Icons.Rounded.MoreHoriz,
                isNonEssential = false
            )
    }
}

data class FinancialTransaction(
    val id: Long = 0L,
    val title: String,
    val amount: Long,
    val type: TransactionType,
    val category: String,
    val dateIso: String,
    val yearMonth: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class CategoryBreakdownItem(
    val category: CategoryInfo,
    val totalAmount: Long,
    val percentageOfExpense: Float,
    val transactionCount: Int
)

data class MonthlyTrendPoint(
    val yearMonth: String,
    val shortLabel: String,
    val totalIncome: Long,
    val totalExpense: Long
) {
    val balance: Long get() = totalIncome - totalExpense
}

data class SmartSavingAdvice(
    val topCategory: CategoryBreakdownItem?,
    val topDiscretionaryCategory: CategoryBreakdownItem?,
    val rankedExpenses: List<CategoryBreakdownItem>,
    val sliderPercent: Int,
    val selectedTargetCategory: CategoryBreakdownItem?,
    val estimatedMonthlySavings: Long,
    val estimatedSixMonthSavings: Long,
    val projectedNewBalance: Long,
    val headlineAdvice: String,
    val actionTip: String
)
