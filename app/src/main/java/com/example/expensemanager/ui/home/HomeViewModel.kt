package com.example.expensemanager.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensemanager.ExpenseManagerApplication
import com.example.expensemanager.db.models.Transactions
import com.example.expensemanager.db.repository.TransactionsRepository
import com.example.expensemanager.ui.transaction.viewmodels.TransactionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: TransactionsRepository

    init {
        repository = (application as ExpenseManagerApplication).repository
    }

    suspend fun transactions(): LiveData<List<Transactions>> {
       return repository.allExpenseTransactions()
    }
    private val _text = MutableStateFlow("Click + to add transactions")

    val text: StateFlow<String> = _text

}
