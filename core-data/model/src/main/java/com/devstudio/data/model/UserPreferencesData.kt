package com.devstudio.data.model

data class UserPreferencesData(
    val theme: Theme,
    val selectedBookId: Long,
)

enum class Theme {
    LIGHT, DARK, SYSTEM
}