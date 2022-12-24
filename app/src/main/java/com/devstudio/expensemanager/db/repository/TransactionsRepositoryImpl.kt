package com.devstudio.expensemanager.db.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.models.Transactions
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TransactionsRepository {
    fun transactions(): List<Transactions>
    fun allTransactionsStream(): LiveData<List<Transactions>>
    suspend fun findTransactionById(id: Long): Transactions?
    suspend fun deleteTransactions(transactions: Transactions)
    suspend fun getExpenseTransaction(): Flow<List<Transactions>>
    suspend fun getIncomeTransaction(): Flow<List<Transactions>>
    suspend fun insert(transactions: Transactions)
    suspend fun updateTransaction(oldTransactionObject: Transactions)
}

@Singleton
class TransactionsRepositoryImpl @Inject constructor(private val transactionDao: TransactionDao):TransactionsRepository {
    override fun transactions(): List<Transactions> {
        return transactionDao.getTransactions()
    }

    override fun allTransactionsStream(): LiveData<List<Transactions>> {
        return transactionDao.getAllTransactionsStream()
    }

    override suspend fun findTransactionById(id: Long): Transactions? {
        return transactionDao.getTransactionById(id)
    }

    override suspend fun deleteTransactions(transactions: Transactions) {
        return transactionDao.deleteTransaction(transactions)
    }

    override suspend fun getExpenseTransaction(): Flow<List<Transactions>> {
        return transactionDao.getExpenseTransactionStream()
    }

    override suspend fun getIncomeTransaction(): Flow<List<Transactions>> {
        return transactionDao.getIncomeTransactionStream()
    }

    @WorkerThread
    override suspend fun insert(transactions: Transactions) {
        transactionDao.insertTransaction(transactions)
    }

    override suspend fun updateTransaction(oldTransactionObject: Transactions) {
        transactionDao.updateTransaction(oldTransactionObject)
    }
}