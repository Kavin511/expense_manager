package com.devstudio.transactions.models

import com.devstudio.transactions.models.FilterItem

data class TransactionOptionsEvent(
    val shouldBookSelection: Boolean = false,
    val filterItem: FilterItem? = null
)