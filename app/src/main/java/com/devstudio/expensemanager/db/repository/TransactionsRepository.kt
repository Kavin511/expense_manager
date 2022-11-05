package com.devstudio.expensemanager.db.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.models.Transactions

class TransactionsRepository(private val transactionDao: TransactionDao) {
    val allTransactions = transactionDao.getAllTransaction()

    suspend fun allExpenseTransactions(): LiveData<List<Transactions>> {
        return transactionDao.getTransactionByMode()
    }

    suspend fun findTransactionById(id: Long): Transactions? {
        return transactionDao.getTransactionById(id)
    }
//
//    suspend fun allIncomeTransactions(): Flow<List<Transactions>> {
//        return transactionDao.getTransactionByMode("income")
//    }

    @WorkerThread
    suspend fun insert(transactions: Transactions) {
        transactionDao.insertTransaction(transactions)
    }

    suspend fun updateTransaction(oldTransactionObject: Transactions) {
       transactionDao.updateTransaction(oldTransactionObject)
    }
}