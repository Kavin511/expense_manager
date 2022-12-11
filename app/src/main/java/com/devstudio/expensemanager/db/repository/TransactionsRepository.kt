package com.devstudio.expensemanager.db.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.models.Transactions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class TransactionsRepository(private val transactionDao: TransactionDao) {
    val transactions = transactionDao.getTransactions()

    fun allTransactionsStream(): LiveData<List<Transactions>> {
        return transactionDao.getAllTransactionsStream()
    }

    suspend fun findTransactionById(id: Long): Transactions? {
        return transactionDao.getTransactionById(id)
    }

    suspend fun deleteTransactions(transactions: Transactions){
        return transactionDao.deleteTransaction(transactions)
    }

    suspend fun getExpenseTransaction():Flow<List<Transactions>>{
        return transactionDao.getExpenseTransactionStream()
    }

    suspend fun getIncomeTransaction():Flow<List<Transactions>>{
        return transactionDao.getIncomeTransactionStream()
    }

    @WorkerThread
    suspend fun insert(transactions: Transactions) {
        transactionDao.insertTransaction(transactions)
    }

    suspend fun updateTransaction(oldTransactionObject: Transactions) {
       transactionDao.updateTransaction(oldTransactionObject)
    }
}