package com.devstudio.data.model

data class UserPreferencesData(
    val theme: Theme,
    val selectedBookId: Long,
    val filterType: TransactionFilterType,
)

enum class Theme {
    LIGHT, DARK, SYSTEM_DEFAULT
}
