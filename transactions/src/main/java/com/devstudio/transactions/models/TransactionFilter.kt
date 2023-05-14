package com.devstudio.transactions.models


data class TransactionFilter(
    val id: Long = Math.random().toLong(),
    val name: String = "",
    var additionalData: Any?,
    val function: TransactionFilter.() -> Unit
)
