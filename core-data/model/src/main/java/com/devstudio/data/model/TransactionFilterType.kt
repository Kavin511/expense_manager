package com.devstudio.data.model

sealed class TransactionFilterType : Type() {
    data class DATE_RANGE(var additionalData: Pair<Long, Long>) : TransactionFilterType()
    object ALL : TransactionFilterType()
    object CURRENT_MONTH : TransactionFilterType()
}