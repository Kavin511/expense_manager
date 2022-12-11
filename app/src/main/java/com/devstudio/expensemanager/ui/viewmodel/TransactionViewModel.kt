package com.devstudio.expensemanager.ui.transaction.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.db.repository.TransactionsRepository
import com.devstudio.expensemanager.model.TransactionMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionsRepository) : ViewModel() {
    val transaction = MutableStateFlow<Transactions?>(null)
    private val _cursorPosition = MutableStateFlow(0)
    val cursorPosition = _cursorPosition
    var transactionType = MutableStateFlow(TransactionMode.EXPENSE)
    var isEditingOldTransaction = MutableLiveData<Boolean>()
    private val _text = MutableStateFlow("Click + to add transactions")

    val text: StateFlow<String> = _text

    suspend fun insertTransaction(transaction: Transactions) {
        repository.insert(transaction)
    }

    suspend fun getAndUpdateTransactionById(id: Long) {
        transaction.value = repository.findTransactionById(id).also { isEditingOldTransaction.value = it != null }
    }

    suspend fun updateTransaction(oldTransactionObject: Transactions) {
        repository.updateTransaction(oldTransactionObject)
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            if (transaction.value != null) {
                repository.deleteTransactions(transaction.value!!)
            }
        }
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