package com.devstudio.core_data.repository

import androidx.annotation.WorkerThread
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.expensemanager.db.models.TransactionMode
import com.devstudio.utils.formatters.DateFormatter
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

interface TransactionsRepository {
    fun allTransactionsStream(bookId: Long): Flow<List<Transaction>>
    suspend fun findTransactionById(id: Long): Transaction?
    suspend fun deleteTransactions(transaction: Transaction)
    suspend fun upsertTransaction(transaction: Transaction)
    suspend fun updateTransaction(oldTransactionObject: Transaction)
    fun filterTransactionFromDateRange(
        dateRange: Pair<Long, Long>,
        bookId: Long
    ): Flow<List<Transaction>>

    fun getTotalTransactionCount(): Int
    fun getCurrentMonthTransactionCount(): Int
    suspend fun getTransactionCategoryName(categoryId: String): String?
    fun getTotalAssets(): Double
    fun getTransactionsForCurrentMonth(selectedBookId: Long): Flow<List<Transaction>>
}

@Singleton
class TransactionsRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    val userDataRepository: UserDataRepository
) :
    TransactionsRepository {
    override fun getTotalAssets(): Double {
        return transactionDao.getTotalAssets(TransactionMode.EXPENSE.name) + transactionDao.getTotalAssets(
            TransactionMode.INCOME.name
        )
    }

    override fun getTransactionsForCurrentMonth(selectedBookId: Long): Flow<List<Transaction>> {
        return transactionDao.getCurrentMonthTransaction(
            formatCurrentMonth(),
            DateFormatter.getCurrentYear(
                Calendar.getInstance()
            ).toString(),
            shouldUseBookId = true,
            selectedBookId
        )
    }

    override fun getTotalTransactionCount(): Int {
        return transactionDao.getTotalTransactionCount()
    }

    override fun getCurrentMonthTransactionCount(): Int {
        return transactionDao.getCurrentMonthTransactionCount(
            formatCurrentMonth(),
            DateFormatter.getCurrentYear(
                Calendar.getInstance()
            ).toString()
        )
    }

    override suspend fun getTransactionCategoryName(categoryId: String): String? {
        return transactionDao.getTransactionCategoryName(categoryId)
    }

    private fun formatCurrentMonth() = ("0" + (DateFormatter.getCurrentMonth(
        Calendar.getInstance()
    ) + 1).toString()).takeLast(2)

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
    override suspend fun upsertTransaction(transaction: Transaction) {
        transactionDao.upsertTransaction(transaction)
    }

    override suspend fun updateTransaction(oldTransactionObject: Transaction) {
        transactionDao.updateTransaction(oldTransactionObject)
    }

    override fun filterTransactionFromDateRange(
        dateRange: Pair<Long, Long>,
        bookId: Long
    ): Flow<List<Transaction>> {
        return transactionDao.filterTransactionDateRange(
            dateRange.first,
            dateRange.second,
            shouldUseBookId = true,
            bookId = bookId
        )
    }
}