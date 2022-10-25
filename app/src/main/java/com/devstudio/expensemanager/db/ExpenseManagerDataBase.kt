package com.devstudio.expensemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devstudio.expensemanager.db.dao.TransactionDao
import com.devstudio.expensemanager.db.models.Transactions

@Database(entities = arrayOf(Transactions::class), version = 1, exportSchema = false)
abstract class ExpenseManagerDataBase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionDao

    companion object {
        private var INSTANCE: ExpenseManagerDataBase? = null
        fun getDatabase(context: Context): ExpenseManagerDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseManagerDataBase::class.java,
                    "expense_manager_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}