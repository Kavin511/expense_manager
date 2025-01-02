package com.devstudio.sharedmodule.importData.model

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf


data class TransactionField(
    val name: String,
    val description: String,
    var csvHeader: String = "",
    val selectedFieldIndex: MutableIntState = mutableIntStateOf(-1),
    val type: TransactionFieldType,
    var additionalInfo: String = "",
)

enum class TransactionFieldType {
    Note, Amount, TransactionMode, DATE, BookName, Category
}

