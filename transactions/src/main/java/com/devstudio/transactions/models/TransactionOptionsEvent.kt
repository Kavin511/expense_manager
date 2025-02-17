package com.devstudio.transactions.models

data class TransactionOptionsEvent(
    val shouldBookSelection: Boolean = false,
    val filterItem: FilterItem? = null,
)
