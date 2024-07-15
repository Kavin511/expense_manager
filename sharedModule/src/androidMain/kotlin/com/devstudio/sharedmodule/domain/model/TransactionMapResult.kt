package com.devstudio.sharedmodule.domain.model

import com.devstudio.database.models.Transaction

data class TransactionMapResult(
    val transactions: List<Transaction>,
    val conflictTransactions: List<Transaction>,
)