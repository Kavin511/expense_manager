package com.example.expensemanager.ui.transaction.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensemanager.db.models.Transactions
import com.example.expensemanager.db.repository.TransactionsRepository
import com.example.expensemanager.ui.transaction.models.TransactionMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransactionViewModel(private val repository: TransactionsRepository) : ViewModel() {
    val transaction = MutableStateFlow<Transactions?>(null)
    private val _cursorPosition = MutableStateFlow(0)
    val cursorPosition = _cursorPosition
    var transactionType = MutableStateFlow(TransactionMode.EXPENSE)
    var isEditingOldTransaction = MutableLiveData<Boolean>()
    private val _text = MutableStateFlow("Click + to add transactions")

    val text: StateFlow<String> = _text
    val allTransactions: MutableStateFlow<List<Transactions>> =
        MutableStateFlow(repository.allTransactions.value ?: listOf())

    suspend fun insertTransaction(transaction: Transactions) {
        repository.insert(transaction)
    }

    suspend fun getTransactionById(id: Long) {
        transaction.value = repository.findTransactionById(id).also { isEditingOldTransaction.value = it != null }
    }

    suspend fun updateTransaction(oldTransactionObject: Transactions) {
        repository.updateTransaction(oldTransactionObject)
    }
}

internal class TransactionViewModelFactory(private val repository: TransactionsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}