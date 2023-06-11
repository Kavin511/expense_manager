package com.devstudio.expensemanager.db.dao

import androidx.annotation.WorkerThread
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import com.devstudio.expensemanager.db.models.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM  transactions_table order by transactionDate DESC")
    fun getAllTransactionsStream(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions_table order by transactionDate DESC")
    fun getTransactions(): List<Transaction>

    @Insert(onConflict = IGNORE)
    suspend fun insertTransaction(transactionMode: Transaction)

    @WorkerThread
    @Query("SELECT * FROM transactions_table")
    fun getTransactionByMode(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions_table WHERE id==:id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM TRANSACTIONS_TABLE WHERE isEditingOldTransaction like :mode")
    fun getExpenseTransactionStream(mode: String = "EXPENSE"): Flow<List<Transaction>>

    @Query("SELECT * FROM TRANSACTIONS_TABLE WHERE isEditingOldTransaction like :mode")
    fun getIncomeTransactionStream(mode: String = "INCOME"): Flow<List<Transaction>>

    @Query("SELECT * FROM TRANSACTIONS_TABLE WHERE transactionDate>= :startDate and transactionDate<=:endDate order by transactionDate desc")
    fun filterTransactionDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>?>

    @Query("SELECT * FROM TRANSACTIONS_TABLE WHERE strftime('%m',transactionDate / 1000,'unixepoch')=:month and strftime('%Y',transactionDate / 1000,'unixepoch')=:year order by transactionDate desc")
    fun getCurrentMonthTransaction(month: String, year: String): Flow<List<Transaction>>

    @Query("SELECT count(id) FROM TRANSACTIONS_TABLE")
    fun getTotalTransactionCount(): Int

    @Query("SELECT count(id) FROM TRANSACTIONS_TABLE WHERE strftime('%m',transactionDate / 1000,'unixepoch')=:month and strftime('%Y',transactionDate / 1000,'unixepoch')=:year order by transactionDate desc")
    fun getCurrentMonthTransactionCount(month: String, year: String):Int

    @Query("SELECT name FROM category_table WHERE id=:categoryId")
    fun getTransactionCategoryName(categoryId: Long): String?
}