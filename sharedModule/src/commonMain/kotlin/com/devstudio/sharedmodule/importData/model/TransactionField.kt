package com.devstudio.sharedmodule.importData.model

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.devstudio.sharedmodule.importData.model.MappingStatus.YetToMap


data class TransactionField(
    val name: String,
    val description: String,
    var csvHeader: String = "",
    val selectedFieldIndex: MutableIntState = mutableIntStateOf(-1),
    val type: TransactionFieldType,
    var additionalInfo: MutableList<MetaInformation>? = null,
    val mappingStatus: MutableState<MappingStatus> = mutableStateOf(YetToMap)
) {
    val isMapped: Boolean
        get() = mappingStatus.value is MappingStatus.Mapped
}

enum class TransactionFieldType {
    Note, Amount, TransactionModeField, DATE, BookName, Category
}

data class MetaInformation(val type: Any, var value: String)
