package com.devstudio.transactions.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devstudio.core_data.TransactionsRepository
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.utils.model.TransactionMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val repository: TransactionsRepository) :
    ViewModel() {
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
        transaction.value =
            repository.findTransactionById(id).also { isEditingOldTransaction.value = it != null }
    }

    fun getTransactions(): Flow<List<Transactions>> {
       return repository.allTransactionsStream()
    }

    suspend fun updateTransaction(oldTransactionObject: Transactions) {
        repository.updateTransaction(oldTransactionObject)
    }

    fun deleteTransaction(transaction:Transactions) {
        viewModelScope.launch {
            repository.deleteTransactions(transaction)
        }
    }
}