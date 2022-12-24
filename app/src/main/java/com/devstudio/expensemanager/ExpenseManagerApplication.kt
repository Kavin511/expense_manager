package com.devstudio.expensemanager

import android.app.Application
import com.devstudio.expensemanager.db.ExpenseManagerDataBase
import com.devstudio.expensemanager.db.repository.TransactionsRepositoryImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExpenseManagerApplication : Application()