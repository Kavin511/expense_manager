package com.devstudio.transactions.models

import com.devstudio.transactions.viewmodel.TransactionFilterType


data class ListItem(
    val id: Long = Math.random().toLong(),
    val name: String = "",
    var additionalData: Any?,
    val filterType: TransactionFilterType
)
