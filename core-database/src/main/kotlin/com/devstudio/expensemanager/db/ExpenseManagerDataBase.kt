package com.devstudio.expensemanager.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.models.Transactions

@Database(entities = [Transactions::class], version = 1, exportSchema = false)
abstract class ExpenseManagerDataBase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionDao
}