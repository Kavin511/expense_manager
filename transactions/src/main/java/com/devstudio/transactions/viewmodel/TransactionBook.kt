package com.devstudio.transactions.viewmodel

import com.devstudio.data.model.TransactionFilterType
import com.devstudio.expensemanager.db.models.Transaction
import kotlinx.coroutines.flow.Flow

data class TransactionBook(
    val bookId: Long,
    val transactions: Flow<List<Transaction>>,
    val bookName: String,
    val filterType: TransactionFilterType,
)
