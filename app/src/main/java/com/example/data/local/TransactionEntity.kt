package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val amount: Long,
    val type: String,
    val category: String,
    val dateIso: String,
    val yearMonth: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): FinancialTransaction = FinancialTransaction(
        id = id,
        title = title,
        amount = amount,
        type = if (type == TransactionType.INCOME.name) TransactionType.INCOME else TransactionType.EXPENSE,
        category = category,
        dateIso = dateIso,
        yearMonth = yearMonth,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(tx: FinancialTransaction): TransactionEntity = TransactionEntity(
            id = tx.id,
            title = tx.title,
            amount = tx.amount,
            type = tx.type.name,
            category = tx.category,
            dateIso = tx.dateIso,
            yearMonth = tx.yearMonth,
            createdAt = tx.createdAt
        )
    }
}
