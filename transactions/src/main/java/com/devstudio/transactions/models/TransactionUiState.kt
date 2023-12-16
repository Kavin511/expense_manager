package com.devstudio.transactions.models

import com.devstudio.transactions.viewmodel.TransactionBook

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    data class Success(val data: TransactionBook) : TransactionUiState()
}