package com.example.data.local

import android.content.Context
import com.example.domain.model.FinancialTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val dao: TransactionDao,
    context: Context
) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "finsmart_edu_prefs",
        Context.MODE_PRIVATE
    )

    val allTransactions: Flow<List<FinancialTransaction>> = dao.getAllTransactions()
        .map { entities -> entities.map { it.toDomain() } }

    suspend fun ensureInitialDemoLoadedIfFirstLaunch() {
        val initialized = prefs.getBoolean(KEY_INITIALIZED_DEMO, false)
        if (!initialized) {
            dao.insertAll(DemoDataGenerator.buildSixMonthDemoTransactions().map {
                TransactionEntity.fromDomain(it)
            })
            prefs.edit().putBoolean(KEY_INITIALIZED_DEMO, true).apply()
        }
    }

    suspend fun addTransaction(transaction: FinancialTransaction) {
        dao.insertTransaction(TransactionEntity.fromDomain(transaction))
    }

    suspend fun deleteTransaction(id: Long) {
        dao.deleteTransactionById(id)
    }

    suspend fun loadSixMonthDemoData() {
        dao.deleteAllTransactions()
        dao.insertAll(DemoDataGenerator.buildSixMonthDemoTransactions().map {
            TransactionEntity.fromDomain(it)
        })
        prefs.edit().putBoolean(KEY_INITIALIZED_DEMO, true).apply()
    }

    suspend fun resetAllData() {
        dao.deleteAllTransactions()
        prefs.edit().putBoolean(KEY_INITIALIZED_DEMO, true).apply()
    }

    companion object {
        private const val KEY_INITIALIZED_DEMO = "has_initialized_demo_v1"
    }
}
