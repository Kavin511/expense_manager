package com.devstudio.expensemanager.db.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.models.Transactions
import kotlinx.coroutines.flow.MutableStateFlow

class TransactionsRepository(private val transactionDao: TransactionDao) {
    val allTransactions = transactionDao.getAllTransaction()

    fun allExpenseTransactions(): LiveData<List<Transactions>> {
        return transactionDao.getAllTransaction()
    }

    suspend fun findTransactionById(id: Long): Transactions? {
        return transactionDao.getTransactionById(id)
    }

    suspend fun deleteTransactions(transactions: Transactions){
        return transactionDao.deleteTransaction(transactions)
    }

    @WorkerThread
    suspend fun insert(transactions: Transactions) {
        transactionDao.insertTransaction(transactions)
    }

    suspend fun updateTransaction(oldTransactionObject: Transactions) {
       transactionDao.updateTransaction(oldTransactionObject)
    }
}