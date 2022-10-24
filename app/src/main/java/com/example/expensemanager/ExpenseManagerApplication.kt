package com.example.expensemanager

import android.app.Application
import com.example.expensemanager.db.ExpenseManagerDataBase
import com.example.expensemanager.db.repository.TransactionsRepository

class ExpenseManagerApplication : Application() {
    private val database by lazy { ExpenseManagerDataBase.getDatabase(this) }
    val repository by lazy { TransactionsRepository(database.transactionsDao()) }
}