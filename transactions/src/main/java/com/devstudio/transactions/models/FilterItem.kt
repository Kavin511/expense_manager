package com.devstudio.transactions.models

import com.devstudio.data.model.TransactionFilterType


data class FilterItem(
    val id: Long = Math.random().toLong(),
    val name: String = "",
    var additionalData: Any?,
    val filterType: TransactionFilterType
)
