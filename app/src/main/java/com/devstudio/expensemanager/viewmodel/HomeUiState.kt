package com.devstudio.expensemanager.viewmodel

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val data: HomeUiData) : HomeUiState
}