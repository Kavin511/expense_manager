package com.devstudio.expensemanager.presentation.transactionMainScreen.model

import com.devstudio.transactions.models.TransactionOptionsEvent

data class TransactionEvents(
    val booksEventCallback: (BookEvent) -> Unit,
    val filterEvent: (TransactionOptionsEvent) -> Unit,
)
