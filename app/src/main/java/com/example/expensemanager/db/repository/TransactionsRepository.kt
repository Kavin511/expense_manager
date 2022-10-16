package com.example.expensemanager.db.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.expensemanager.db.dao.TransactionDao
import com.example.expensemanager.db.models.Transactions

class TransactionsRepository(private val transactionDao: TransactionDao) {
   val allTransactions = transactionDao.getAllTransaction()

    suspend fun allExpenseTransactions(): List<Transactions> {
        return transactionDao.getTransactionByMode()
    }
//
//    suspend fun allIncomeTransactions(): Flow<List<Transactions>> {
//        return transactionDao.getTransactionByMode("income")
//    }

    @WorkerThread
    suspend fun insert(transactions: Transactions) {
        transactionDao.insertTransaction(transactions)
    }
}