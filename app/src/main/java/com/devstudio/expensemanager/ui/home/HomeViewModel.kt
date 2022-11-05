package com.devstudio.expensemanager.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.devstudio.expensemanager.ExpenseManagerApplication
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.db.repository.TransactionsRepository
import com.devstudio.expensemanager.ui.transaction.viewmodels.TransactionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: TransactionsRepository

    init {
        repository = (application as com.devstudio.expensemanager.ExpenseManagerApplication).repository
    }

    suspend fun transactions(): LiveData<List<Transactions>> {
       return repository.allExpenseTransactions()
    }
    private val _text = MutableStateFlow("Click + to add transactions")

    val text: StateFlow<String> = _text

}
