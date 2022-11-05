package com.devstudio.expensemanager

import android.app.Application
import com.devstudio.expensemanager.db.ExpenseManagerDataBase
import com.devstudio.expensemanager.db.repository.TransactionsRepository

class ExpenseManagerApplication : Application() {
    private val database by lazy { ExpenseManagerDataBase.getDatabase(this) }
    val repository by lazy { TransactionsRepository(database.transactionsDao()) }
}