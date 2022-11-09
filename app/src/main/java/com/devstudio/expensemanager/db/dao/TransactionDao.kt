package com.devstudio.expensemanager.db.dao

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update
import com.devstudio.expensemanager.db.models.Transactions

@Dao
interface TransactionDao {
    @Query("SELECT * FROM  transactions_table order by transactionDate DESC")
    fun getAllTransaction(): LiveData<List<Transactions>>

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
}