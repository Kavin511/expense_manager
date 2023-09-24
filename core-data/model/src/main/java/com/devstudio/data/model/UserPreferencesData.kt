package com.devstudio.core_model.models

data class UserPreferencesData(
    val theme: Theme,
    val selectedBookId: Long,
)

enum class Theme {
    LIGHT, DARK, SYSTEM
}