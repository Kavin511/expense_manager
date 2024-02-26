package com.devstudio.expensemanager.presentation.home.model

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val data: HomeUiData) : HomeUiState
}
