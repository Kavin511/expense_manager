package com.example.expensemanager.db.dao

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update
import com.example.expensemanager.db.models.Transactions

@Dao
interface TransactionDao {
    @Query("SELECT * FROM  transactions_table order by transactionDate ASC")
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
}