package com.devstudio.data.model

sealed class TransactionFilterType : Type() {
    data class DateRange(var additionalData: Pair<Long, Long>) : TransactionFilterType()
    object ALL : TransactionFilterType()
    object CurrentMonth : TransactionFilterType()
}
