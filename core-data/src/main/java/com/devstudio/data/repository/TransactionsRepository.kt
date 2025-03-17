package com.devstudio.data.repository

import androidx.annotation.WorkerThread
import com.devstudio.database.ApplicationModule
import com.devstudio.database.models.Transaction
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.utils.TransactionMode
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import java.util.Calendar

interface TransactionsRepository {
    fun allTransactionsStream(bookId: Long): Flow<List<Transaction>>
    suspend fun findTransactionById(id: Long): Transaction?
    suspend fun deleteTransactions(transaction: Transaction)
    suspend fun upsertTransaction(transaction: Transaction): Boolean
    suspend fun insertTransactions(transaction: List<Transaction>): Int
    suspend fun updateTransaction(oldTransactionObject: Transaction)
    fun filterTransactionFromDateRange(
        dateRange: Pair<Long, Long>,
        bookId: Long,
    ): Flow<List<Transaction>>

    fun getTotalTransactionCount(): Int
    fun getCurrentMonthTransactionCount(): Int
    suspend fun getTransactionCategoryName(categoryId: String): String?
    fun getTotalAssets(): Double
    fun getTransactionsForCurrentMonth(selectedBookId: Long): Flow<List<Transaction>>
}

class TransactionsRepositoryImpl : TransactionsRepository, KoinComponent {
    val db = ApplicationModule.config.factory.getRoomInstance()
    private val transactionDao = db.transactionsDao()
    override fun getTotalAssets(): Double {
        return transactionDao.getTotalAssets(
            TransactionMode.EXPENSE.title,
            shouldUseBookId = false,
        ) + transactionDao.getTotalAssets(
            TransactionMode.INCOME.title,
            shouldUseBookId = false,
        ) + transactionDao.getTotalAssets(TransactionMode.INVESTMENT.title, shouldUseBookId = false)
    }

    override fun getTransactionsForCurrentMonth(selectedBookId: Long): Flow<List<Transaction>> {
        return transactionDao.getCurrentMonthTransaction(
            formatCurrentMonth(),
            DateFormatter.getCurrentYear(
                Calendar.getInstance().timeInMillis,
            ).toString(),
            shouldUseBookId = true,
            selectedBookId,
        )
    }

    override fun getTotalTransactionCount(): Int {
        return transactionDao.getTotalTransactionCount()
    }

    override fun getCurrentMonthTransactionCount(): Int {
        return transactionDao.getCurrentMonthTransactionCount(
            formatCurrentMonth(),
            DateFormatter.getCurrentYear(
                Calendar.getInstance().timeInMillis,
            ).toString(),
        )
    }

    override suspend fun getTransactionCategoryName(categoryId: String): String? {
        return transactionDao.getTransactionCategoryName(categoryId)
    }

    private fun formatCurrentMonth() = (
            "0" + (
                    DateFormatter.getCurrentMonth(
                        Calendar.getInstance().timeInMillis,
                    ) + 1
                    ).toString()
            ).takeLast(2)

    override fun allTransactionsStream(bookId: Long): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsStream(shouldUseBookId = true, bookId = bookId)
    }

    override suspend fun findTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    override suspend fun deleteTransactions(transaction: Transaction) {
        return transactionDao.deleteTransaction(transaction)
    }

    @WorkerThread
    override suspend fun upsertTransaction(transaction: Transaction): Boolean {
        try {
            transactionDao.upsertTransaction(transaction)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun insertTransactions(transaction: List<Transaction>): Int {
        try {
            return transactionDao.insertTransactions(transaction).size
        } catch (e: Exception) {
            return 0
        }
    }

    override suspend fun updateTransaction(oldTransactionObject: Transaction) {
        transactionDao.updateTransaction(oldTransactionObject)
    }

    override fun filterTransactionFromDateRange(
        dateRange: Pair<Long, Long>,
        bookId: Long,
    ): Flow<List<Transaction>> {
        return transactionDao.filterTransactionDateRange(
            dateRange.first,
            dateRange.second,
            shouldUseBookId = true,
            bookId = bookId,
        )
    }
}
