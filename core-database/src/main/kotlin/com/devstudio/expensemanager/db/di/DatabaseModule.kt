package com.devstudio.expensemanager.db.di

import android.content.Context
import androidx.room.Room
import com.devstudio.expensemanager.db.ExpenseManagerDataBase
import com.devstudio.expensemanager.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun providesTransactionDao(expenseManagerDataBase: ExpenseManagerDataBase): TransactionDao {
        return expenseManagerDataBase.transactionsDao()
    }

    @Provides
    fun providesExpenseManagerDatabase(@ApplicationContext applicationContext: Context): ExpenseManagerDataBase {
        return Room.databaseBuilder(
            applicationContext,
            ExpenseManagerDataBase::class.java,
            "expense_manager_database"
        ).allowMainThreadQueries().build()
    }
}