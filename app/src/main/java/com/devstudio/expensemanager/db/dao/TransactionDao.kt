package com.devstudio.expensemanager.db.dao

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import com.devstudio.expensemanager.db.models.Transactions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

@Dao
interface TransactionDao {
    @Query("SELECT * FROM  transactions_table order by id DESC")
    fun getAllTransactionsStream(): LiveData<List<Transactions>>

    @Query("SELECT * FROM transactions_table order by id DESC")
    fun getTransactions(): List<Transactions>

    @Insert(onConflict = IGNORE)
    suspend fun insertTransaction(transactionMode: Transactions)

    @WorkerThread
    @Query("SELECT * FROM transactions_table")
    fun getTransactionByMode(): LiveData<List<Transactions>>

    @Query("SELECT * FROM transactions_table WHERE id==:id")
    suspend fun getTransactionById(id: Long): Transactions?

    @Update
    suspend fun updateTransaction(transactions: Transactions)

    @Delete
    suspend fun deleteTransaction(transaction: Transactions)

    @Query("SELECT * FROM TRANSACTIONS_TABLE WHERE isEditingOldTransaction like :mode")
    fun getExpenseTransactionStream(mode: String = "EXPENSE"): Flow<List<Transactions>>

    @Query("SELECT * FROM TRANSACTIONS_TABLE WHERE isEditingOldTransaction like :mode")
    fun getIncomeTransactionStream(mode: String = "INCOME"): Flow<List<Transactions>>
}