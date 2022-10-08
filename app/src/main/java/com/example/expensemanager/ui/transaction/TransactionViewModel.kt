package com.example.expensemanager.ui.transaction

import androidx.lifecycle.ViewModel
import com.example.expensemanager.ui.transaction.models.TransactionMode
import kotlinx.coroutines.flow.MutableStateFlow

class TransactionViewModel() : ViewModel() {
    private val _cursorPosition = MutableStateFlow(0)
    val cursorPosition = _cursorPosition
    var transactionMode = MutableStateFlow(TransactionMode.EXPENSE)


}