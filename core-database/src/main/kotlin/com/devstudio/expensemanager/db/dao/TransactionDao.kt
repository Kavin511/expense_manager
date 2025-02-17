package com.devstudio.expensemanager.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devstudio.expensemanager.db.models.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query(
        """SELECT * FROM  transactions_table 
        where case when :shouldUseBookId then bookId=:bookId else 1 end and case when :transactionMode!=null then isEditingOldTransaction=:transactionMode else 1 end
order by transactionDate DESC""",
    )
    fun getAllTransactionsStream(
        transactionMode: String? = null,
        shouldUseBookId: Boolean = false,
        bookId: Long = 0,
    ): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransaction(transactionMode: Transaction)

    @Query("SELECT * FROM transactions_table WHERE id==:id and case when :shouldUseBookId then bookId=:bookId else 1 end ")
    suspend fun getTransactionById(
        id: Long,
        shouldUseBookId: Boolean = false,
        bookId: Long = 0,
    ): Transaction?

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM TRANSACTIONS_TABLE WHERE transactionDate>= :startDate and transactionDate<=:endDate and case when :shouldUseBookId then bookId=:bookId else 1 end order by transactionDate desc")
    fun filterTransactionDateRange(
        startDate: Long,
        endDate: Long,
        shouldUseBookId: Boolean = false,
        bookId: Long = 0,
    ): Flow<List<Transaction>>

    @Query("SELECT * FROM TRANSACTIONS_TABLE WHERE strftime('%m',transactionDate / 1000,'unixepoch')=:month and strftime('%Y',transactionDate / 1000,'unixepoch')=:year and case when :shouldUseBookId then bookId=:bookId else 1 end  order by transactionDate desc")
    fun getCurrentMonthTransaction(
        month: String,
        year: String,
        shouldUseBookId: Boolean = false,
        bookId: Long = 0,
    ): Flow<List<Transaction>>

    @Query("SELECT count(id) FROM TRANSACTIONS_TABLE")
    fun getTotalTransactionCount(): Int

    @Query("SELECT count(id) FROM TRANSACTIONS_TABLE WHERE strftime('%m',transactionDate / 1000,'unixepoch')=:month and strftime('%Y',transactionDate / 1000,'unixepoch')=:year and case when :shouldUseBookId then bookId=:bookId else 1 end order by transactionDate desc")
    fun getCurrentMonthTransactionCount(
        month: String,
        year: String,
        shouldUseBookId: Boolean = false,
        bookId: Long = 0,
    ): Int

    @Query("SELECT name FROM category_table WHERE id=:categoryId and case when :shouldUseBookId then bookId=:bookId else 1 end ")
    fun getTransactionCategoryName(
        categoryId: String,
        shouldUseBookId: Boolean = false,
        bookId: Long = 0,
    ): String?

    @Query("SELECT SUM(amount) FROM TRANSACTIONS_TABLE WHERE isEditingOldTransaction=:mode and case when :shouldUseBookId then bookId=:bookId else 1 end ")
    fun getTotalAssets(
        mode: String,
        shouldUseBookId: Boolean = true,
        bookId: Long = 0,
    ): Double
}
