package com.devstudio.expensemanager.ui.home

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.db.repository.TransactionsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    var repository: TransactionsRepository

    init {
        repository =
            (application as com.devstudio.expensemanager.ExpenseManagerApplication).repository
    }

    suspend fun transactions(): LiveData<List<Transactions>> {
            return repository.allExpenseTransactions()
    }

    fun deleteTransaction(transaction: Transactions){
        viewModelScope.launch {
            repository.deleteTransactions(transaction)
        }
    }

    fun getTransactions(): LiveData<List<Transactions>> {
        return repository.allExpenseTransactions()
    }

    private val _text = MutableStateFlow("Click + to add transactions")

    val text: StateFlow<String> = _text

}
