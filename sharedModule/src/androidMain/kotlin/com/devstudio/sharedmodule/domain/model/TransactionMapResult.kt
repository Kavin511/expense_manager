package com.devstudio.sharedmodule.domain.model

import com.devstudio.expensemanager.db.models.Transaction

data class TransactionMapResult(
    val transactions: List<Transaction>,
    val conflictTransactions: List<Transaction>,
)