package com.devstudio.sharedmodule.importData.model

import com.devstudio.database.models.Transaction

data class TransactionMapResult(
    val transactions: List<TransactionWithIndex>,
    val conflictTransactions: List<TransactionWithIndex>,
)
data class TransactionWithIndex(
    val index: Int,
    val transaction: Transaction,
)