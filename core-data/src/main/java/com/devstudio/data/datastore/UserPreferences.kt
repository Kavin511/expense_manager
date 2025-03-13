package com.devstudio.data.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

data class UserPreferences(
    val theme: ThemeProto = ThemeProto.LIGHT,
    val selectedBookId: Long = 0,
    val filterType: FilterType = FilterType.ALL,
    val filterStartDate: String = "",
    val filterEndDate: String = ""
)

enum class ThemeProto {
    SYSTEM_DEFAULT, LIGHT, DARK,
}

enum class FilterType {
    CURRENT_MONTH, DATE_RANGE, ALL,
}

